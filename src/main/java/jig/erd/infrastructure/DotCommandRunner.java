package jig.erd.infrastructure;

import jig.erd.domain.diagram.DocumentFormat;
import jig.erd.infrastructure.process.DotProcessExecutor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public class DotCommandRunner {
    Logger logger = Logger.getLogger(DotCommandRunner.class.getName());

    DotProcessExecutor dotProcessExecutor = new DotProcessExecutor();

    public DotCommandResult run(DocumentFormat documentFormat, Path inputPath, Path outputPath) throws IOException {
        if (documentFormat == DocumentFormat.DOT) {
            Files.move(inputPath, outputPath);
            return DotCommandResult.success();
        }

        String[] options = {documentFormat.dotOption(), "-o" + outputPath, inputPath.toString()};
        DotCommandResult result = dotProcessExecutor.execute(options);

        if (result.succeed()) {
            logger.info("image file: " + outputPath);
            logger.info("delete DOT file.");
            Files.deleteIfExists(inputPath);
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
