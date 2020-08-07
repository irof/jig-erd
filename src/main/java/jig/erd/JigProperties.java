package jig.erd;

import jig.erd.domain.diagram.DocumentFormat;
import jig.erd.domain.diagram.ViewPoint;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;

public class JigProperties {
    static Logger logger = Logger.getLogger(JigProperties.class.getName());

    Path outputDirectory = Paths.get(System.getProperty("user.dir"));
    String outputPrefix = "jig-erd";
    DocumentFormat outputFormat = DocumentFormat.SVG;

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
            }
        } catch (RuntimeException e) {
            logger.warning(jigProperty + "に無効な値が指定されました。設定は無視されます。");
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

    enum JigProperty {
        OUTPUT_DIRECTORY,
        OUTPUT_PREFIX,
        OUTPUT_FORMAT;

        void setIfExists(JigProperties jigProperties, Properties properties) {
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

    public void load() {
        loadClasspathConfig();
        loadCurrentDirectoryConfig();
    }

    private void loadClasspathConfig() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("jig.properties")) {
            // 読めない場合はnullになる
            if (is != null) {
                Properties properties = new Properties();
                properties.load(is);
                JigProperty.apply(this, properties);
            }
        } catch (IOException e) {
            logger.warning("JIG設定ファイルの読み込みに失敗しました。設定無しで続行します。" + e.toString());
        }
    }

    private void loadCurrentDirectoryConfig() {
        String userDir = System.getProperty("user.dir");
        Path workDirPath = Paths.get(userDir);
        Path jigPropertiesPath = workDirPath.resolve("jig.properties");
        if (jigPropertiesPath.toFile().exists()) {
            logger.warning(jigPropertiesPath.toAbsolutePath() + "をロードします。");
            try (InputStream is = Files.newInputStream(jigPropertiesPath)) {
                Properties properties = new Properties();
                properties.load(is);
                JigProperty.apply(this, properties);
            } catch (IOException e) {
                logger.warning("JIG設定ファイルのロードに失敗しました。" + e.toString());
            }
        }
    }

    static ThreadLocal<JigProperties> holder = ThreadLocal.withInitial(() -> new JigProperties());

    public static JigProperties get() {
        return holder.get();
    }
}
