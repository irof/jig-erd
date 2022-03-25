package jig.erd.infrastructure.database;

import jig.erd.domain.primitive.ColumnIdentifier;
import jig.erd.domain.primitive.Schema;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PsqlLoader {
    private Repository repository;

    public PsqlLoader(Repository repository) {
        this.repository = repository;
    }

    public void load(Connection conn) throws SQLException {
        loadTables(conn);
        loadReferences(conn);
    }

    void loadTables(Connection conn) throws SQLException {
        try (Statement t = conn.createStatement();
             // TODO コメントを pg_catalog.pg_description から取る。そのうち。
             ResultSet rs = t.executeQuery("SELECT" +
                     " table_schema, table_name" +
                     " FROM information_schema.tables" +
                     " WHERE" +
                     " table_schema NOT IN ('pg_catalog', 'information_schema')" +
                     " AND table_type = 'BASE TABLE'")) {
            while (rs.next()) {
                Schema schema = repository.getSchema(rs.getString("table_schema"));
                repository.registerEntity(schema, rs.getString("table_name"), "");
            }
        }
    }

    void loadReferences(Connection conn) throws SQLException {
        try (Statement t = conn.createStatement();
             ResultSet rs = t.executeQuery("SELECT" +
                     " k.table_schema fk_schema, k.table_name fk_table, k.column_name fk_column," +
                     " r.table_schema r_schema, r.table_name r_table, r.column_name r_column" +
                     " FROM information_schema.key_column_usage k" +
                     " INNER JOIN information_schema.referential_constraints rUK" +
                     "         ON k.constraint_name = rUK.constraint_name" +
                     " INNER JOIN information_schema.key_column_usage r" +
                     "         ON rUK.unique_constraint_name = r.constraint_name" +
                     "        AND rUK.unique_constraint_schema = r.constraint_schema" +
                     "        AND r.position_in_unique_constraint IS NULL" +
                     "        AND r.ordinal_position = k.position_in_unique_constraint" +
                     " WHERE" +
                     " k.position_in_unique_constraint IS NOT NULL")) {
            while (rs.next()) {
                ColumnIdentifier from = new ColumnIdentifier(
                        rs.getString("fk_schema"),
                        rs.getString("fk_table"),
                        rs.getString("fk_column"));
                ColumnIdentifier to = new ColumnIdentifier(
                        rs.getString("r_schema"),
                        rs.getString("r_table"),
                        rs.getString("r_column"));
                repository.registerRelation(from, to);
            }
        }
    }
}
