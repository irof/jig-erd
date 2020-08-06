package jig.erd;

import jig.erd.application.repository.Repository;
import jig.erd.domain.diagram.detail.ColumnRelationDiagram;
import jig.erd.domain.primitive.ColumnIdentifier;
import jig.erd.domain.primitive.Schema;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.logging.Logger;

public class JigErd {
    Logger logger = Logger.getLogger(JigErd.class.getName());

    Repository repository = new Repository();
    DataSource dataSource;

    public JigErd(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static void run(DataSource dataSource) {
        JigErd jigErd = new JigErd(dataSource);
        jigErd.run();
    }

    public void run() {
        loadSchemaData();
        exportDiagram();
    }

    private void exportDiagram() {
        try {
            ColumnRelationDiagram columnRelationDiagram = repository.columnRelationDiagram();
            String graphText = columnRelationDiagram.dotText();

            Path dir = Paths.get("");
            Path gvPath = dir.resolve("jig-er.gv");
            Files.writeString(gvPath, graphText, StandardCharsets.UTF_8);
            logger.info("exported DOT file: " + gvPath.toAbsolutePath());

            Path imagePath = dir.resolve("jig-er.svg");

            String[] dotCommand = {"dot", "-Tsvg", "-o" + imagePath.toAbsolutePath(), gvPath.toAbsolutePath().toString()};
            logger.info("command: " + Arrays.toString(dotCommand));

            int code = new ProcessBuilder()
                    .command(dotCommand)
                    .start()
                    .waitFor();
            logger.info("exit code: " + code);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadSchemaData() {
        try (Connection conn = dataSource.getConnection()) {
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
