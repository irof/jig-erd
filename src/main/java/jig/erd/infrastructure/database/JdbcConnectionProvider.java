package jig.erd.infrastructure.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface JdbcConnectionProvider {

    Connection getConnection() throws SQLException;
}
