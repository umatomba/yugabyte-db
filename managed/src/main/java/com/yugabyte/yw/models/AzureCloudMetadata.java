package com.yugabyte.yw.models;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AzureCloudMetadata implements CloudMetadata {

  @JsonAlias("AZURE_TENANT_ID")
  @ApiModelProperty
  public String azuTenantId;

  @JsonAlias("AZURE_CLIENT_ID")
  @ApiModelProperty
  public String azuClientId;

  @JsonAlias("AZURE_CLIENT_SECRET")
  @ApiModelProperty
  public String azuClientSecret;

  @JsonAlias("AZURE_SUBSCRIPTION_ID")
  @ApiModelProperty
  public String azuSubscriptionId;

  @JsonAlias("AZURE_RG")
  @ApiModelProperty
  public String azuRG;

  @JsonIgnore
  public Map<String, String> getEnvVars() {
    Map<String, String> envVars = new HashMap<>();

    if (this.azuClientId != null) {
      envVars.put("AZURE_TENANT_ID", this.azuTenantId);
      envVars.put("AZURE_CLIENT_ID", this.azuClientId);
      envVars.put("AZURE_CLIENT_SECRET", this.azuClientSecret);
      envVars.put("AZURE_SUBSCRIPTION_ID", this.azuSubscriptionId);
      envVars.put("AZURE_RG", this.azuRG);
    }

    return envVars;
  }
}
