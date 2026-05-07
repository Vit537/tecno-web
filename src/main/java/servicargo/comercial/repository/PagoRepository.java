package servicargo.comercial.repository;

import servicargo.db.DbConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PagoRepository {
    public String insert(int ventaId, String monto, String metodo) {
        if (isBlank(monto) || isBlank(metodo)) {
            return "Error: faltan campos obligatorios (monto, metodo).";
        }

        String sql = "INSERT INTO pago (venta_id, monto, metodo) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ventaId);
            ps.setBigDecimal(2, new BigDecimal(monto));
            ps.setString(3, metodo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return "Pago registrado. ID: " + rs.getInt(1);
            }
            return "Error: no se pudo registrar pago.";
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    public String list() {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT id, venta_id, monto, metodo, fecha FROM pago ORDER BY id";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
                sb.append("ID: ").append(rs.getInt("id")).append("\n");
                sb.append("Venta ID: ").append(rs.getInt("venta_id")).append("\n");
                sb.append("Monto: ").append(rs.getBigDecimal("monto")).append("\n");
                sb.append("Metodo: ").append(rs.getString("metodo")).append("\n");
                sb.append("Fecha: ").append(rs.getTimestamp("fecha")).append("\n");
                sb.append("------------------\n");
            }
            if (count == 0) {
                return "No hay pagos.";
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
        String sql = "SELECT id, venta_id, monto, metodo, fecha FROM pago WHERE id = ?";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                StringBuilder sb = new StringBuilder();
                sb.append("ID: ").append(rs.getInt("id")).append("\n");
                sb.append("Venta ID: ").append(rs.getInt("venta_id")).append("\n");
                sb.append("Monto: ").append(rs.getBigDecimal("monto")).append("\n");
                sb.append("Metodo: ").append(rs.getString("metodo")).append("\n");
                sb.append("Fecha: ").append(rs.getTimestamp("fecha")).append("\n");
                return sb.toString();
            }
            return "No se encontro pago.";
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    public String update(String id) {
        return "Operacion no soportada.";
    }

    public String delete(String id) {
        if (isBlank(id)) {
            return "Error: se requiere id para eliminar.";
        }
        String sql = "DELETE FROM pago WHERE id = ?";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
            int deleted = ps.executeUpdate();
            if (deleted == 0) {
                return "No se encontro pago con ID: " + id;
            }
            return "Pago eliminado. ID: " + id;
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
