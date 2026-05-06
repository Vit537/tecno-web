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
            stmt.executeUpdate(PRODUCTOS_TABLE_SQL);
            stmt.executeUpdate(INVENTARIO_TABLE_SQL);
            stmt.executeUpdate(ENCOMIENDAS_TABLE_SQL);
            stmt.executeUpdate(COTIZACIONES_TABLE_SQL);
            stmt.executeUpdate(VENTAS_TABLE_SQL);
            stmt.executeUpdate(PAGOS_TABLE_SQL);
            stmt.executeUpdate(FACTURAS_TABLE_SQL);
        }
    }

    private static final String USERS_TABLE_SQL =
        "CREATE TABLE IF NOT EXISTS usuario (" +
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
        "CREATE TABLE IF NOT EXISTS almacen (" +
        "id SERIAL PRIMARY KEY, " +
        "nombre VARCHAR(120) NOT NULL, " +
        "direccion VARCHAR(200) NOT NULL, " +
        "capacidad INTEGER NOT NULL, " +
        "responsable VARCHAR(120) NOT NULL, " +
        "telefono VARCHAR(30), " +
        "created_at TIMESTAMP NOT NULL DEFAULT NOW()" +
        ")";

    private static final String PRODUCTOS_TABLE_SQL =
        "CREATE TABLE IF NOT EXISTS producto (" +
        "id SERIAL PRIMARY KEY, " +
        "codigo VARCHAR(40) NOT NULL UNIQUE, " +
        "nombre VARCHAR(120) NOT NULL, " +
        "descripcion TEXT, " +
        "precio NUMERIC(10,2) NOT NULL, " +
        "created_at TIMESTAMP NOT NULL DEFAULT NOW()" +
        ")";

    private static final String INVENTARIO_TABLE_SQL =
        "CREATE TABLE IF NOT EXISTS inventario (" +
        "id SERIAL PRIMARY KEY, " +
        "producto_id INTEGER NOT NULL REFERENCES producto(id), " +
        "almacen_id INTEGER NOT NULL REFERENCES almacen(id), " +
        "cantidad INTEGER NOT NULL, " +
        "updated_at TIMESTAMP NOT NULL DEFAULT NOW(), " +
        "UNIQUE (producto_id, almacen_id)" +
        ")";

    private static final String ENCOMIENDAS_TABLE_SQL =
        "CREATE TABLE IF NOT EXISTS encomienda (" +
        "id SERIAL PRIMARY KEY, " +
        "cliente_id INTEGER NOT NULL REFERENCES usuario(id), " +
        "producto_id INTEGER NOT NULL REFERENCES producto(id), " +
        "almacen_id INTEGER NOT NULL REFERENCES almacen(id), " +
        "descripcion TEXT, " +
        "peso NUMERIC(10,2), " +
        "estado VARCHAR(30) NOT NULL DEFAULT 'RECIBIDO', " +
        "created_at TIMESTAMP NOT NULL DEFAULT NOW()" +
        ")";

    private static final String COTIZACIONES_TABLE_SQL =
        "CREATE TABLE IF NOT EXISTS cotizacion (" +
        "id SERIAL PRIMARY KEY, " +
        "cliente_id INTEGER NOT NULL REFERENCES usuario(id), " +
        "vendedor_id INTEGER NOT NULL REFERENCES usuario(id), " +
        "producto_id INTEGER NOT NULL REFERENCES producto(id), " +
        "cantidad INTEGER NOT NULL, " +
        "precio_unit NUMERIC(10,2) NOT NULL, " +
        "total NUMERIC(10,2) NOT NULL, " +
        "estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE', " +
        "created_at TIMESTAMP NOT NULL DEFAULT NOW()" +
        ")";

    private static final String VENTAS_TABLE_SQL =
        "CREATE TABLE IF NOT EXISTS venta (" +
        "id SERIAL PRIMARY KEY, " +
        "cliente_id INTEGER NOT NULL REFERENCES usuario(id), " +
        "vendedor_id INTEGER NOT NULL REFERENCES usuario(id), " +
        "cotizacion_id INTEGER REFERENCES cotizacion(id), " +
        "total NUMERIC(10,2) NOT NULL, " +
        "estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE', " +
        "created_at TIMESTAMP NOT NULL DEFAULT NOW()" +
        ")";

    private static final String PAGOS_TABLE_SQL =
        "CREATE TABLE IF NOT EXISTS pago (" +
        "id SERIAL PRIMARY KEY, " +
        "venta_id INTEGER NOT NULL REFERENCES venta(id), " +
        "monto NUMERIC(10,2) NOT NULL, " +
        "metodo VARCHAR(30) NOT NULL, " +
        "fecha TIMESTAMP NOT NULL DEFAULT NOW()" +
        ")";

    private static final String FACTURAS_TABLE_SQL =
        "CREATE TABLE IF NOT EXISTS factura (" +
        "id SERIAL PRIMARY KEY, " +
        "venta_id INTEGER NOT NULL REFERENCES venta(id), " +
        "nit VARCHAR(20) NOT NULL, " +
        "razon_social VARCHAR(120) NOT NULL, " +
        "total NUMERIC(10,2) NOT NULL, " +
        "fecha TIMESTAMP NOT NULL DEFAULT NOW()" +
        ")";
}
