package servicargo.dao;

import servicargo.db.DbConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VentaDao {
    public String insert(Map<String, String> data) {
        String clienteId = data.get("cliente_id");
        String vendedorId = data.get("vendedor_id");
        String cotizacionId = data.get("cotizacion_id");
        String total = data.get("total");
        String estado = data.get("estado");

        if (isBlank(clienteId) || isBlank(vendedorId) || isBlank(total)) {
            return "Error: faltan campos obligatorios (cliente_id, vendedor_id, total).";
        }

        String sql = "INSERT INTO venta (cliente_id, vendedor_id, cotizacion_id, total, estado) " +
            "VALUES (?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(clienteId));
            ps.setInt(2, Integer.parseInt(vendedorId));
            if (isBlank(cotizacionId)) {
                ps.setNull(3, java.sql.Types.INTEGER);
            } else {
                ps.setInt(3, Integer.parseInt(cotizacionId));
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
        } catch (NumberFormatException e) {
            return "Error: ids y total deben ser numeros.";
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

    public String update(Map<String, String> data) {
        String id = data.get("id");
        if (isBlank(id)) {
            return "Error: se requiere id para actualizar.";
        }

        List<String> fields = new ArrayList<>();
        List<String> values = new ArrayList<>();
        addField(fields, values, "cliente_id", data.get("cliente_id"));
        addField(fields, values, "vendedor_id", data.get("vendedor_id"));
        addField(fields, values, "cotizacion_id", data.get("cotizacion_id"));
        addField(fields, values, "total", data.get("total"));
        addField(fields, values, "estado", data.get("estado"));

        if (fields.isEmpty()) {
            return "Error: no hay campos para actualizar.";
        }

        StringBuilder sql = new StringBuilder("UPDATE venta SET ");
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) sql.append(", ");
            sql.append(fields.get(i)).append(" = ?");
        }
        sql.append(" WHERE id = ?");

        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            for (int i = 0; i < values.size(); i++) {
                String field = fields.get(i);
                String value = values.get(i);
                if (isIntegerField(field)) {
                    if (isBlank(value)) {
                        ps.setNull(idx++, java.sql.Types.INTEGER);
                    } else {
                        ps.setInt(idx++, Integer.parseInt(value));
                    }
                } else if ("total".equals(field)) {
                    ps.setBigDecimal(idx++, new BigDecimal(value));
                } else {
                    ps.setString(idx++, value);
                }
            }
            ps.setInt(idx, Integer.parseInt(id));
            int updated = ps.executeUpdate();
            if (updated == 0) {
                return "No se encontro venta con ID: " + id;
            }
            return "Venta actualizada. ID: " + id;
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        } catch (NumberFormatException e) {
            return "Error: ids y total deben ser numeros.";
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

    private static void addField(List<String> fields, List<String> values, String field, String value) {
        if (!isBlank(value)) {
            fields.add(field);
            values.add(value);
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static boolean isIntegerField(String field) {
        return "cliente_id".equals(field) || "vendedor_id".equals(field) || "cotizacion_id".equals(field);
    }
}
