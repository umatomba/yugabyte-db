package com.yugabyte.yw.models;

import java.util.Map;

import com.google.inject.Singleton;

import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class AWSCloudMetadata implements CloudMetadata {
    @ApiModelProperty private String awsAccessKeyID;
    @ApiModelProperty private String awsAccessKeySecret;

    public void setConfig(Map<String, String> config) {
        System.out.println("Testing the config value here");
        System.out.println(config);
        this.awsAccessKeyID = config.get("AWS_ACCESS_KEY_ID");
        this.awsAccessKeySecret = config.get("AWS_SECRET_ACCESS_KEY");
        System.out.println(config.get("AWS_ACCESS_KEY_ID"));
        System.out.println(config.get("AWS_SECRET_ACCESS_KEY"));
    }

    public void getConfig() {
        System.out.println("Testing the configs here");
        System.out.println(this.awsAccessKeyID);
        System.out.println(this.awsAccessKeySecret);
    }
}
