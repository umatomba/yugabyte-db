package com.yugabyte.yw.models;

import static play.mvc.Http.Status.BAD_REQUEST;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yugabyte.yw.commissioner.Common.CloudType;
import com.yugabyte.yw.common.PlatformServiceException;

public interface CloudMetadata {

  public final ObjectMapper mapper = new ObjectMapper();

  public final String[] gcpExtraConfigKeys = {
    "type", "auth_uri", "token_uri", "auth_provider_x509_cert_url", "client_x509_cert_url",
    "client_id", "client_email", "GCE_EMAIL", "private_key", "private_key_id", "use_host_credentials"
  };

  public Map<String, String> getEnvVars() throws Exception;

  public void updateCloudMetadataDetails(Map<String, String> configData) throws Exception;

  public static <T extends CloudMetadata> T getCloudProvider(
      String configType, Map<String, String> configData) {
    CloudType cloudType = CloudType.valueOf(configType);
    switch (cloudType) {
      case aws:
        AWSCloudMetadata awsCloudMetadata = mapper.convertValue(configData, AWSCloudMetadata.class);
        return (T) awsCloudMetadata;
      case gcp:
        Map<String, String> newConfig = new HashMap<>(configData);
        for (String key: gcpExtraConfigKeys) {
          newConfig.remove(key);
        }
        GCPCloudMetadata gcpCloudMetadata = mapper.convertValue(newConfig, GCPCloudMetadata.class);
        return (T) gcpCloudMetadata;
      case azu:
        AzureCloudMetadata azuCloudMetadata =
            mapper.convertValue(configData, AzureCloudMetadata.class);
        return (T) azuCloudMetadata;
      case kubernetes:
        KubernetesMetadata kubernetesMetadata =
            mapper.convertValue(configData, KubernetesMetadata.class);
        return (T) kubernetesMetadata;
      case onprem:
        OnPremCloudMetadata onPremCloudMetadata =
            mapper.convertValue(configData, OnPremCloudMetadata.class);
        return (T) onPremCloudMetadata;
      default:
        throw new PlatformServiceException(BAD_REQUEST, "Unsupported cloud type");
    }
  }
}
