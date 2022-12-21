package com.yugabyte.yw.models;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yugabyte.yw.models.helpers.CommonUtils;

import io.swagger.annotations.ApiModelProperty;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class KubernetesMetadata implements CloudMetadataInterface {

  @JsonIgnore
  final Map<String, String> configKeyMap =
      new HashMap<String, String>() {
        {
          put("kubeConfigProvider", "KUBECONFIG_PROVIDER");
          put("kubeConfigServiceAccount", "KUBECONFIG_SERVICE_ACCOUNT");
          put("kubeConfigImageRegistry", "KUBECONFIG_IMAGE_REGISTRY");
          put("kubeConfigImagePullSecretName", "KUBECONFIG_IMAGE_PULL_SECRET_NAME");
          put("kubeConfigPullSecret", "KUBECONFIG_PULL_SECRET");
          put("kubeConfigName", "KUBECONFIG_NAME");
          put("kubeConfigContent", "KUBECONFIG_CONTENT");
          put("kubeConfig", "KUBECONFIG");
          put("kubernetesStorageClass", "KUBECONFIG_STORAGE_CLASSES");
          put("kubeConfigPullSecretContent", "KUBECONFIG_PULL_SECRET_CONTENT");
          put("kubeConfigPullSecretName", "KUBECONFIG_PULL_SECRET_NAME");
        }
      };

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

  @JsonProperty("KUBECONFIG_NAME")
  @ApiModelProperty
  public String kubeConfigName;

  @JsonProperty("KUBECONFIG_CONTENT")
  @ApiModelProperty
  public String kubeConfigContent;

  @JsonProperty("KUBECONFIG")
  @ApiModelProperty
  public String kubeConfig;

  // Used??
  @JsonProperty("KUBECONFIG_STORAGE_CLASSES")
  @ApiModelProperty
  public String kubernetesStorageClass;

  @JsonProperty("KUBECONFIG_PULL_SECRET_CONTENT")
  @ApiModelProperty
  public String kubeConfigPullSecretContent;

  @JsonProperty("KUBECONFIG_PULL_SECRET_NAME")
  @ApiModelProperty
  public String kubeConfigPullSecretName;

  @JsonIgnore
  public Map<String, String> getEnvVars() {
    // pass
    return null;
  }

  @JsonIgnore
  public Map<String, String> getConfigMapForUIOnlyAPIs(Map<String, String> config) {
    return config;
  }

  @JsonIgnore
  public void maskSensitiveData() {
    this.kubeConfig = CommonUtils.getMaskedValue(kubeConfig);
    this.kubeConfigImagePullSecretName = CommonUtils.getMaskedValue(kubeConfigImagePullSecretName);
    this.kubeConfigPullSecret = CommonUtils.getMaskedValue(kubeConfigPullSecret);
  }
}
