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

  @JsonIgnore
  final Map<String, String> configKeyMap =
      new HashMap<String, String>() {
        {
          put("azuTenantId", "AZURE_TENANT_ID");
          put("azuClientId", "AZURE_CLIENT_ID");
          put("azuClientSecret", "AZURE_CLIENT_SECRET");
          put("azuSubscriptionId", "AZURE_SUBSCRIPTION_ID");
          put("azuRG", "AZURE_RG");
        }
      };

  @JsonAlias("AZURE_TENANT_ID")
  @ApiModelProperty
  public String azuTenantId;

  @JsonAlias("AZURE_CLIENT_ID")
  @ApiModelProperty
  public String azuClientId;

  // ToDo: Masking
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

    if (azuClientId != null) {
      envVars.put("AZURE_TENANT_ID", azuTenantId);
      envVars.put("AZURE_CLIENT_ID", azuClientId);
      envVars.put("AZURE_CLIENT_SECRET", azuClientSecret);
      envVars.put("AZURE_SUBSCRIPTION_ID", azuSubscriptionId);
      envVars.put("AZURE_RG", azuRG);
    }

    return envVars;
  }

  @JsonIgnore
  public Map<String, String> getConfigKeyMap() {
    return configKeyMap;
  }
}
