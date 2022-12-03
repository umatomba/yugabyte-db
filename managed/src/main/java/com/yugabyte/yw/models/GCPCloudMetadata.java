package com.yugabyte.yw.models;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yugabyte.yw.cloud.gcp.GCPCloudImpl;
import com.yugabyte.yw.controllers.handlers.CloudProviderHandler;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GCPCloudMetadata implements CloudMetadata {

  @JsonProperty(GCPCloudImpl.GCE_PROJECT_PROPERTY)
  @ApiModelProperty
  public String gceProject;

  @JsonProperty(GCPCloudImpl.GOOGLE_APPLICATION_CREDENTIALS_PROPERTY)
  @ApiModelProperty
  public String googleApplicationCredentials;

  @JsonProperty(GCPCloudImpl.CUSTOM_GCE_NETWORK_PROPERTY)
  @ApiModelProperty
  public String customGceNetwork;

  @JsonProperty(GCPCloudImpl.GCE_HOST_PROJECT_PROPERTY)
  @ApiModelProperty
  public String gceHostProject;

  @JsonProperty(CloudProviderHandler.YB_FIREWALL_TAGS)
  @ApiModelProperty
  public String ybFirewallTags;

  @JsonProperty("use_host_vpc")
  @ApiModelProperty
  public String useHostVPC;

  @JsonProperty("project_id")
  @ApiModelProperty
  public String projectId;

  @JsonIgnore
  public Map<String, String> getEnvVars() {
    Map<String, String> envVars = new HashMap<>();

    if (this.gceProject != null) {
      envVars.put(GCPCloudImpl.GCE_HOST_PROJECT_PROPERTY, this.gceHostProject);
      envVars.put(CloudProviderHandler.YB_FIREWALL_TAGS, this.ybFirewallTags);
      envVars.put(GCPCloudImpl.GCE_PROJECT_PROPERTY, this.gceProject);
      envVars.put(
          GCPCloudImpl.GOOGLE_APPLICATION_CREDENTIALS_PROPERTY, this.googleApplicationCredentials);
    }

    return envVars;
  }

  @JsonIgnore
  public void updateCloudMetadataDetails(Map<String, String> configMap) {
    // pass
  }
}
