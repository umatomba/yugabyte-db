package com.yugabyte.yw.models;

import static play.mvc.Http.Status.BAD_REQUEST;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yugabyte.yw.commissioner.Common.CloudType;
import com.yugabyte.yw.common.PlatformServiceException;

public interface CloudMetadata {

  public final ObjectMapper mapper = new ObjectMapper();

  public Map<String, String> getEnvVars() throws Exception;

  public void updateCloudMetadataDetails(String key, String value) throws Exception;

  public static <T extends CloudMetadata> T getCloudProvider(String configType, Map<String, String> configData) {
    CloudType cloudType = CloudType.valueOf(configType);
    switch (cloudType) {
      case aws:
        AWSCloudMetadata awsCloudMetadata = mapper.convertValue(configData, AWSCloudMetadata.class);
        return (T) awsCloudMetadata;
      case gcp:
        GCPCloudMetadata gcpCloudMetadata = mapper.convertValue(configData, GCPCloudMetadata.class);
        return (T) gcpCloudMetadata;
      case azu:
        AzureCloudMetadata azuCloudMetadata = mapper.convertValue(configData, AzureCloudMetadata.class);
        return (T) azuCloudMetadata;
      default:
        throw new PlatformServiceException(BAD_REQUEST, "Unsupported cloud type");
    }
  }
}
