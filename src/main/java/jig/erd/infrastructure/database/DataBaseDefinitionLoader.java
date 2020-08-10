package jig.erd.infrastructure.database;

import jig.erd.application.repository.Repository;
import jig.erd.domain.environment.RDBMS;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataBaseDefinitionLoader {

    DataSource dataSource;
    Repository repository;

    public DataBaseDefinitionLoader(DataSource dataSource, Repository repository) {
        this.dataSource = dataSource;
        this.repository = repository;
    }

    public void load() {
        try (Connection conn = dataSource.getConnection()) {
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
