package com.yugabyte.yw.models;

import static com.yugabyte.yw.models.helpers.CommonUtils.maskConfigNew;
import static play.mvc.Http.Status.BAD_REQUEST;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yugabyte.yw.commissioner.Common.CloudType;
import com.yugabyte.yw.common.PlatformServiceException;

import play.libs.Json;

public interface CloudMetadata {

  public final ObjectMapper mapper = Json.mapper();

  public Map<String, String> getEnvVars() throws Exception;

  public Map<String, String> getConfigMapForUIOnlyAPIs(Map<String, String> config);

  public void maskSensitiveData();

  public static <T extends CloudMetadata> T getCloudProviderMetadata(Provider provider) {
    return getCloudProviderMetadata(provider, false);
  }

  public static <T extends CloudMetadata> T getCloudProviderMetadata(
      Provider provider, Boolean maskSensitiveData) {
    ProviderDetails providerDetails = provider.getUnmaskedProviderDetails();
    if (providerDetails == null) {
      return null;
    }
    String providerType = provider.code;
    CloudType cloudType = CloudType.valueOf(providerType);
    switch (cloudType) {
      case aws:
        AWSCloudMetadata awsMetadata = providerDetails.awsCloudMetadata;
        if (maskSensitiveData) {
          awsMetadata.maskSensitiveData();
        }
        return (T) awsMetadata;
      case gcp:
        GCPCloudMetadata gcpMetadata = providerDetails.gcpCloudMetadata;
        if (maskSensitiveData) {
          gcpMetadata.maskSensitiveData();
        }
        return (T) gcpMetadata;
      case azu:
        AzureCloudMetadata azuMetadata = providerDetails.azureCloudMetadata;
        if (maskSensitiveData) {
          azuMetadata.maskSensitiveData();
        }
        return (T) azuMetadata;
      case kubernetes:
        KubernetesMetadata kubernetesMetadata = providerDetails.kubernetesCloudMetadata;
        if (maskSensitiveData) {
          kubernetesMetadata.maskSensitiveData();
        }
        return (T) kubernetesMetadata;
      case onprem:
        OnPremCloudMetadata onPremMetadata = providerDetails.onPremCloudMetadata;
        if (maskSensitiveData) {
          onPremMetadata.maskSensitiveData();
        }
        return (T) onPremMetadata;
      case local:
        // Import Universe case
        return null;
      default:
        throw new PlatformServiceException(BAD_REQUEST, "Unsupported cloud type");
    }
  }

  public static void maskProviderDetails(Provider provider) {
    getCloudProviderMetadata(provider, true);
  }

  public static void setCloudProviderMetadataFromConfig(
      Provider provider, Map<String, String> config) {
    ProviderDetails providerDetails = provider.getUnmaskedProviderDetails();
    CloudType cloudType = CloudType.valueOf(provider.code);
    switch (cloudType) {
      case aws:
        AWSCloudMetadata awsCloudMetadata = mapper.convertValue(config, AWSCloudMetadata.class);
        providerDetails.setAwsCloudMetadata(awsCloudMetadata);
        break;
      case gcp:
        GCPCloudMetadata gcpCloudMetadata = mapper.convertValue(config, GCPCloudMetadata.class);
        providerDetails.setGcpCloudMetadata(gcpCloudMetadata);
        break;
      case azu:
        AzureCloudMetadata azuCloudMetadata = mapper.convertValue(config, AzureCloudMetadata.class);
        providerDetails.setAzureCloudMetadata(azuCloudMetadata);
        break;
      case kubernetes:
        KubernetesMetadata kubernetesMetadata =
            mapper.convertValue(config, KubernetesMetadata.class);
        providerDetails.setKubernetesCloudMetadata(kubernetesMetadata);
        break;
      case onprem:
        OnPremCloudMetadata onPremCloudMetadata =
            mapper.convertValue(config, OnPremCloudMetadata.class);
        providerDetails.setOnPremCloudMetadata(onPremCloudMetadata);
        break;
      case local:
        // Import Universe case
        break;
      default:
        throw new PlatformServiceException(BAD_REQUEST, "Unsupported cloud type");
    }
  }

  public static JsonNode mayBeMassageRequest(JsonNode requestBody) {
    // For Backward Compatiblity support.
    JsonNode config = requestBody.get("config");
    ObjectNode reqBody = (ObjectNode) requestBody;
    // Confirm we had a "config" key and it was not null.
    if (config != null && !config.isNull()) {
      if (requestBody.get("code").asText().equals(CloudType.gcp.name())) {
        ObjectNode details = mapper.createObjectNode();
        ObjectNode gcpCloudMetadata = mapper.createObjectNode();

        Boolean shouldUseHostCredentials =
            config.has("use_host_credentials") && config.get("use_host_credentials").asBoolean();
        gcpCloudMetadata.set("host_project_id", config.get("host_project_id"));
        if (!shouldUseHostCredentials && config.has("config_file_contents")) {
          gcpCloudMetadata.put(
              "config_file_contents", config.get("config_file_contents").toString());
        }
        if (config.has("use_host_vpc")) {
          gcpCloudMetadata.set("use_host_vpc", config.get("use_host_vpc"));
        }
        gcpCloudMetadata.set("YB_FIREWALL_TAGS", config.get("YB_FIREWALL_TAGS"));

        details.set("gcpCloudMetadata", gcpCloudMetadata);
        details.set("airGapInstall", config.get("airGapInstall"));

        reqBody.set("details", details);
        reqBody.remove("config");
      }
    }
    return reqBody;
  }

  public static void mayBeMassageResponse(Provider p) {
    Map<String, String> config = p.getUnmaskedConfig();
    ProviderDetails providerDetails = p.getUnmaskedProviderDetails();
    CloudType cloudType = CloudType.valueOf(p.code);
    switch (cloudType) {
      case aws:
        config = providerDetails.awsCloudMetadata.getConfigMapForUIOnlyAPIs(config);
        break;
      case gcp:
        config = providerDetails.gcpCloudMetadata.getConfigMapForUIOnlyAPIs(config);
        break;
      case azu:
        config = providerDetails.azureCloudMetadata.getConfigMapForUIOnlyAPIs(config);
        break;
      case kubernetes:
        config = providerDetails.kubernetesCloudMetadata.getConfigMapForUIOnlyAPIs(config);
        break;
      case onprem:
        config = providerDetails.onPremCloudMetadata.getConfigMapForUIOnlyAPIs(config);
        break;
      case local:
        // Import Universe case
      default:
        break;
    }
    p.config = maskConfigNew(config);
  }
}
