package com.yugabyte.yw.models;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yugabyte.yw.cloud.gcp.GCPCloudImpl;
import com.yugabyte.yw.controllers.handlers.CloudProviderHandler;
import com.yugabyte.yw.models.helpers.CommonUtils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
public class GCPCloudMetadata implements CloudMetadata {

  @JsonIgnore
  final Map<String, String> configKeyMap =
      new HashMap<String, String>() {
        {
          put("gceProject", "host_project_id");
          put("gceApplicationCredentialsPath", "config_file_path");
          put("gceApplicationCredentials", "config_file_contents");
          put("customGceNetwork", "network");
          put("ybFirewallTags", CloudProviderHandler.YB_FIREWALL_TAGS);
          put("useHostVPC", "use_host_vpc");
        }
      };

  @JsonAlias("host_project_id")
  @ApiModelProperty
  public String gceProject;

  @JsonAlias("config_file_path")
  public String gceApplicationCredentialsPath;

  @JsonAlias("config_file_contents")
  // Nit: Need to be changed as JsonNode once we remove the
  // config Map<String, String> dependency & start using details instead.
  public String gceApplicationCredentials;

  @JsonAlias("network")
  @ApiModelProperty
  public String customGceNetwork;

  @JsonAlias(CloudProviderHandler.YB_FIREWALL_TAGS)
  @ApiModelProperty
  public String ybFirewallTags;

  @JsonAlias("use_host_vpc")
  @ApiModelProperty
  public String useHostVPC;

  @JsonAlias("use_host_credentials")
  @ApiModelProperty
  public String useHostCredentials;

  @JsonProperty("gceApplicationCredentials")
  public String getGceApplicationCredentials() {
    if (gceApplicationCredentials != null) {
      return CommonUtils.getMaskedValue("gceApplicationCredentials", gceApplicationCredentials);
    }
    return null;
  }

  @JsonIgnore
  public JsonNode getCredentialJSON() {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode gcpCredentials = null;
    try {
      gcpCredentials = mapper.readTree(this.gceApplicationCredentials);
    } catch (Exception e) {
      log.error("Error parsing the credential json", e);
    }
    return gcpCredentials;
  }

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

  @JsonIgnore
  public Map<String, String> getConfigKeyMap() {
    return configKeyMap;
  }
}
