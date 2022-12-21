// Copyright (c) YugaByte, Inc.

package db.migration.default_.postgres;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.yugabyte.yw.commissioner.Common.CloudType;
import com.yugabyte.yw.controllers.handlers.CloudProviderHandler;
import com.yugabyte.yw.models.CloudMetadata;
import com.yugabyte.yw.models.Customer;
import com.yugabyte.yw.models.Provider;

import io.ebean.Ebean;
import play.libs.Json;

import org.flywaydb.core.api.migration.jdbc.BaseJdbcMigration;

public class V226__Provider_persist_details extends BaseJdbcMigration {

  @Override
  public void migrate(Connection connection) throws Exception {
    Ebean.execute(V226__Provider_persist_details::migrateConfigToDetails);
  }

  public static void migrateConfigToDetails() {
    for (Customer customer : Customer.getAll()) {
      for (Provider provider : Provider.getAll(customer.uuid)) {
        Map<String, String> config = provider.config;
        if (config == null) {
          continue;
        }

        // Massage the config to be stored in newer format.
        if (provider.getCloudCode().equals(CloudType.gcp)) {
          config = V226__Provider_persist_details.massageGCPConfig(config);
        }
        CloudMetadata.setCloudProviderMetadataFromConfig(provider, config);
        provider.save();
      }
    }
  }

  private static Map<String, String> massageGCPConfig(Map<String, String> config) {
    List<String> credKeys =
        ImmutableList.of(
            "client_email",
            "project_id",
            "auth_provider_x509_cert_url",
            "auth_uri",
            "client_id",
            "client_x509_cert_url",
            "private_key",
            "private_key_id",
            "token_uri",
            "type");
    Map<String, String> modifiedConfigMap = new HashMap<>();
    final Map<String, String> oldToNewConfigKeyMap =
        new HashMap<String, String>() {
          {
            put("project_id", "host_project_id");
            put("GOOGLE_APPLICATION_CREDENTIALS", "config_file_path");
            put("CUSTOM_GCE_NETWORK", "network");
            put(CloudProviderHandler.YB_FIREWALL_TAGS, CloudProviderHandler.YB_FIREWALL_TAGS);
          }
        };
    ObjectMapper mapper = Json.mapper();
    ObjectNode gcpCredentials = mapper.createObjectNode();

    for (String key : credKeys) {
      if (config.containsKey(key)) {
        gcpCredentials.put(key, config.get(key));
      }
    }
    for (Map.Entry<String, String> entry : oldToNewConfigKeyMap.entrySet()) {
      if (config.containsKey(entry.getKey())) {
        modifiedConfigMap.put(entry.getValue(), config.get(entry.getKey()));
      }
    }
    modifiedConfigMap.put("config_file_contents", gcpCredentials.toString());
    System.out.println("Testing SHUBHAM");
    System.out.println(modifiedConfigMap);
    return modifiedConfigMap;
  }
}
