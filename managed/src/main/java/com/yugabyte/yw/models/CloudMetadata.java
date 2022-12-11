package com.yugabyte.yw.models;

import static play.mvc.Http.Status.BAD_REQUEST;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yugabyte.yw.commissioner.Common.CloudType;
import com.yugabyte.yw.common.PlatformServiceException;

public interface CloudMetadata {

  public final ObjectMapper mapper = new ObjectMapper();

  public Map<String, String> getEnvVars() throws Exception;

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
      default:
        throw new PlatformServiceException(BAD_REQUEST, "Unsupported cloud type");
    }
  }
}
