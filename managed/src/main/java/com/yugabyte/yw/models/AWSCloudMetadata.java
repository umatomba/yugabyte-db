package com.yugabyte.yw.models;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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

  @JsonProperty("HOSTED_ZONE_NAME")
  @ApiModelProperty
  public String awsHostedZoneName;

  @JsonIgnore
  public Map<String, String> getEnvVars() {
    Map<String, String> envVars = new HashMap<>();

    if (this.awsAccessKeyID != null) {
      envVars.put("AWS_ACCESS_KEY_ID", this.awsAccessKeyID);
      envVars.put("AWS_SECRET_ACCESS_KEY", this.awsAccessKeySecret);
    }
    return envVars;
  }

  @JsonIgnore
  public void updateCloudMetadataDetails(Map<String, String> configData) {
    // pass
  }
}
