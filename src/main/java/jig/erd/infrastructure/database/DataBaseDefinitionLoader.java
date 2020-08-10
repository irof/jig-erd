package jig.erd.infrastructure.database;

import jig.erd.application.repository.Repository;
import jig.erd.domain.environment.RDBMS;

import java.sql.Connection;
import java.sql.SQLException;

public class DataBaseDefinitionLoader {

    JdbcConnectionProvider jdbcConnectionProvider;
    Repository repository;

    public DataBaseDefinitionLoader(JdbcConnectionProvider jdbcConnectionProvider, Repository repository) {
        this.jdbcConnectionProvider = jdbcConnectionProvider;
        this.repository = repository;
    }

    public void load() {
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
