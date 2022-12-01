package com.yugabyte.yw.models;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class KubernetesMetadata implements CloudMetadata {

  @JsonProperty("KUBECONFIG_PROVIDER")
  @ApiModelProperty
  public String kubeConfigProvider;

  @JsonProperty("KUBECONFIG_SERVICE_ACCOUNT")
  @ApiModelProperty
  public String kubeConfigServiceAccount;

  @JsonProperty("KUBECONFIG_IMAGE_REGISTRY")
  @ApiModelProperty
  public String kubeConfigImageRegistry;

  @JsonProperty("KUBECONFIG_IMAGE_PULL_SECRET_NAME")
  @ApiModelProperty
  public String kubeConfigImagePullSecretName;

  @JsonProperty("KUBECONFIG_PULL_SECRET")
  @ApiModelProperty
  public String kubeConfigPullSecret;

  @JsonProperty("KUBECONFIG")
  @ApiModelProperty
  public String kubeConfig;

  @JsonIgnore
  public Map<String, String> getEnvVars() {
    // pass
    return null;
  }

  @JsonIgnore
  public void updateCloudMetadataDetails(String key, String value) {
    // pass
  }
}
