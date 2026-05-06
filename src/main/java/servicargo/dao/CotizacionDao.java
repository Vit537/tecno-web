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

public class CotizacionDao {
    public String insert(Map<String, String> data) {
        String clienteId = data.get("cliente_id");
        String vendedorId = data.get("vendedor_id");
        String productoId = data.get("producto_id");
        String cantidad = data.get("cantidad");
        String precioUnit = data.get("precio_unit");
        String estado = data.get("estado");

        if (isBlank(clienteId) || isBlank(vendedorId) || isBlank(productoId)
                || isBlank(cantidad) || isBlank(precioUnit)) {
            return "Error: faltan campos obligatorios (cliente_id, vendedor_id, producto_id, cantidad, precio_unit).";
        }

        BigDecimal total = new BigDecimal(precioUnit).multiply(new BigDecimal(cantidad));

        String sql = "INSERT INTO cotizacion (cliente_id, vendedor_id, producto_id, cantidad, precio_unit, total, estado) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(clienteId));
            ps.setInt(2, Integer.parseInt(vendedorId));
            ps.setInt(3, Integer.parseInt(productoId));
            ps.setInt(4, Integer.parseInt(cantidad));
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
        } catch (NumberFormatException e) {
            return "Error: ids, cantidad y precio_unit deben ser numeros.";
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

    public String getById(String id) {
        if (isBlank(id)) {
            return "Error: se requiere id.";
        }
        String sql = "SELECT id, cliente_id, vendedor_id, producto_id, cantidad, precio_unit, total, estado, created_at " +
            "FROM cotizacion WHERE id = ?";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
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

    public String update(Map<String, String> data) {
        String id = data.get("id");
        if (isBlank(id)) {
            return "Error: se requiere id para actualizar.";
        }

        List<String> fields = new ArrayList<>();
        List<String> values = new ArrayList<>();
        addField(fields, values, "cliente_id", data.get("cliente_id"));
        addField(fields, values, "vendedor_id", data.get("vendedor_id"));
        addField(fields, values, "producto_id", data.get("producto_id"));
        addField(fields, values, "cantidad", data.get("cantidad"));
        addField(fields, values, "precio_unit", data.get("precio_unit"));
        addField(fields, values, "total", data.get("total"));
        addField(fields, values, "estado", data.get("estado"));

        if (fields.isEmpty()) {
            return "Error: no hay campos para actualizar.";
        }

        StringBuilder sql = new StringBuilder("UPDATE cotizacion SET ");
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
                    ps.setInt(idx++, Integer.parseInt(value));
                } else if (isDecimalField(field)) {
                    ps.setBigDecimal(idx++, new BigDecimal(value));
                } else {
                    ps.setString(idx++, value);
                }
            }
            ps.setInt(idx, Integer.parseInt(id));
            int updated = ps.executeUpdate();
            if (updated == 0) {
                return "No se encontro cotizacion con ID: " + id;
            }
            return "Cotizacion actualizada. ID: " + id;
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        } catch (NumberFormatException e) {
            return "Error: ids, cantidad y precios deben ser numeros.";
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
        return "cliente_id".equals(field) || "vendedor_id".equals(field)
            || "producto_id".equals(field) || "cantidad".equals(field);
    }

    private static boolean isDecimalField(String field) {
        return "precio_unit".equals(field) || "total".equals(field);
    }
}
