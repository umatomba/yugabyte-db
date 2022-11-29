package com.yugabyte.yw.models;

import static play.mvc.Http.Status.BAD_REQUEST;

import java.util.Map;

import com.yugabyte.yw.commissioner.Common;
import com.yugabyte.yw.common.PlatformServiceException;

import play.api.Play;

public interface CloudMetadata {

  public void setConfig(Map<String, String> config) throws Exception;

  public Map<String, String> getEnvVars() throws Exception;

  public static <T extends CloudMetadata> T getCloudProvider(String configType) {
    if (configType.equals(Common.CloudType.aws.toString())) {
      return (T) Play.current().injector().instanceOf(AWSCloudMetadata.class);
    } else if (configType.equals(Common.CloudType.gcp.toString())) {
      return (T) Play.current().injector().instanceOf(GCPCloudMetadata.class);
    }
    throw new PlatformServiceException(BAD_REQUEST, "Unsupported cloud type");
  }
}
