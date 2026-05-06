package servicargo;

import servicargo.db.SchemaInitializer;

public class App {
    public static void main(String[] args) {
        try {
            SchemaInitializer.createCoreTables();
            System.out.println("Se crearon las tablas core.");
        } catch (Exception e) {
            System.err.println("Error creando tablas: " + e.getMessage());
        }
    }
}
