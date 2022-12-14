package com.yugabyte.yw.models;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.yugabyte.yw.cloud.gcp.GCPCloudImpl;
import com.yugabyte.yw.controllers.handlers.CloudProviderHandler;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GCPCloudMetadata implements CloudMetadata {

  @JsonProperty("gce_project")
  @ApiModelProperty
  public String gceProject;

  @JsonProperty("gce_credential_file_path")
  public String gceApplicationCredentialsPath;

  @JsonProperty("gce_credential")
  public JsonNode gceApplicationCredentials;

  @JsonProperty(GCPCloudImpl.CUSTOM_GCE_NETWORK_PROPERTY)
  @ApiModelProperty
  public String customGceNetwork;

  // TO_DO remove the allCaps
  @JsonProperty(CloudProviderHandler.YB_FIREWALL_TAGS)
  @ApiModelProperty
  public String ybFirewallTags;

  @JsonProperty("use_host_vpc")
  @ApiModelProperty
  public String useHostVPC = "false";

  @JsonIgnore
  public Map<String, String> getEnvVars() {
    Map<String, String> envVars = new HashMap<>();

    if (this.ybFirewallTags != null) {
      envVars.put(CloudProviderHandler.YB_FIREWALL_TAGS, this.ybFirewallTags);
    }
    envVars.put(GCPCloudImpl.GCE_PROJECT_PROPERTY, this.gceProject);
    envVars.put(
        GCPCloudImpl.GOOGLE_APPLICATION_CREDENTIALS_PROPERTY, this.gceApplicationCredentialsPath);

    return envVars;
  }
}
