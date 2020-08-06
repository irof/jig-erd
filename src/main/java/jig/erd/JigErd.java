package jig.erd;

import jig.erd.application.repository.Repository;
import jig.erd.domain.diagram.detail.ColumnRelationDiagram;
import jig.erd.infrastructure.DataBaseDefinitionLoader;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        new DataBaseDefinitionLoader(dataSource, repository).load();
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
}
