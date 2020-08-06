package jig.erd.infrastructure;

import jig.erd.application.repository.Repository;
import jig.erd.domain.environment.RDBMS;
import jig.erd.domain.primitive.ColumnIdentifier;
import jig.erd.domain.primitive.Schema;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseDefinitionLoader {

    DataSource dataSource;
    Repository repository;

    public DataBaseDefinitionLoader(DataSource dataSource, Repository repository) {
        this.dataSource = dataSource;
        this.repository = repository;
    }

    public void load() {
        try (Connection conn = dataSource.getConnection()) {
            String url = conn.getMetaData().getURL();
            RDBMS.from(url);

            loadTables(conn);
            loadReferences(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadTables(Connection conn) throws SQLException {
        try (Statement t = conn.createStatement();
             ResultSet rs = t.executeQuery("SELECT " +
                     " TABLE_SCHEMA, TABLE_NAME, REMARKS" +
                     " FROM INFORMATION_SCHEMA.TABLES" +
                     " WHERE TABLE_TYPE = 'TABLE'")) {
            while (rs.next()) {
                Schema schema = repository.getSchema(rs.getString("TABLE_SCHEMA"));
                repository.registerEntity(schema, rs.getString("TABLE_NAME"), rs.getString("REMARKS"));
            }
        }
    }

    private void loadReferences(Connection conn) throws SQLException {
        try (Statement t = conn.createStatement();
             ResultSet rs = t.executeQuery("SELECT"
                     + " FKTABLE_SCHEMA, FKTABLE_NAME, FKCOLUMN_NAME,"
                     + " PKTABLE_SCHEMA, PKTABLE_NAME, PKCOLUMN_NAME"
                     + " FROM INFORMATION_SCHEMA.CROSS_REFERENCES")) {
            while (rs.next()) {
                ColumnIdentifier from = new ColumnIdentifier(
                        rs.getString("FKTABLE_SCHEMA"),
                        rs.getString("FKTABLE_NAME"),
                        rs.getString("FKCOLUMN_NAME"));
                ColumnIdentifier to = new ColumnIdentifier(
                        rs.getString("PKTABLE_SCHEMA"),
                        rs.getString("PKTABLE_NAME"),
                        rs.getString("PKCOLUMN_NAME"));
                repository.registerRelation(from, to);
            }
        }
    }
}
