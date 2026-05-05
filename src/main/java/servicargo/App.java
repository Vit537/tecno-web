package servicargo;

import servicargo.db.SchemaInitializer;

public class App {
    public static void main(String[] args) {
        try {
            SchemaInitializer.createCoreTables();
            System.out.println("Tablas de usuarios y almacenes creadas.");
        } catch (Exception e) {
            System.err.println("Error creando tablas: " + e.getMessage());
        }
    }
}
