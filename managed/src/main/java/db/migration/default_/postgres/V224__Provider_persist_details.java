// Copyright (c) YugaByte, Inc.

package db.migration.default_.postgres;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yugabyte.yw.models.CloudMetadata;
import com.yugabyte.yw.models.Provider;
import com.yugabyte.yw.models.ProviderDetails;

import org.flywaydb.core.api.migration.jdbc.BaseJdbcMigration;

public class V224__Provider_persist_details extends BaseJdbcMigration {

  @Override
  public void migrate(Connection connection) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    String selectStmt =
        "SELECT uuid, customer_uuid, pgp_sym_decrypt(config, 'provider::config') as config, code FROM provider";
    ResultSet providers = connection.createStatement().executeQuery(selectStmt);

    while (providers.next()) {
      String providerUUID = providers.getString("uuid");
      String customerUUID = providers.getString("customer_uuid");
      String providerConfig = providers.getString("config");
      if (providerConfig == null) {
        continue;
      }
      Map<String, String> config = mapper.readValue(providers.getString("config"), Map.class);
      Provider provider =
          Provider.getOrBadRequest(UUID.fromString(customerUUID), UUID.fromString(providerUUID));
      provider.details = new ProviderDetails();

      CloudMetadata.setCloudProviderMetadataFromConfig(provider, config);
      String detailsSerialized = mapper.writeValueAsString(provider.details);
      String updateStmt =
          String.format(
              "UPDATE provider SET details = '%s' WHERE uuid = '%s'",
              detailsSerialized, providerUUID);
      connection.createStatement().execute(updateStmt);
    }

    connection
        .createStatement()
        .executeUpdate(
            "ALTER TABLE provider ALTER COLUMN details TYPE"
                + " bytea USING pgp_sym_encrypt(details::text, 'provider::details')");
  }
}
