/*
 * Copyright 2022 YugaByte, Inc. and Contributors
 *
 * Licensed under the Polyform Free Trial License 1.0.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://github.com/YugaByte/yugabyte-db/blob/master/licenses/POLYFORM-FREE-TRIAL-LICENSE-1.0.0.txt
 */

package com.yugabyte.yw.models;

import io.swagger.annotations.ApiModelProperty;
import java.util.Collections;
import java.util.List;

public class ProviderDetails {
  // these are the fields in access key info that actually belong in provider
  @ApiModelProperty public String sshUser;
  @ApiModelProperty public Integer sshPort;
  @ApiModelProperty public boolean airGapInstall = false;
  @ApiModelProperty public String provisionInstanceScript = "";
  @ApiModelProperty public boolean installNodeExporter = true;
  @ApiModelProperty public Integer nodeExporterPort = 9300;
  @ApiModelProperty public String nodeExporterUser = "prometheus";
  @ApiModelProperty public boolean skipProvisioning = false;
  @ApiModelProperty public boolean deleteRemote = true;
  @ApiModelProperty public boolean setUpChrony = false;
  @ApiModelProperty public List<String> ntpServers = Collections.emptyList();

  // Indicates whether the provider was created before or after PLAT-3009
  // True if it was created after, else it was created before.
  // Dictates whether or not to show the set up NTP option in the provider UI
  @ApiModelProperty public boolean showSetUpChrony = false;
}
