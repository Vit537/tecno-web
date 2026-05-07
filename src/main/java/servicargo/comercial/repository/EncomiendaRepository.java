package servicargo.comercial.repository;

import servicargo.db.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EncomiendaRepository {
    public String insert(int clienteId, int productoId, int almacenId, String descripcion, String peso, String estado) {

        String sql = "INSERT INTO encomienda (cliente_id, producto_id, almacen_id, descripcion, peso, estado) " +
            "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clienteId);
            ps.setInt(2, productoId);
            ps.setInt(3, almacenId);
            ps.setString(4, descripcion);
            if (isBlank(peso)) {
                ps.setNull(5, java.sql.Types.NUMERIC);
            } else {
                ps.setBigDecimal(5, new java.math.BigDecimal(peso));
            }
            ps.setString(6, isBlank(estado) ? "RECIBIDO" : estado);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return "Encomienda creada. ID: " + rs.getInt(1);
            }
            return "Error: no se pudo crear encomienda.";
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    public String list() {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT id, cliente_id, producto_id, almacen_id, descripcion, peso, estado, created_at " +
            "FROM encomienda ORDER BY id";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
                sb.append("ID: ").append(rs.getInt("id")).append("\n");
                sb.append("Cliente ID: ").append(rs.getInt("cliente_id")).append("\n");
                sb.append("Producto ID: ").append(rs.getInt("producto_id")).append("\n");
                sb.append("Almacen ID: ").append(rs.getInt("almacen_id")).append("\n");
                sb.append("Descripcion: ").append(nvl(rs.getString("descripcion"))).append("\n");
                sb.append("Peso: ").append(nvl(rs.getBigDecimal("peso"))).append("\n");
                sb.append("Estado: ").append(rs.getString("estado")).append("\n");
                sb.append("Creado: ").append(rs.getTimestamp("created_at")).append("\n");
                sb.append("------------------\n");
            }
            if (count == 0) {
                return "No hay encomiendas.";
            }
            return sb.toString();
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    public String getById(int id) {
        String sql = "SELECT id, cliente_id, producto_id, almacen_id, descripcion, peso, estado, created_at " +
            "FROM encomienda WHERE id = ?";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                StringBuilder sb = new StringBuilder();
                sb.append("ID: ").append(rs.getInt("id")).append("\n");
                sb.append("Cliente ID: ").append(rs.getInt("cliente_id")).append("\n");
                sb.append("Producto ID: ").append(rs.getInt("producto_id")).append("\n");
                sb.append("Almacen ID: ").append(rs.getInt("almacen_id")).append("\n");
                sb.append("Descripcion: ").append(nvl(rs.getString("descripcion"))).append("\n");
                sb.append("Peso: ").append(nvl(rs.getBigDecimal("peso"))).append("\n");
                sb.append("Estado: ").append(rs.getString("estado")).append("\n");
                sb.append("Creado: ").append(rs.getTimestamp("created_at")).append("\n");
                return sb.toString();
            }
            return "No se encontro encomienda.";
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    public String updateEstado(int id, String estado) {
        if (isBlank(estado)) {
            return "Error: se requiere estado para actualizar.";
        }
        String sql = "UPDATE encomienda SET estado = ? WHERE id = ?";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                return "No se encontro encomienda con ID: " + id;
            }
            return "Encomienda actualizada. ID: " + id;
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    public String delete(String id) {
        if (isBlank(id)) {
            return "Error: se requiere id para eliminar.";
        }
        String sql = "DELETE FROM encomienda WHERE id = ?";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
            int deleted = ps.executeUpdate();
            if (deleted == 0) {
                return "No se encontro encomienda con ID: " + id;
            }
            return "Encomienda eliminada. ID: " + id;
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static String nvl(Object value) {
        return value == null ? "" : value.toString();
    }
}
