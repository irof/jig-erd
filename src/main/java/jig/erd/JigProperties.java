package jig.erd;

import jig.erd.domain.diagram.DocumentFormat;
import jig.erd.domain.diagram.ViewPoint;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class JigProperties {
    static Logger logger = Logger.getLogger(JigProperties.class.getName());

    Path outputDirectory = Paths.get(System.getProperty("user.dir"));
    String outputPrefix = "jig-erd";
    DocumentFormat outputFormat = DocumentFormat.SVG;
    String outputRankdir = "RL";
    Pattern filterSchemaPattern = null;

    public static JigProperties create() {
        JigProperties instance = new JigProperties();
        // 設定の読み込み、後勝ち
        instance.loadDirectoryConfig(Paths.get(System.getProperty("user.home")).resolve(".jig"));
        instance.loadDirectoryConfig(Paths.get(System.getProperty("user.dir")));
        instance.loadClasspathConfig();

        instance.prepareOutputDirectory();
        return instance;
    }

    void set(JigProperty jigProperty, String value) {
        try {
            switch (jigProperty) {
                case OUTPUT_DIRECTORY:
                    outputDirectory = Paths.get(value);
                    return;
                case OUTPUT_PREFIX:
                    if (value.matches("[a-zA-Z0-9-_.]+")) {
                        outputPrefix = value;
                    } else {
                        logger.warning(jigProperty + "は半角英数と-_.以外は使用できません。設定値は無視されます。");
                    }
                    return;
                case OUTPUT_FORMAT:
                    outputFormat = DocumentFormat.valueOf(value.toUpperCase(Locale.ENGLISH));
                    return;
                case OUTPUT_RANKDIR:
                    if (value.matches("(LR|TB|RL|BT)")) {
                        outputRankdir = value;
                    } else {
                        logger.warning(jigProperty + "はLR,RL,TB,BTのいずれかを指定してください。");
                    }
                    return;
                case FILTER_SCHEMA_PATTERN:
                    filterSchemaPattern = Pattern.compile(value);
                    return;
            }
        } catch (RuntimeException e) {
            logger.warning(jigProperty + "に無効な値が指定されました。設定は無視されます。");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.warning(baos.toString());
        }
    }

    public String dotFileName(ViewPoint viewPoint) {
        return outputPath(viewPoint, DocumentFormat.DOT);
    }

    public Path outputPath(ViewPoint viewPoint) {
        return outputDirectory.resolve(outputPath(viewPoint, outputFormat)).toAbsolutePath();
    }

    private void prepareOutputDirectory() {
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

    private String outputPath(ViewPoint viewPoint, DocumentFormat documentFormat) {
        return outputPrefix + '-' + viewPoint.suffix() + documentFormat.extension();
    }

    public DocumentFormat outputFormat() {
        return outputFormat;
    }

    public String rankdir() {
        return outputRankdir;
    }

    public Optional<Pattern> filterSchemaPattern() {
        return Optional.ofNullable(filterSchemaPattern);
    }

    enum JigProperty {
        OUTPUT_DIRECTORY,
        OUTPUT_PREFIX,
        OUTPUT_FORMAT,
        OUTPUT_RANKDIR,
        FILTER_SCHEMA_PATTERN,
        ;

        private void setIfExists(JigProperties jigProperties, Properties properties) {
            String key = "jig.erd." + name().toLowerCase().replace("_", ".");
            if (properties.containsKey(key)) {
                jigProperties.set(this, properties.getProperty(key));
            }
        }

        static void apply(JigProperties jigProperties, Properties properties) {
            for (JigProperty jigProperty : values()) {
                jigProperty.setIfExists(jigProperties, properties);
            }
        }
    }

    private void loadClasspathConfig() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("jig.properties")) {
            // 読めない場合はnullになる
            if (is != null) {
                try (Reader r = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                    Properties properties = new Properties();
                    properties.load(r);
                    logger.info(properties.toString());
                    JigProperty.apply(this, properties);
                }
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "クラスパス上のJIG設定ファイルの読み込みに失敗しました。設定無しで続行します。", e);
        }
    }

    private void loadDirectoryConfig(Path directory) {
        Path jigPropertiesPath = directory.resolve("jig.properties");
        if (jigPropertiesPath.toFile().exists()) {
            logger.warning(jigPropertiesPath.toAbsolutePath() + "をロードします。");
            try (Reader r = Files.newBufferedReader(jigPropertiesPath, StandardCharsets.UTF_8)) {
                Properties properties = new Properties();
                properties.load(r);
                logger.info(properties.toString());
                JigProperty.apply(this, properties);
            } catch (IOException e) {
                logger.log(Level.WARNING, "JIG設定ファイル'" + jigPropertiesPath + "'の読み込みに失敗しました。設定無しで続行します。", e);
            }
        }
    }
}
