package servicargo.dao;

import servicargo.db.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EncomiendaDao {
    public String insert(Map<String, String> data) {
        String clienteId = data.get("cliente_id");
        String productoId = data.get("producto_id");
        String almacenId = data.get("almacen_id");
        String descripcion = data.get("descripcion");
        String peso = data.get("peso");
        String estado = data.get("estado");

        if (isBlank(clienteId) || isBlank(productoId) || isBlank(almacenId)) {
            return "Error: faltan campos obligatorios (cliente_id, producto_id, almacen_id).";
        }

        String sql = "INSERT INTO encomienda (cliente_id, producto_id, almacen_id, descripcion, peso, estado) " +
            "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(clienteId));
            ps.setInt(2, Integer.parseInt(productoId));
            ps.setInt(3, Integer.parseInt(almacenId));
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
        } catch (NumberFormatException e) {
            return "Error: ids y peso deben ser numeros.";
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

    public String getById(String id) {
        if (isBlank(id)) {
            return "Error: se requiere id.";
        }
        String sql = "SELECT id, cliente_id, producto_id, almacen_id, descripcion, peso, estado, created_at " +
            "FROM encomienda WHERE id = ?";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
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

    public String update(Map<String, String> data) {
        String id = data.get("id");
        if (isBlank(id)) {
            return "Error: se requiere id para actualizar.";
        }

        List<String> fields = new ArrayList<>();
        List<String> values = new ArrayList<>();
        addField(fields, values, "cliente_id", data.get("cliente_id"));
        addField(fields, values, "producto_id", data.get("producto_id"));
        addField(fields, values, "almacen_id", data.get("almacen_id"));
        addField(fields, values, "descripcion", data.get("descripcion"));
        addField(fields, values, "peso", data.get("peso"));
        addField(fields, values, "estado", data.get("estado"));

        if (fields.isEmpty()) {
            return "Error: no hay campos para actualizar.";
        }

        StringBuilder sql = new StringBuilder("UPDATE encomienda SET ");
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
                if (isNumericField(field)) {
                    ps.setInt(idx++, Integer.parseInt(value));
                } else if ("peso".equals(field)) {
                    ps.setBigDecimal(idx++, new java.math.BigDecimal(value));
                } else {
                    ps.setString(idx++, value);
                }
            }
            ps.setInt(idx, Integer.parseInt(id));
            int updated = ps.executeUpdate();
            if (updated == 0) {
                return "No se encontro encomienda con ID: " + id;
            }
            return "Encomienda actualizada. ID: " + id;
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        } catch (NumberFormatException e) {
            return "Error: ids y peso deben ser numeros.";
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

    private static void addField(List<String> fields, List<String> values, String field, String value) {
        if (!isBlank(value)) {
            fields.add(field);
            values.add(value);
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static boolean isNumericField(String field) {
        return "cliente_id".equals(field) || "producto_id".equals(field) || "almacen_id".equals(field);
    }

    private static String nvl(Object value) {
        return value == null ? "" : value.toString();
    }
}
