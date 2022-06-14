package jig.erd.infrastructure.database;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.*;

class H2LoaderTest {

    @Test
    void test() throws Exception {
        Repository repository = new Repository();
        H2Loader h2Loader = new H2Loader(repository);

        assertDoesNotThrow(() -> {
            try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:")) {
                h2Loader.load(conn);
            }
        });
    }
}