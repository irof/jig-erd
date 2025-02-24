package jig.erd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.assertFalse;

class JigErdTest {
    @TempDir
    Path tempDir;

    /**
     * @link <a href="https://github.com/irof/jig-erd/issues/91">h2でMODE=PostgreSQLで動作させるとpg_catalogスキーマが出力される</a>
     */
    @Test
    void H2のPostgreSQLモードでPG_CATALOGが出力されない() throws Exception {

        try (var conn = DriverManager.getConnection("jdbc:h2:mem:test;mode=PostgreSQL;DB_CLOSE_ON_EXIT=FALSE", "sa", "");
             var st = conn.createStatement()) {
            st.execute("CREATE TABLE HOGE(id INTEGER PRIMARY KEY)");
            st.execute("CREATE TABLE FUGA(id INTEGER PRIMARY KEY REFERENCES HOGE(id))");

            JigErd instance = new JigErd(() -> conn);

            instance.jigProperties.set(JigProperties.JigProperty.OUTPUT_DIRECTORY, tempDir.toString());
            instance.jigProperties.set(JigProperties.JigProperty.OUTPUT_FORMAT, "dot");

            instance.run();
        }

        var 出力ファイル = tempDir.resolve("jig-erd-detail.dot");
        var 出力内容 = Files.readString(出力ファイル);

        assertFalse(出力内容.contains("PG_CATALOG"), 出力内容);
    }
}