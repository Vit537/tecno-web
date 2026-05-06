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

public class PagoDao {
    public String insert(Map<String, String> data) {
        String ventaId = data.get("venta_id");
        String monto = data.get("monto");
        String metodo = data.get("metodo");

        if (isBlank(ventaId) || isBlank(monto) || isBlank(metodo)) {
            return "Error: faltan campos obligatorios (venta_id, monto, metodo).";
        }

        String sql = "INSERT INTO pago (venta_id, monto, metodo) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(ventaId));
            ps.setBigDecimal(2, new BigDecimal(monto));
            ps.setString(3, metodo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return "Pago registrado. ID: " + rs.getInt(1);
            }
            return "Error: no se pudo registrar pago.";
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        } catch (NumberFormatException e) {
            return "Error: venta_id y monto deben ser numeros.";
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

    public String update(Map<String, String> data) {
        String id = data.get("id");
        if (isBlank(id)) {
            return "Error: se requiere id para actualizar.";
        }

        List<String> fields = new ArrayList<>();
        List<String> values = new ArrayList<>();
        addField(fields, values, "venta_id", data.get("venta_id"));
        addField(fields, values, "monto", data.get("monto"));
        addField(fields, values, "metodo", data.get("metodo"));

        if (fields.isEmpty()) {
            return "Error: no hay campos para actualizar.";
        }

        StringBuilder sql = new StringBuilder("UPDATE pago SET ");
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
                if ("venta_id".equals(field)) {
                    ps.setInt(idx++, Integer.parseInt(value));
                } else if ("monto".equals(field)) {
                    ps.setBigDecimal(idx++, new BigDecimal(value));
                } else {
                    ps.setString(idx++, value);
                }
            }
            ps.setInt(idx, Integer.parseInt(id));
            int updated = ps.executeUpdate();
            if (updated == 0) {
                return "No se encontro pago con ID: " + id;
            }
            return "Pago actualizado. ID: " + id;
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        } catch (NumberFormatException e) {
            return "Error: venta_id y monto deben ser numeros.";
        }
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
