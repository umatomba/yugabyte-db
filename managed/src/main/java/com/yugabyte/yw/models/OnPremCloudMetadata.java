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
public class OnPremCloudMetadata implements CloudMetadata {

  @JsonIgnore
  final Map<String, String> configKeyMap =
      new HashMap<String, String>() {
        {
          put("ybHomeDir", "YB_HOME_DIR");
        }
      };

  @JsonAlias("YB_HOME_DIR")
  @ApiModelProperty
  public String ybHomeDir;

  @JsonIgnore
  public Map<String, String> getEnvVars() {
    Map<String, String> envVars = new HashMap<>();

    if (ybHomeDir != null) {
      envVars.put("YB_HOME_DIR", ybHomeDir);
    }

    return envVars;
  }

  @JsonIgnore
  public Map<String, String> getConfigKeyMap() {
    return configKeyMap;
  }
}
