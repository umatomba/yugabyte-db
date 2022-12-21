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

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.yugabyte.yw.models.AccessKey.MigratedKeyInfoFields;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProviderDetails extends MigratedKeyInfoFields {

  @ApiModelProperty public AWSCloudMetadata awsCloudMetadata;
  @ApiModelProperty public AzureCloudMetadata azureCloudMetadata;
  @ApiModelProperty public GCPCloudMetadata gcpCloudMetadata;
  @ApiModelProperty public KubernetesMetadata kubernetesCloudMetadata;
  @ApiModelProperty public OnPremCloudMetadata onPremCloudMetadata;
}
