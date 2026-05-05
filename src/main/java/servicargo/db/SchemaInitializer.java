package servicargo.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class SchemaInitializer {
    private SchemaInitializer() {}

    public static void createCoreTables() throws SQLException {
        try (Connection conn = DbConnection.open();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(USERS_TABLE_SQL);
            stmt.executeUpdate(ALMACENES_TABLE_SQL);
        }
    }

    private static final String USERS_TABLE_SQL =
        "CREATE TABLE IF NOT EXISTS usuarios (" +
        "id SERIAL PRIMARY KEY, " +
        "ci VARCHAR(20) NOT NULL UNIQUE, " +
        "nombre VARCHAR(100) NOT NULL, " +
        "apellido VARCHAR(100) NOT NULL, " +
        "rol VARCHAR(30) NOT NULL, " +
        "telefono VARCHAR(30), " +
        "email VARCHAR(120) NOT NULL UNIQUE, " +
        "password VARCHAR(120) NOT NULL, " +
        "created_at TIMESTAMP NOT NULL DEFAULT NOW()" +
        ")";

    private static final String ALMACENES_TABLE_SQL =
        "CREATE TABLE IF NOT EXISTS almacenes (" +
        "id SERIAL PRIMARY KEY, " +
        "nombre VARCHAR(120) NOT NULL, " +
        "direccion VARCHAR(200) NOT NULL, " +
        "capacidad INTEGER NOT NULL, " +
        "responsable VARCHAR(120) NOT NULL, " +
        "telefono VARCHAR(30), " +
        "created_at TIMESTAMP NOT NULL DEFAULT NOW()" +
        ")";
}
