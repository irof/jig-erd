package jig.erd.infrastructure.database;

import jig.erd.domain.ErdRoot;
import jig.erd.domain.environment.RDBMS;

import java.sql.Connection;
import java.sql.SQLException;

public class DataBaseDefinitionLoader {

    JdbcConnectionProvider jdbcConnectionProvider;
    Repository repository;

    public DataBaseDefinitionLoader(JdbcConnectionProvider jdbcConnectionProvider) {
        this.jdbcConnectionProvider = jdbcConnectionProvider;
        this.repository = new Repository();
    }

    public ErdRoot load() {
        try (Connection conn = jdbcConnectionProvider.getConnection()) {
            String url = conn.getMetaData().getURL();

            RDBMS rdbms = RDBMS.from(url);

            // 気が向いたら多態にする
            if (rdbms == RDBMS.H2) {
                H2Loader loader = new H2Loader(repository);
                loader.load(conn);
            } else if (rdbms == RDBMS.POSTGRESQL) {
                PsqlLoader loader = new PsqlLoader(repository);
                loader.load(conn);
            }
            return repository.buildErdRoot();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
