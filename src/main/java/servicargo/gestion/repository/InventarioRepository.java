package servicargo.gestion.repository;

import servicargo.db.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InventarioRepository {
    public static class InventarioItem {
        public final int id;
        public final int cantidad;

        public InventarioItem(int id, int cantidad) {
            this.id = id;
            this.cantidad = cantidad;
        }
    }

    public InventarioItem findByProductoAlmacen(int productoId, int almacenId) {
        String sql = "SELECT id, cantidad FROM inventario WHERE producto_id = ? AND almacen_id = ?";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productoId);
            ps.setInt(2, almacenId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new InventarioItem(rs.getInt("id"), rs.getInt("cantidad"));
            }
            return null;
        } catch (SQLException e) {
            return null;
        }
    }

    public String insert(int productoId, int almacenId, int cantidad) {
        String sql = "INSERT INTO inventario (producto_id, almacen_id, cantidad) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productoId);
            ps.setInt(2, almacenId);
            ps.setInt(3, cantidad);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return "Inventario creado. ID: " + rs.getInt(1);
            }
            return "Error: no se pudo crear inventario.";
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    public String updateCantidad(int id, int cantidad) {
        String sql = "UPDATE inventario SET cantidad = ?, updated_at = NOW() WHERE id = ?";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, id);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                return "No se encontro inventario con ID: " + id;
            }
            return "Inventario actualizado. ID: " + id;
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    public String listAll() {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT id, producto_id, almacen_id, cantidad, updated_at FROM inventario ORDER BY id";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
                appendRow(sb, rs);
            }
            if (count == 0) {
                return "No hay inventario.";
            }
            return sb.toString();
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    public String listByAlmacen(int almacenId) {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT id, producto_id, almacen_id, cantidad, updated_at FROM inventario WHERE almacen_id = ? ORDER BY id";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, almacenId);
            ResultSet rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
                appendRow(sb, rs);
            }
            if (count == 0) {
                return "No hay inventario.";
            }
            return sb.toString();
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    private static void appendRow(StringBuilder sb, ResultSet rs) throws SQLException {
        sb.append("ID: ").append(rs.getInt("id")).append("\n");
        sb.append("Producto ID: ").append(rs.getInt("producto_id")).append("\n");
        sb.append("Almacen ID: ").append(rs.getInt("almacen_id")).append("\n");
        sb.append("Cantidad: ").append(rs.getInt("cantidad")).append("\n");
        sb.append("Actualizado: ").append(rs.getTimestamp("updated_at")).append("\n");
        sb.append("------------------\n");
    }
}
