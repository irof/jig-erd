package jig.erd.infrastructure;

import jig.erd.domain.diagram.DocumentFormat;
import jig.erd.infrastructure.process.DotProcessExecutor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

public class DotCommandRunner {
    Logger logger = Logger.getLogger(DotCommandRunner.class.getName());

    DotProcessExecutor dotProcessExecutor = new DotProcessExecutor();

    public DotCommandResult run(DocumentFormat documentFormat, Path inputPath, Path outputPath) throws IOException {
        if (documentFormat == DocumentFormat.DOT) {
            Files.move(inputPath, outputPath, StandardCopyOption.REPLACE_EXISTING);
            return DotCommandResult.success();
        }

        String[] options = {documentFormat.dotOption(), "-o" + outputPath, inputPath.toString()};
        DotCommandResult result = dotProcessExecutor.execute(options);

        if (result.succeed()) {
            logger.info("image file: " + outputPath);
            logger.info("delete DOT file.");
            Files.deleteIfExists(inputPath);
        } else {
            // 失敗時にWikiにリンクする
            logger.info("画像化に失敗しました。 https://github.com/irof/jig-erd/wiki/FAQ-GraphvizOutput を確認してください。DOT file Path: " + inputPath);
        }

        return result;
    }

    public DotCommandResult runVersion() {
        DotCommandResult result = dotProcessExecutor.execute("-V");
        if (result.failed()) {
            return result.withMessage("Graphvizのバージョン取得に失敗しました。インストール状況を確認してください。");
        }
        return result;
    }
}
