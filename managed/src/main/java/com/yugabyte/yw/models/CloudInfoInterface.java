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

public interface CloudInfoInterface {

  public final ObjectMapper mapper = Json.mapper();

  public Map<String, String> getEnvVars() throws Exception;

  public Map<String, String> getConfigMapForUIOnlyAPIs(Map<String, String> config);

  public void maskSensitiveData();

  public static <T extends CloudInfoInterface> T getCloudProviderMetadata(Provider provider) {
    return getCloudProviderMetadata(provider, false);
  }

  public static <T extends CloudInfoInterface> T getCloudProviderMetadata(
      Provider provider, Boolean maskSensitiveData) {
    ProviderDetails providerDetails = provider.getUnmaskedProviderDetails();
    if (providerDetails == null) {
      return null;
    }
    ProviderDetails.CloudInfo cloudInfo = providerDetails.getCloudInfo();
    if (cloudInfo == null) {
      cloudInfo = new ProviderDetails.CloudInfo();
    }
    String providerType = provider.code;
    CloudType cloudType = CloudType.valueOf(providerType);
    switch (cloudType) {
      case aws:
        AWSCloudInfo awsCloudInfo = cloudInfo.getAws();
        if (awsCloudInfo != null && maskSensitiveData) {
          awsCloudInfo.maskSensitiveData();
        }
        return (T) awsCloudInfo;
      case gcp:
        GCPCloudInfo gcpCloudInfo = cloudInfo.getGcp();
        if (gcpCloudInfo != null && maskSensitiveData) {
          gcpCloudInfo.maskSensitiveData();
        }
        return (T) gcpCloudInfo;
      case azu:
        AzureCloudInfo azuCloudInfo = cloudInfo.getAzu();
        if (azuCloudInfo != null && maskSensitiveData) {
          azuCloudInfo.maskSensitiveData();
        }
        return (T) azuCloudInfo;
      case kubernetes:
        KubernetesInfo kubernetesInfo = cloudInfo.getKubernetes();
        if (kubernetesInfo != null && maskSensitiveData) {
          kubernetesInfo.maskSensitiveData();
        }
        return (T) kubernetesInfo;
      case onprem:
        OnPremCloudInfo onpremCloudInfo = cloudInfo.getOnprem();
        if (onpremCloudInfo != null && maskSensitiveData) {
          onpremCloudInfo.maskSensitiveData();
        }
        return (T) onpremCloudInfo;
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
    ProviderDetails.CloudInfo cloudInfo = providerDetails.getCloudInfo();
    if (cloudInfo == null) {
      cloudInfo = new ProviderDetails.CloudInfo();
      providerDetails.setCloudInfo(cloudInfo);
    }
    CloudType cloudType = CloudType.valueOf(provider.code);
    switch (cloudType) {
      case aws:
        AWSCloudInfo awsCloudInfo = mapper.convertValue(config, AWSCloudInfo.class);
        cloudInfo.setAws(awsCloudInfo);
        break;
      case gcp:
        GCPCloudInfo gcpCloudInfo = mapper.convertValue(config, GCPCloudInfo.class);
        cloudInfo.setGcp(gcpCloudInfo);
        break;
      case azu:
        AzureCloudInfo azuCloudInfo = mapper.convertValue(config, AzureCloudInfo.class);
        cloudInfo.setAzu(azuCloudInfo);
        break;
      case kubernetes:
        KubernetesInfo kubernetesInfo =
            mapper.convertValue(config, KubernetesInfo.class);
        cloudInfo.setKubernetes(kubernetesInfo);
        break;
      case onprem:
        OnPremCloudInfo onPremCloudInfo =
            mapper.convertValue(config, OnPremCloudInfo.class);
        cloudInfo.setOnprem(onPremCloudInfo);
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
        ObjectNode cloudInfo = mapper.createObjectNode();
        ObjectNode gcpCloudInfo = mapper.createObjectNode();

        Boolean shouldUseHostCredentials =
            config.has("use_host_credentials") && config.get("use_host_credentials").asBoolean();
        gcpCloudInfo.set("host_project_id", config.get("host_project_id"));
        if (!shouldUseHostCredentials && config.has("config_file_contents")) {
          gcpCloudInfo.put(
              "config_file_contents", config.get("config_file_contents").toString());
        }
        if (config.has("use_host_vpc")) {
          gcpCloudInfo.set("use_host_vpc", config.get("use_host_vpc"));
        }
        gcpCloudInfo.set("YB_FIREWALL_TAGS", config.get("YB_FIREWALL_TAGS"));

        cloudInfo.set("gcp", gcpCloudInfo);
        details.set("cloudInfo", cloudInfo);
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
    ProviderDetails.CloudInfo cloudInfo = providerDetails.getCloudInfo();
    if (cloudInfo == null) {
      return;
    }
    CloudType cloudType = CloudType.valueOf(p.code);
    CloudInfoInterface cloudInfoInterface = null;
    switch (cloudType) {
      case aws:
        cloudInfoInterface = cloudInfo.getAws();
        break;
      case gcp:
        cloudInfoInterface = cloudInfo.getGcp();
        break;
      case azu:
        cloudInfoInterface = cloudInfo.getAzu();
        break;
      case kubernetes:
        cloudInfoInterface = cloudInfo.getKubernetes();
        break;
      case onprem:
        cloudInfoInterface = cloudInfo.getOnprem();
        break;
      case local:
        // Import Universe case
      default:
        break;
    }
    if (cloudInfoInterface == null) {
      return;
    }
    config = cloudInfoInterface.getConfigMapForUIOnlyAPIs(config);
    p.config = maskConfigNew(config);
  }
}
