package com.yugabyte.yw.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.yugabyte.yw.cloud.gcp.GCPCloudImpl;
import com.yugabyte.yw.controllers.handlers.CloudProviderHandler;
import com.yugabyte.yw.models.helpers.CommonUtils;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModelProperty.AccessMode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
public class GCPCloudInfo implements CloudInfoInterface {

  @JsonIgnore
  final Map<String, String> configKeyMap =
      new HashMap<String, String>() {
        {
          put("gceProject", "project_id");
          put("gceApplicationCredentialsPath", "GOOGLE_APPLICATION_CREDENTIALS");
          put("customGceNetwork", "network");
          put("ybFirewallTags", CloudProviderHandler.YB_FIREWALL_TAGS);
          put("useHostVPC", "use_host_vpc");
        }
      };

  @JsonIgnore
  final List<String> toRemoveKeyFromConfig =
      ImmutableList.of("gceApplicationCredentials", "useHostCredentials");

  @JsonIgnore
  final Map<String, String> toAddKeysInConfig =
      new HashMap<String, String>() {
        {
          put("client_email", "GCE_EMAIL");
          put("project_id", "GCE_PROJECT");
          put("auth_provider_x509_cert_url", "auth_provider_x509_cert_url");
          put("auth_uri", "auth_uri");
          put("client_email", "client_email");
          put("client_id", "client_id");
          put("client_x509_cert_url", "client_x509_cert_url");
          put("private_key", "private_key");
          put("private_key_id", "private_key_id");
          put("token_uri", "token_uri");
          put("type", "type");
        }
      };

  @JsonAlias("host_project_id")
  @ApiModelProperty
  public String gceProject;

  @JsonAlias("config_file_path")
  @ApiModelProperty(accessMode = AccessMode.READ_ONLY)
  public String gceApplicationCredentialsPath;

  @JsonAlias("config_file_contents")
  @ApiModelProperty
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

  @JsonIgnore
  public JsonNode getCredentialJSON() {
    if (gceApplicationCredentials == null) {
      return null;
    }
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
  public Map<String, String> getConfigMapForUIOnlyAPIs(Map<String, String> config) {
    for (Map.Entry<String, String> entry : configKeyMap.entrySet()) {
      if (config.get(entry.getKey()) != null) {
        config.put(entry.getValue(), config.get(entry.getKey()));
        config.remove(entry.getKey());
      }
    }

    for (String removeKey : toRemoveKeyFromConfig) {
      config.remove(removeKey);
    }

    JsonNode gcpCredential = this.getCredentialJSON();
    if (gcpCredential == null) {
      return config;
    }
    ObjectNode credentialJSON = (ObjectNode) gcpCredential;
    for (Map.Entry<String, String> entry : toAddKeysInConfig.entrySet()) {
      if (credentialJSON.get(entry.getKey()) != null) {
        config.put(entry.getValue(), credentialJSON.get(entry.getKey()).toString());
      }
    }

    return config;
  }

  @JsonIgnore
  public void maskSensitiveData() {
    this.gceApplicationCredentialsPath = CommonUtils.getMaskedValue(gceApplicationCredentialsPath);
    this.gceApplicationCredentials = CommonUtils.getMaskedValue(gceApplicationCredentials);
  }
}
