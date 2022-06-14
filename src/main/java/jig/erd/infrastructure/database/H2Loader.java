package jig.erd.infrastructure.database;

import jig.erd.domain.primitive.ColumnIdentifier;
import jig.erd.domain.primitive.Schema;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * H2 Database Engine v2向けのLoaderです。
 *
 * @see <a href="https://www.h2database.com/html/systemtables.html">h2のINFORMATION_SCHEMAの説明</a>
 */
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
             ResultSet rs = t.executeQuery("SELECT" +
                     " TABLE_SCHEMA, TABLE_NAME, REMARKS" +
                     " FROM INFORMATION_SCHEMA.TABLES" +
                     " WHERE TABLE_TYPE = 'BASE TABLE'" +
                     " AND TABLE_SCHEMA <> 'INFORMATION_SCHEMA'")) {
            while (rs.next()) {
                Schema schema = repository.getSchema(rs.getString("TABLE_SCHEMA"));
                repository.registerEntity(schema, rs.getString("TABLE_NAME"), rs.getString("REMARKS"));
            }
        }
    }

    void loadReferences(Connection conn) throws SQLException {
        try (Statement t = conn.createStatement();
             // key_column_usage には制約で使用されているカラムの順番が記録されている。
             //   複数キーからなるFK/PKで列の組み合わせを特定するためにJOINする必要がある。
             // referential_constraints でFKの制約と対象になるPKの制約を特定
             //   key_column_usageと結合することで列を特定する。
             //   constraint_nameは異なるschemaで同じ名前が使用できるので、schema/name両方で結合する必要がある。
             ResultSet rs = t.executeQuery("SELECT" +
                     " k.table_schema fk_schema, k.table_name fk_table, k.column_name fk_column," +
                     " r.table_schema r_schema, r.table_name r_table, r.column_name r_column" +
                     " FROM information_schema.key_column_usage k" +
                     " INNER JOIN information_schema.referential_constraints rUK" +
                     "         ON k.constraint_name = rUK.constraint_name" +
                     "        AND k.constraint_schema = rUK.constraint_schema" +
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
