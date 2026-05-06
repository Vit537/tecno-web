package servicargo.dao;

import servicargo.db.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InventarioDao {
    public String insert(Map<String, String> data) {
        String productoId = data.get("producto_id");
        String almacenId = data.get("almacen_id");
        String cantidad = data.get("cantidad");

        if (isBlank(productoId) || isBlank(almacenId) || isBlank(cantidad)) {
            return "Error: faltan campos obligatorios (producto_id, almacen_id, cantidad).";
        }

        String sql = "INSERT INTO inventario (producto_id, almacen_id, cantidad) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(productoId));
            ps.setInt(2, Integer.parseInt(almacenId));
            ps.setInt(3, Integer.parseInt(cantidad));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return "Inventario creado. ID: " + rs.getInt(1);
            }
            return "Error: no se pudo crear inventario.";
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        } catch (NumberFormatException e) {
            return "Error: producto_id, almacen_id y cantidad deben ser numeros.";
        }
    }

    public String list() {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT id, producto_id, almacen_id, cantidad, updated_at FROM inventario ORDER BY id";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
                sb.append("ID: ").append(rs.getInt("id")).append("\n");
                sb.append("Producto ID: ").append(rs.getInt("producto_id")).append("\n");
                sb.append("Almacen ID: ").append(rs.getInt("almacen_id")).append("\n");
                sb.append("Cantidad: ").append(rs.getInt("cantidad")).append("\n");
                sb.append("Actualizado: ").append(rs.getTimestamp("updated_at")).append("\n");
                sb.append("------------------\n");
            }
            if (count == 0) {
                return "No hay inventario.";
            }
            return sb.toString();
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    public String getById(String id) {
        if (isBlank(id)) {
            return "Error: se requiere id.";
        }
        String sql = "SELECT id, producto_id, almacen_id, cantidad, updated_at FROM inventario WHERE id = ?";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                StringBuilder sb = new StringBuilder();
                sb.append("ID: ").append(rs.getInt("id")).append("\n");
                sb.append("Producto ID: ").append(rs.getInt("producto_id")).append("\n");
                sb.append("Almacen ID: ").append(rs.getInt("almacen_id")).append("\n");
                sb.append("Cantidad: ").append(rs.getInt("cantidad")).append("\n");
                sb.append("Actualizado: ").append(rs.getTimestamp("updated_at")).append("\n");
                return sb.toString();
            }
            return "No se encontro inventario.";
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    public String update(Map<String, String> data) {
        String id = data.get("id");
        if (isBlank(id)) {
            return "Error: se requiere id para actualizar.";
        }

        List<String> fields = new ArrayList<>();
        List<String> values = new ArrayList<>();
        addField(fields, values, "producto_id", data.get("producto_id"));
        addField(fields, values, "almacen_id", data.get("almacen_id"));
        addField(fields, values, "cantidad", data.get("cantidad"));

        if (fields.isEmpty()) {
            return "Error: no hay campos para actualizar.";
        }

        StringBuilder sql = new StringBuilder("UPDATE inventario SET ");
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) sql.append(", ");
            sql.append(fields.get(i)).append(" = ?");
        }
        sql.append(", updated_at = NOW() WHERE id = ?");

        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            for (String val : values) {
                ps.setInt(idx++, Integer.parseInt(val));
            }
            ps.setInt(idx, Integer.parseInt(id));
            int updated = ps.executeUpdate();
            if (updated == 0) {
                return "No se encontro inventario con ID: " + id;
            }
            return "Inventario actualizado. ID: " + id;
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        } catch (NumberFormatException e) {
            return "Error: producto_id, almacen_id y cantidad deben ser numeros.";
        }
    }

    public String delete(String id) {
        if (isBlank(id)) {
            return "Error: se requiere id para eliminar.";
        }
        String sql = "DELETE FROM inventario WHERE id = ?";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
            int deleted = ps.executeUpdate();
            if (deleted == 0) {
                return "No se encontro inventario con ID: " + id;
            }
            return "Inventario eliminado. ID: " + id;
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    private static void addField(List<String> fields, List<String> values, String field, String value) {
        if (!isBlank(value)) {
            fields.add(field);
            values.add(value);
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
