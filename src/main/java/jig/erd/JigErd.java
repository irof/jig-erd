package jig.erd;

import jig.erd.domain.diagram.ViewPoint;
import jig.erd.infrastructure.DotCommandResult;
import jig.erd.infrastructure.DotCommandRunner;
import jig.erd.infrastructure.database.DataBaseDefinitionLoader;
import jig.erd.infrastructure.database.JdbcConnectionProvider;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.util.logging.Logger;

public class JigErd {
    static final Logger logger = Logger.getLogger(JigErd.class.getName());

    JdbcConnectionProvider jdbcConnectionProvider;
    JigProperties jigProperties;

    public JigErd(JdbcConnectionProvider jdbcConnectionProvider) {
        this.jdbcConnectionProvider = jdbcConnectionProvider;
        jigProperties = JigProperties.get();
        jigProperties.load();
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            logger.info("JIG-ERD 0.0.4\n\nusage:\n" +
                    "  java -cp jig-erd.jar:{jdbcJAR} " + JigErd.class.getName() + " {url} {user} {password}\n");
            return;
        }
        String url = args[0];
        String user = args[1];
        String pass = args[2];
        JigErd instance = new JigErd(() -> DriverManager.getConnection(url, user, pass));
        instance.run();
    }

    /**
     * DataSourceを使用する場合のエントリポイント
     *
     * @param dataSource ダイアグラムを出力するデータソース
     */
    public static void run(DataSource dataSource) {
        JigErd jigErd = new JigErd(dataSource::getConnection);
        jigErd.run();
    }

    public void run() {
        var erdRoot = new DataBaseDefinitionLoader(jdbcConnectionProvider).load();

        DotCommandResult result1 = exportDiagram(erdRoot.columnRelationDiagram().filter(jigProperties).dotText(jigProperties), ViewPoint.詳細);
        logger.info(result1.toString());
        DotCommandResult result2 = exportDiagram(erdRoot.entityRelationDiagram().filter(jigProperties).dotText(jigProperties), ViewPoint.概要);
        logger.info(result2.toString());
        DotCommandResult result3 = exportDiagram(erdRoot.schemaRelationDiagram().filter(jigProperties).dotText(jigProperties), ViewPoint.俯瞰);
        logger.info(result3.toString());
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
