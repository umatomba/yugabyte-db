// Copyright (c) YugaByte, Inc.

package db.migration.default.postgres

import java.sql.Connection
import java.util.Map
import java.util.UUID

import org.flywaydb.core.api.migration.jdbc.JdbcMigration
import play.api.libs.json._
import com.yugabyte.yw.models.helpers.CommonUtils
import com.yugabyte.yw.common.kms.util.EncryptionAtRestUtil
import com.yugabyte.yw.common.kms.util.KeyProvider
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode

class V219__Provider_persist_details extends JdbcMigration {
  override def migrate(connection: Connection): Unit = {
    
      // Add the details column to the provider table.
    connection.createStatement.executeUpdate("ALTER TABLE provider ADD COLUMN" +
      " IF NOT EXISTS details json_alias")
    connection.createStatement.executeUpdate("ALTER TABLE provider ALTER COLUMN details TYPE" +
      " bytea USING pgp_sym_encrypt(details::text, 'provider::details')")

    // Populate details in the provider config
    var selectStmt = "SELECT uuid, customer_uuid FROM provider"
    var resultSet = connection.createStatement().executeQuery(selectStmt)
    while(resultSet.next()) {
      val providerUuid = UUID.fromString(resultSet.getString("uuid"))
      val customerUuid = UUID.fromString(resultSet.getString("customer_uuid"))
      CommonUtils.populateProviderDetails(providerUuid, customerUuid);
    }
  }
}
