package jig.erd;

import jig.erd.domain.diagram.ViewPoint;
import jig.erd.domain.diagram.editor.Digraph;
import jig.erd.infrastructure.DotCommandResult;
import jig.erd.infrastructure.DotCommandRunner;
import jig.erd.infrastructure.database.DataBaseDefinitionLoader;
import jig.erd.infrastructure.database.JdbcConnectionProvider;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
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
        this.jigProperties = JigProperties.create();
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            logger.info("JIG-ERD\n\nusage:\n" +
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
        prepareOutputDirectory(jigProperties.outputDirectory);

        var erdRoot = new DataBaseDefinitionLoader(jdbcConnectionProvider).load().filter(jigProperties);
        logger.info("erdRoot: " + erdRoot.summaryText());

        DotCommandResult result1 = exportDiagram(erdRoot.columnRelationDiagram(), ViewPoint.詳細);
        logger.info(result1.toString());
        DotCommandResult result2 = exportDiagram(erdRoot.entityRelationDiagram(), ViewPoint.概要);
        logger.info(result2.toString());
        DotCommandResult result3 = exportDiagram(erdRoot.schemaRelationDiagram(), ViewPoint.俯瞰);
        logger.info(result3.toString());
    }

    private DotCommandResult exportDiagram(Digraph digraph, ViewPoint viewPoint) {
        try {
            Path workDirectory = Files.createTempDirectory("temp");
            workDirectory.toFile().deleteOnExit();

            Path sourcePath = workDirectory.resolve(jigProperties.dotFileName(viewPoint)).toAbsolutePath();
            Files.writeString(sourcePath, digraph.writeToString(jigProperties), StandardCharsets.UTF_8);
            logger.info("temporary DOT file: " + sourcePath);

            Path outputPath = jigProperties.outputPath(viewPoint);

            DotCommandRunner dotCommandRunner = new DotCommandRunner();
            return dotCommandRunner.run(jigProperties.outputFormat(), sourcePath, outputPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void prepareOutputDirectory(Path outputDirectory) {
        File file = outputDirectory.toFile();
        if (file.exists()) {
            if (file.isDirectory() && file.canWrite()) {
                // ディレクトリかつ書き込み可能なので対応不要
                return;
            }
            throw new IllegalStateException(file.getAbsolutePath() + " がディレクトリでないか書き込みできません。");
        }

        try {
            Files.createDirectories(outputDirectory);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
