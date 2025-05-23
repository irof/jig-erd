package jig.erd;

import jig.erd.domain.diagram.DocumentFormat;
import jig.erd.domain.diagram.ViewPoint;
import jig.erd.domain.graphviz.DotAttributes;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class JigProperties {
    static Logger logger = Logger.getLogger(JigProperties.class.getName());

    Path outputDirectory = Paths.get(System.getProperty("user.dir"));
    String outputPrefix = "jig-erd";
    DocumentFormat outputFormat = DocumentFormat.SVG;
    Pattern filterSchemaPattern = null;

    private final Map<String, String> attributesMap = new HashMap<>();

    public static JigProperties create() {
        JigProperties instance = new JigProperties();
        // 設定の読み込み、後勝ち
        instance.loadDirectoryConfig(Paths.get(System.getProperty("user.home")).resolve(".jig"));
        instance.loadDirectoryConfig(Paths.get(System.getProperty("user.dir")));
        instance.loadClasspathConfig();
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
                    logger.warning("jig.erd.output.rankdir は廃止されます。代わりに `jig.erd.dot.root.rankdir` を使用してください。");
                    if (value.matches("(LR|TB|RL|BT)")) {
                        attributesMap.put(DotAttributes.Keys.ROOT_RANKDIR, value);
                    } else {
                        logger.warning(jigProperty + "はLR,RL,TB,BTのいずれかを指定してください。設定値は無視されます。");
                    }
                    return;
                case FILTER_SCHEMA_PATTERN:
                    filterSchemaPattern = Pattern.compile(value);
                    return;
            }
        } catch (RuntimeException e) {
            logger.log(Level.WARNING, jigProperty + "に無効な値が指定されました。設定値は無視されます。", e);
        }
    }

    public String dotFileName(ViewPoint viewPoint) {
        return outputPath(viewPoint, DocumentFormat.DOT);
    }

    public Path outputPath(ViewPoint viewPoint) {
        return outputDirectory.resolve(outputPath(viewPoint, outputFormat)).toAbsolutePath();
    }

    private String outputPath(ViewPoint viewPoint, DocumentFormat documentFormat) {
        return outputPrefix + '-' + viewPoint.suffix() + documentFormat.extension();
    }

    public DocumentFormat outputFormat() {
        return outputFormat;
    }

    public Optional<Pattern> filterSchemaPattern() {
        return Optional.ofNullable(filterSchemaPattern);
    }

    public DotAttributes toDotAttributes() {
        return new DotAttributes(attributesMap);
    }

    enum JigProperty {
        OUTPUT_DIRECTORY,
        OUTPUT_PREFIX,
        OUTPUT_FORMAT,
        OUTPUT_RANKDIR,
        FILTER_SCHEMA_PATTERN,
        ;

        private void setIfExists(JigProperties jigProperties, Map<String, String> map) {
            String key = "jig.erd." + name().toLowerCase().replace("_", ".");
            if (map.containsKey(key)) {
                jigProperties.set(this, map.get(key));
            }
        }

        static void apply(JigProperties jigProperties, Map<String, String> map) {
            for (JigProperty jigProperty : values()) {
                jigProperty.setIfExists(jigProperties, map);
            }
        }
    }

    private void loadClasspathConfig() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("jig.properties")) {
            // 読めない場合はnullになる
            if (is != null) {
                logger.info("クラスパスから jig.properties をロードします。");
                try (Reader r = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                    loadProperties(r);
                }
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "クラスパス上のJIG設定ファイルの読み込みに失敗しました。設定無しで続行します。", e);
        }
    }

    private void loadDirectoryConfig(Path directory) {
        Path jigPropertiesPath = directory.resolve("jig.properties");
        if (jigPropertiesPath.toFile().exists()) {
            logger.info(jigPropertiesPath.toAbsolutePath() + "をロードします。");
            try (Reader r = Files.newBufferedReader(jigPropertiesPath, StandardCharsets.UTF_8)) {
                loadProperties(r);
            } catch (IOException e) {
                logger.log(Level.WARNING, "JIG設定ファイル'" + jigPropertiesPath + "'の読み込みに失敗しました。設定無しで続行します。", e);
            }
        }
    }

    private void loadProperties(Reader r) throws IOException {
        Properties properties = new Properties();
        properties.load(r);
        logger.info(properties.toString());

        HashMap<String, String> map = new HashMap<>();
        properties.forEach((key, value) -> {
            map.put(String.valueOf(key), String.valueOf(value));
        });
        loadMapConfig(map);
    }

    public void loadMapConfig(Map<String, String> map) {
        logger.info(map.toString());
        JigProperty.apply(this, map);
        registerDotAttributes(map);
    }

    private void registerDotAttributes(Map<String, String> map) {
        map.forEach((key, newValue) -> {
            if (key.startsWith(DotAttributes.Keys.PREFIX)) {
                String oldValue = attributesMap.put(key, newValue);
                if (oldValue != null) {
                    logger.log(Level.INFO, String.format("override '%s': '%s' => '%s'", key, oldValue, newValue));
                }
            }
        });
    }
}
