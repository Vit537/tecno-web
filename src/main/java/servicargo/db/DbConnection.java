package servicargo.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DbConnection {
    private DbConnection() {}

    public static Connection open() throws SQLException {
        return DriverManager.getConnection(DbConfig.DB_URL, DbConfig.DB_USER, DbConfig.DB_PASS);
    }
}
