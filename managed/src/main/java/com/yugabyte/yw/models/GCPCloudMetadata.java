package com.yugabyte.yw.models;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Singleton;
import com.yugabyte.yw.cloud.gcp.GCPCloudImpl;
import com.yugabyte.yw.controllers.handlers.CloudProviderHandler;

import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class GCPCloudMetadata implements CloudMetadata {

  @JsonProperty("GCE_PROJECT")
  @ApiModelProperty
  public String gceProject;

  @JsonProperty("GCE_EMAIL")
  @ApiModelProperty
  public String gceEmail;

  @JsonProperty("GOOGLE_APPLICATION_CREDENTIALS")
  @ApiModelProperty
  public String googleApplicationCredentials;

  @JsonProperty("CUSTOM_GCE_NETWORK")
  @ApiModelProperty
  public String customGceNetwork;

  @JsonProperty("GCP_HOST_PROJECT")
  @ApiModelProperty
  public String gceHostProject;

  // TODO: cleanup some fields.
  @ApiModelProperty public String ybFirewallTags;
  @ApiModelProperty public String network;
  @ApiModelProperty public String project_id;

  private final String[] whiteListedEnvProperties = {
    "YB_FIREWALL_TAGS", GCPCloudImpl.GCE_PROJECT_PROPERTY, GCPCloudImpl.GCE_HOST_PROJECT_PROPERTY
  };

  public void setConfig(Map<String, String> config) {
    this.gceProject = config.get(GCPCloudImpl.PROJECT_ID_PROPERTY);
    this.gceEmail = config.get(GCPCloudImpl.GCE_EMAIL_PROPERTY);
    this.googleApplicationCredentials =
        config.get(GCPCloudImpl.GOOGLE_APPLICATION_CREDENTIALS_PROPERTY);
    this.customGceNetwork = config.get(GCPCloudImpl.CUSTOM_GCE_NETWORK_PROPERTY);
    this.gceHostProject = config.get(GCPCloudImpl.GCE_HOST_PROJECT_PROPERTY);
    if (config.containsKey(CloudProviderHandler.YB_FIREWALL_TAGS)) {
      this.ybFirewallTags = config.get(CloudProviderHandler.YB_FIREWALL_TAGS);
    }
    this.network = config.get("network");
    this.project_id = config.get("project_id");
  }

  public Map<String, String> getEnvVars() {
    Map<String, String> envVars = new HashMap<>();

    if (this.gceProject != null) {
      envVars.put(GCPCloudImpl.GCE_HOST_PROJECT_PROPERTY, this.gceHostProject);
      envVars.put(CloudProviderHandler.YB_FIREWALL_TAGS, this.ybFirewallTags);
      envVars.put(GCPCloudImpl.GCE_PROJECT_PROPERTY, this.gceProject);
    }

    return envVars;
  }
}
