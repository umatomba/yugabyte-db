package com.yugabyte.yw.models;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Singleton;

import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class AWSCloudMetadata implements CloudMetadata {

  @JsonProperty("AWS_ACCESS_KEY_ID")
  @ApiModelProperty
  public String awsAccessKeyID;

  @JsonProperty("AWS_SECRET_ACCESS_KEY")
  @ApiModelProperty
  public String awsAccessKeySecret;

  @JsonProperty("HOSTED_ZONE_ID")
  @ApiModelProperty
  public String awsHostedZoneId;

  private final String[] whiteListedEnvProperties = {"AWS_ACCESS_KEY_ID", "AWS_SECRET_ACCESS_KEY"};

  public void setConfig(Map<String, String> config) {
    this.awsAccessKeyID = config.get("AWS_ACCESS_KEY_ID");
    this.awsAccessKeySecret = config.get("AWS_SECRET_ACCESS_KEY");
    this.awsHostedZoneId = config.get("HOSTED_ZONE_ID");
  }

  public Map<String, String> getEnvVars() {
    Map<String, String> envVars = new HashMap<>();

    if (this.awsAccessKeyID != null) {
      envVars.put("AWS_ACCESS_KEY_ID", this.awsAccessKeyID);
      envVars.put("AWS_SECRET_ACCESS_KEY", this.awsAccessKeySecret);
    }
    return envVars;
  }
}
