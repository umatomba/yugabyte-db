// Copyright (c) YugaByte, Inc.

package db.migration.default_.postgres;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yugabyte.yw.models.CloudMetadata;
import com.yugabyte.yw.models.ProviderDetails;

import org.flywaydb.core.api.migration.jdbc.BaseJdbcMigration;

public class V221__Provider_persist_details extends BaseJdbcMigration {

  @Override
  public void migrate(Connection connection) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    String selectStmt =
        "SELECT uuid, pgp_sym_decrypt(config, 'provider::config') as config, code FROM provider";
    ResultSet providers = connection.createStatement().executeQuery(selectStmt);

    while (providers.next()) {
      String providerUUID = providers.getString("uuid");
      String providerCode = providers.getString("code");
      Map<String, String> config =
          (HashMap<String, String>) mapper.readValue(providers.getString("config"), HashMap.class);

      ProviderDetails details = new ProviderDetails();
      // CloudMetadata cloudMetadata = CloudMetadata.getCloudProvider(providerCode, config);
      // details.setCloudMetadata(cloudMetadata);

      String detailsSerialized = mapper.writeValueAsString(details);
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
