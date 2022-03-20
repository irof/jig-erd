package jig.erd.infrastructure.database;

import jig.erd.domain.primitive.ColumnIdentifier;
import jig.erd.domain.primitive.Schema;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class H2Loader {
    private Repository repository;

    public H2Loader(Repository repository) {
        this.repository = repository;
    }

    public void load(Connection conn) throws SQLException {
        loadTables(conn);
        loadReferences(conn);
    }

    void loadTables(Connection conn) throws SQLException {
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

    void loadReferences(Connection conn) throws SQLException {
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
