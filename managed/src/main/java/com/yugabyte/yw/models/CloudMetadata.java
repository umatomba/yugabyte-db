package com.yugabyte.yw.models;

import static com.yugabyte.yw.models.helpers.CommonUtils.maskConfigNew;
import static play.mvc.Http.Status.BAD_REQUEST;

import java.util.HashMap;
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

  public Map<String, String> getConfigKeyMap();

  public static <T extends CloudMetadata> T getCloudProviderMetadata(Provider provider) {
    if (provider.details == null) {
      return null;
    }
    String providerType = provider.code;
    CloudType cloudType = CloudType.valueOf(providerType);
    switch (cloudType) {
      case aws:
        AWSCloudMetadata awsMetadata = provider.details.awsCloudMetadata;
        return (T) awsMetadata;
      case gcp:
        GCPCloudMetadata gcpMetadata = provider.details.gcpCloudMetadata;
        return (T) gcpMetadata;
      case azu:
        AzureCloudMetadata azuMetadata = provider.details.azureCloudMetadata;
        return (T) azuMetadata;
      case kubernetes:
        KubernetesMetadata kubernetesMetadata = provider.details.kubernetesCloudMetadata;
        return (T) kubernetesMetadata;
      case onprem:
        OnPremCloudMetadata onPremMetadata = provider.details.onPremCloudMetadata;
        return (T) onPremMetadata;
      case local:
        // Import Universe case
        return null;
      default:
        throw new PlatformServiceException(BAD_REQUEST, "Unsupported cloud type");
    }
  }

  public static void setCloudProviderMetadataFromConfig(
      Provider provider, Map<String, String> config) {
    CloudType cloudType = CloudType.valueOf(provider.code);
    switch (cloudType) {
      case aws:
        AWSCloudMetadata awsCloudMetadata = mapper.convertValue(config, AWSCloudMetadata.class);
        provider.details.setAwsCloudMetadata(awsCloudMetadata);
        break;
      case gcp:
        GCPCloudMetadata gcpCloudMetadata = mapper.convertValue(config, GCPCloudMetadata.class);
        provider.details.setGcpCloudMetadata(gcpCloudMetadata);
        break;
      case azu:
        AzureCloudMetadata azuCloudMetadata = mapper.convertValue(config, AzureCloudMetadata.class);
        provider.details.setAzureCloudMetadata(azuCloudMetadata);
        break;
      case kubernetes:
        KubernetesMetadata kubernetesMetadata =
            mapper.convertValue(config, KubernetesMetadata.class);
        provider.details.setKubernetesCloudMetadata(kubernetesMetadata);
        break;
      case onprem:
        OnPremCloudMetadata onPremCloudMetadata =
            mapper.convertValue(config, OnPremCloudMetadata.class);
        provider.details.setOnPremCloudMetadata(onPremCloudMetadata);
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
    Map<String, String> configKeyMap = new HashMap<>();
    CloudType cloudType = CloudType.valueOf(p.code);
    switch (cloudType) {
      case aws:
        configKeyMap = p.details.awsCloudMetadata.getConfigKeyMap();
        break;
      case gcp:
        configKeyMap = p.details.gcpCloudMetadata.getConfigKeyMap();
        break;
      case azu:
        configKeyMap = p.details.azureCloudMetadata.getConfigKeyMap();
        break;
      case kubernetes:
        configKeyMap = p.details.kubernetesCloudMetadata.getConfigKeyMap();
        break;
      case onprem:
        configKeyMap = p.details.onPremCloudMetadata.getConfigKeyMap();
        break;
      case local:
        // Import Universe case
      default:
        break;
    }
    for (Map.Entry<String, String> entry : configKeyMap.entrySet()) {
      if (config.get(entry.getKey()) != null) {
        config.put(entry.getValue(), config.get(entry.getKey()));
      }
    }
    p.config = maskConfigNew(config);
  }
}
