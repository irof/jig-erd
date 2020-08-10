package jig.erd;

import jig.erd.application.repository.Repository;
import jig.erd.domain.diagram.ViewPoint;
import jig.erd.infrastructure.DotCommandResult;
import jig.erd.infrastructure.DotCommandRunner;
import jig.erd.infrastructure.database.DataBaseDefinitionLoader;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public class JigErd {
    Logger logger = Logger.getLogger(JigErd.class.getName());

    DataSource dataSource;
    JigProperties jigProperties;

    public JigErd(DataSource dataSource) {
        this.dataSource = dataSource;
        jigProperties = JigProperties.get();
        jigProperties.load();
    }

    public static void run(DataSource dataSource) {
        JigErd jigErd = new JigErd(dataSource);
        jigErd.run();
    }

    public void run() {
        Repository repository = new Repository();
        new DataBaseDefinitionLoader(dataSource, repository).load();

        DotCommandResult result1 = exportDiagram(repository.columnRelationDiagram().dotText(jigProperties), ViewPoint.詳細);
        logger.info(result1.toString());
        DotCommandResult result2 = exportDiagram(repository.entityRelationDiagram().dotText(jigProperties), ViewPoint.概要);
        logger.info(result2.toString());
    }

    private DotCommandResult exportDiagram(String graphText, ViewPoint viewPoint) {
        try {
            Path workDirectory = Files.createTempDirectory("temp");
            workDirectory.toFile().deleteOnExit();

            Path sourcePath = workDirectory.resolve(jigProperties.dotFileName(viewPoint)).toAbsolutePath();
            Files.writeString(sourcePath, graphText, StandardCharsets.UTF_8);
            logger.info("temporary DOT file: " + sourcePath);

            Path outputPath = jigProperties.outputPath(viewPoint);

            DotCommandRunner dotCommandRunner = new DotCommandRunner();
            return dotCommandRunner.run(jigProperties.outputFormat(), sourcePath, outputPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
