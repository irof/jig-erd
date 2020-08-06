package jig.erd;

import jig.erd.application.repository.Repository;
import jig.erd.domain.diagram.ColumnRelationDiagram;
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
import java.util.*;
import java.util.logging.Logger;

public class ERDiagram {
    Logger logger = Logger.getLogger(ERDiagram.class.getName());

    Repository repository = new Repository();
    DataSource dataSource;

    public ERDiagram(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void run() {
        try {
            StringJoiner graph = new StringJoiner("\n", "digraph ERD {", "}")
                    .add("rankdir=LR;")
                    .add("graph[style=filled,fillcolor=lightyellow];")
                    .add("node[shape=box,style=filled,fillcolor=lightgoldenrod];")
                    .add("edge[arrowhead=open, style=dashed]");
            graph.add(tables());
            graph.add(references());
            String graphText = graph.toString();

            ColumnRelationDiagram columnRelationDiagram = repository.columnRelationDiagram();
            graphText = columnRelationDiagram.dotText();

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

    private CharSequence tables() {
        try (Connection conn = dataSource.getConnection()) {
            StringJoiner result = new StringJoiner("\n");

            try (Statement t = conn.createStatement();
                 ResultSet rs = t.executeQuery("SELECT " +
                         " TABLE_SCHEMA, TABLE_NAME, REMARKS" +
                         " FROM INFORMATION_SCHEMA.TABLES" +
                         " WHERE TABLE_TYPE = 'TABLE'")) {
                Map<String, List<String>> map = new HashMap<>();
                while (rs.next()) {
                    map.computeIfAbsent(rs.getString("TABLE_SCHEMA"), key -> new ArrayList<>())
                            .add(rs.getString("TABLE_NAME"));

                    Schema schema = repository.getSchema(rs.getString("TABLE_SCHEMA"));
                    repository.registerEntity(schema, rs.getString("TABLE_NAME"), rs.getString("REMARKS"));
                }

                for (String schema : map.keySet()) {
                    StringJoiner schemaContent = new StringJoiner("\n", "{", "}");
                    schemaContent.add("label=" + schema + ";");

                    for (String table : map.get(schema)) {
                        schemaContent.add(String.format("\"%s.%s\"[label=\"%s\"];", schema, table, table));
                    }
                    result.add("subgraph cluster_" + schema + schemaContent);
                }
            }

            return result.toString();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private CharSequence references() {
        try (Connection conn = dataSource.getConnection()) {
            StringJoiner result = new StringJoiner("\n");

            try (Statement t = conn.createStatement();
                 ResultSet rs = t.executeQuery("SELECT"
                         + " FKTABLE_SCHEMA, FKTABLE_NAME, FKCOLUMN_NAME,"
                         + " PKTABLE_SCHEMA, PKTABLE_NAME, PKCOLUMN_NAME"
                         + " FROM INFORMATION_SCHEMA.CROSS_REFERENCES")) {
                while (rs.next()) {
                    String schema = rs.getString("FKTABLE_SCHEMA");
                    String table = rs.getString("FKTABLE_NAME");

                    String toSchema = rs.getString("PKTABLE_SCHEMA");
                    String toTable = rs.getString("PKTABLE_NAME");

                    ColumnIdentifier from = new ColumnIdentifier(rs.getString("FKTABLE_SCHEMA"), rs.getString("FKTABLE_NAME"), rs.getString("FKCOLUMN_NAME"));
                    ColumnIdentifier to = new ColumnIdentifier(rs.getString("PKTABLE_SCHEMA"), rs.getString("PKTABLE_NAME"), rs.getString("PKCOLUMN_NAME"));
                    repository.registerRelation(from, to);

                    result.add(String.format("\"%s.%s\" -> \"%s.%s\"", schema, table, toSchema, toTable));
                }
            }
            return result.toString();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
