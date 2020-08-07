package jig.erd.infrastructure;

import jig.erd.infrastructure.process.DotProcessExecutor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public class DotCommandRunner {
    Logger logger = Logger.getLogger(DotCommandRunner.class.getName());

    DotProcessExecutor dotProcessExecutor = new DotProcessExecutor();

    public DotCommandResult run(Path inputPath, Path outputPath) throws IOException {
        String[] options = {"-Tsvg", "-o" + outputPath, inputPath.toString()};
        DotCommandResult result = dotProcessExecutor.execute(options);

        if (result.succeed()) {
            logger.info("image file: " + outputPath);
            logger.info("delete DOT file.");
            Files.deleteIfExists(inputPath);
        }

        return result;
    }

    public static void main(String[] args) {
        DotCommandResult result = new DotCommandRunner().runVersion();
        System.out.println(result.message);
    }

    public DotCommandResult runVersion() {
        return dotProcessExecutor.execute("-V");
    }
}
