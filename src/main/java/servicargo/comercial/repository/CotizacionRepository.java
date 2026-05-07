package servicargo.comercial.repository;

import servicargo.db.DbConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CotizacionRepository {
    public String insert(int clienteId, int vendedorId, int productoId, int cantidad, String precioUnit, String estado) {
        if (isBlank(precioUnit)) {
            return "Error: faltan campos obligatorios (precio_unit).";
        }

        BigDecimal total = new BigDecimal(precioUnit).multiply(new BigDecimal(cantidad));

        String sql = "INSERT INTO cotizacion (cliente_id, vendedor_id, producto_id, cantidad, precio_unit, total, estado) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clienteId);
            ps.setInt(2, vendedorId);
            ps.setInt(3, productoId);
            ps.setInt(4, cantidad);
            ps.setBigDecimal(5, new BigDecimal(precioUnit));
            ps.setBigDecimal(6, total);
            ps.setString(7, isBlank(estado) ? "PENDIENTE" : estado);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return "Cotizacion creada. ID: " + rs.getInt(1) + " Total: " + total;
            }
            return "Error: no se pudo crear cotizacion.";
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    public String list() {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT id, cliente_id, vendedor_id, producto_id, cantidad, precio_unit, total, estado, created_at " +
            "FROM cotizacion ORDER BY id";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
                sb.append("ID: ").append(rs.getInt("id")).append("\n");
                sb.append("Cliente ID: ").append(rs.getInt("cliente_id")).append("\n");
                sb.append("Vendedor ID: ").append(rs.getInt("vendedor_id")).append("\n");
                sb.append("Producto ID: ").append(rs.getInt("producto_id")).append("\n");
                sb.append("Cantidad: ").append(rs.getInt("cantidad")).append("\n");
                sb.append("Precio Unit: ").append(rs.getBigDecimal("precio_unit")).append("\n");
                sb.append("Total: ").append(rs.getBigDecimal("total")).append("\n");
                sb.append("Estado: ").append(rs.getString("estado")).append("\n");
                sb.append("Creado: ").append(rs.getTimestamp("created_at")).append("\n");
                sb.append("------------------\n");
            }
            if (count == 0) {
                return "No hay cotizaciones.";
            }
            return sb.toString();
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    public String getById(int id) {
        String sql = "SELECT id, cliente_id, vendedor_id, producto_id, cantidad, precio_unit, total, estado, created_at " +
            "FROM cotizacion WHERE id = ?";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                StringBuilder sb = new StringBuilder();
                sb.append("ID: ").append(rs.getInt("id")).append("\n");
                sb.append("Cliente ID: ").append(rs.getInt("cliente_id")).append("\n");
                sb.append("Vendedor ID: ").append(rs.getInt("vendedor_id")).append("\n");
                sb.append("Producto ID: ").append(rs.getInt("producto_id")).append("\n");
                sb.append("Cantidad: ").append(rs.getInt("cantidad")).append("\n");
                sb.append("Precio Unit: ").append(rs.getBigDecimal("precio_unit")).append("\n");
                sb.append("Total: ").append(rs.getBigDecimal("total")).append("\n");
                sb.append("Estado: ").append(rs.getString("estado")).append("\n");
                sb.append("Creado: ").append(rs.getTimestamp("created_at")).append("\n");
                return sb.toString();
            }
            return "No se encontro cotizacion.";
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    public String updateEstado(int id, String estado) {
        if (isBlank(estado)) {
            return "Error: se requiere estado para actualizar.";
        }
        String sql = "UPDATE cotizacion SET estado = ? WHERE id = ?";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                return "No se encontro cotizacion con ID: " + id;
            }
            return "Cotizacion actualizada. ID: " + id;
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    public String delete(String id) {
        if (isBlank(id)) {
            return "Error: se requiere id para eliminar.";
        }
        String sql = "DELETE FROM cotizacion WHERE id = ?";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
            int deleted = ps.executeUpdate();
            if (deleted == 0) {
                return "No se encontro cotizacion con ID: " + id;
            }
            return "Cotizacion eliminada. ID: " + id;
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
