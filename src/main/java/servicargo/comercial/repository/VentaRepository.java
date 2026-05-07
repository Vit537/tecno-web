package servicargo.comercial.repository;

import servicargo.db.DbConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VentaRepository {
    public String insert(int clienteId, int vendedorId, Integer cotizacionId, String total, String estado) {
        if (isBlank(total)) {
            return "Error: faltan campos obligatorios (total).";
        }

        String sql = "INSERT INTO venta (cliente_id, vendedor_id, cotizacion_id, total, estado) " +
            "VALUES (?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clienteId);
            ps.setInt(2, vendedorId);
            if (cotizacionId == null) {
                ps.setNull(3, java.sql.Types.INTEGER);
            } else {
                ps.setInt(3, cotizacionId);
            }
            ps.setBigDecimal(4, new BigDecimal(total));
            ps.setString(5, isBlank(estado) ? "PENDIENTE" : estado);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return "Venta creada. ID: " + rs.getInt(1);
            }
            return "Error: no se pudo crear venta.";
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    public String list() {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT id, cliente_id, vendedor_id, cotizacion_id, total, estado, created_at " +
            "FROM venta ORDER BY id";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
                sb.append("ID: ").append(rs.getInt("id")).append("\n");
                sb.append("Cliente ID: ").append(rs.getInt("cliente_id")).append("\n");
                sb.append("Vendedor ID: ").append(rs.getInt("vendedor_id")).append("\n");
                sb.append("Cotizacion ID: ").append(rs.getObject("cotizacion_id")).append("\n");
                sb.append("Total: ").append(rs.getBigDecimal("total")).append("\n");
                sb.append("Estado: ").append(rs.getString("estado")).append("\n");
                sb.append("Creado: ").append(rs.getTimestamp("created_at")).append("\n");
                sb.append("------------------\n");
            }
            if (count == 0) {
                return "No hay ventas.";
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
        String sql = "SELECT id, cliente_id, vendedor_id, cotizacion_id, total, estado, created_at " +
            "FROM venta WHERE id = ?";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                StringBuilder sb = new StringBuilder();
                sb.append("ID: ").append(rs.getInt("id")).append("\n");
                sb.append("Cliente ID: ").append(rs.getInt("cliente_id")).append("\n");
                sb.append("Vendedor ID: ").append(rs.getInt("vendedor_id")).append("\n");
                sb.append("Cotizacion ID: ").append(rs.getObject("cotizacion_id")).append("\n");
                sb.append("Total: ").append(rs.getBigDecimal("total")).append("\n");
                sb.append("Estado: ").append(rs.getString("estado")).append("\n");
                sb.append("Creado: ").append(rs.getTimestamp("created_at")).append("\n");
                return sb.toString();
            }
            return "No se encontro venta.";
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }


    public String delete(String id) {
        if (isBlank(id)) {
            return "Error: se requiere id para eliminar.";
        }
        String sql = "DELETE FROM venta WHERE id = ?";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
            int deleted = ps.executeUpdate();
            if (deleted == 0) {
                return "No se encontro venta con ID: " + id;
            }
            return "Venta eliminada. ID: " + id;
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
