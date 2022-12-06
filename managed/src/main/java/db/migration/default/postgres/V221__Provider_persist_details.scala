// Copyright (c) YugaByte, Inc.

package db.migration.default.postgres

import java.sql.Connection
import java.util.Map
import java.util.UUID

import org.flywaydb.core.api.migration.jdbc.JdbcMigration
import play.api.libs.json._
import com.yugabyte.yw.models.ProviderDetails
import com.fasterxml.jackson.databind.ObjectMapper

class V221__Provider_persist_details extends JdbcMigration {
  override def migrate(connection: Connection): Unit = {

    var selectStmt = "SELECT uuid, pgp_sym_decrypt(config, 'provider::config') as config, code FROM provider"
    var resultSet = connection.createStatement().executeQuery(selectStmt)
    while(resultSet.next()) {
      val providerUuid = resultSet.getString("uuid")
      var config = resultSet.getString("config")
      val code = resultSet.getString("code")
      val objectMapper = new ObjectMapper()
      val configMap = objectMapper.readValue(config, classOf[Map[String, String]])
      val providerDetails = ProviderDetails.getProviderDetails(configMap, code);
      var details = objectMapper.writeValueAsString(providerDetails)
      connection.createStatement().execute(s"UPDATE provider SET details = '${details}'"+
        s" WHERE uuid = '$providerUuid'")
    }

    connection.createStatement.executeUpdate("ALTER TABLE provider ALTER COLUMN details TYPE" +
      " bytea USING pgp_sym_encrypt(details::text, 'provider::details')")
  }
}
