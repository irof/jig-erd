package jig.erd.infrastructure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DotCommandRunnerTest {

    @Test
    void testVersion() throws Exception {
        DotCommandRunner sut = new DotCommandRunner();

        DotCommandResult result = sut.runVersion();

        assertTrue(result.message().startsWith("dot - graphviz version 2."), result.toString());
    }
}