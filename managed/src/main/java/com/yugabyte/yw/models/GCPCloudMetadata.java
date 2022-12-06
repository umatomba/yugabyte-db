package com.yugabyte.yw.models;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yugabyte.yw.cloud.gcp.GCPCloudImpl;
import com.yugabyte.yw.controllers.handlers.CloudProviderHandler;

import io.swagger.annotations.ApiModelProperty;
import javax.persistence.Transient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GCPCloudMetadata implements CloudMetadata {
  private static final String TRANSIENT_PROPERTY_IN_MUTATE_API_REQUEST =
      "Transient property - only present in mutate API request";

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

  @Transient
  @ApiModelProperty(TRANSIENT_PROPERTY_IN_MUTATE_API_REQUEST)
  public String use_host_vpc;

  @Transient
  @ApiModelProperty(TRANSIENT_PROPERTY_IN_MUTATE_API_REQUEST)
  public String project_id;

  @Transient
  @ApiModelProperty(TRANSIENT_PROPERTY_IN_MUTATE_API_REQUEST)
  public String auth_provider_x509_cert_url;

  @Transient
  @ApiModelProperty(TRANSIENT_PROPERTY_IN_MUTATE_API_REQUEST)
  public String auth_uri;

  @Transient
  @ApiModelProperty(TRANSIENT_PROPERTY_IN_MUTATE_API_REQUEST)
  public String client_email;

  @Transient
  @ApiModelProperty(TRANSIENT_PROPERTY_IN_MUTATE_API_REQUEST)
  public String client_id;

  @Transient
  @ApiModelProperty(TRANSIENT_PROPERTY_IN_MUTATE_API_REQUEST)
  public String client_x509_cert_url;

  @Transient
  @ApiModelProperty(TRANSIENT_PROPERTY_IN_MUTATE_API_REQUEST)
  public String private_key;

  @Transient
  @ApiModelProperty(TRANSIENT_PROPERTY_IN_MUTATE_API_REQUEST)
  public String private_key_id;

  @Transient
  @ApiModelProperty(TRANSIENT_PROPERTY_IN_MUTATE_API_REQUEST)
  public String token_uri;

  @Transient
  @ApiModelProperty(TRANSIENT_PROPERTY_IN_MUTATE_API_REQUEST)
  public String type;

  @Transient
  @ApiModelProperty(TRANSIENT_PROPERTY_IN_MUTATE_API_REQUEST)
  public String use_host_credentials;

  @JsonIgnore
  public Map<String, String> getEnvVars() {
    Map<String, String> envVars = new HashMap<>();

    envVars.put(GCPCloudImpl.GCE_HOST_PROJECT_PROPERTY, this.gceHostProject);
    if (this.ybFirewallTags != null) {
      envVars.put(CloudProviderHandler.YB_FIREWALL_TAGS, this.ybFirewallTags);
    }
    envVars.put(GCPCloudImpl.GCE_PROJECT_PROPERTY, this.gceProject);
    envVars.put(
        GCPCloudImpl.GOOGLE_APPLICATION_CREDENTIALS_PROPERTY, this.googleApplicationCredentials);

    return envVars;
  }
}
