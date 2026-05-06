package servicargo.dao;

import servicargo.db.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductoDao {
    public String insert(Map<String, String> data) {
        String codigo = data.get("codigo");
        String nombre = data.get("nombre");
        String descripcion = data.get("descripcion");
        String precio = data.get("precio");

        if (isBlank(codigo) || isBlank(nombre) || isBlank(precio)) {
            return "Error: faltan campos obligatorios (codigo, nombre, precio).";
        }

        String sql = "INSERT INTO producto (codigo, nombre, descripcion, precio) " +
            "VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ps.setString(2, nombre);
            ps.setString(3, descripcion);
            ps.setBigDecimal(4, parseDecimal(precio));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return "Producto creado. ID: " + rs.getInt(1);
            }
            return "Error: no se pudo crear el producto.";
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        } catch (NumberFormatException e) {
            return "Error: precio debe ser numero.";
        }
    }

    public String list() {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT id, codigo, nombre, descripcion, precio FROM producto ORDER BY id";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
                sb.append("ID: ").append(rs.getInt("id")).append("\n");
                sb.append("Codigo: ").append(rs.getString("codigo")).append("\n");
                sb.append("Nombre: ").append(rs.getString("nombre")).append("\n");
                sb.append("Descripcion: ").append(nvl(rs.getString("descripcion"))).append("\n");
                sb.append("Precio: ").append(rs.getBigDecimal("precio")).append("\n");
                sb.append("------------------\n");
            }
            if (count == 0) {
                return "No hay productos.";
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
        String sql = "SELECT id, codigo, nombre, descripcion, precio FROM producto WHERE id = ?";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                StringBuilder sb = new StringBuilder();
                sb.append("ID: ").append(rs.getInt("id")).append("\n");
                sb.append("Codigo: ").append(rs.getString("codigo")).append("\n");
                sb.append("Nombre: ").append(rs.getString("nombre")).append("\n");
                sb.append("Descripcion: ").append(nvl(rs.getString("descripcion"))).append("\n");
                sb.append("Precio: ").append(rs.getBigDecimal("precio")).append("\n");
                return sb.toString();
            }
            return "No se encontro producto.";
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
        addField(fields, values, "codigo", data.get("codigo"));
        addField(fields, values, "nombre", data.get("nombre"));
        addField(fields, values, "descripcion", data.get("descripcion"));
        addField(fields, values, "precio", data.get("precio"));

        if (fields.isEmpty()) {
            return "Error: no hay campos para actualizar.";
        }

        StringBuilder sql = new StringBuilder("UPDATE producto SET ");
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
                if ("precio".equals(field)) {
                    ps.setBigDecimal(idx++, parseDecimal(value));
                } else {
                    ps.setString(idx++, value);
                }
            }
            ps.setInt(idx, Integer.parseInt(id));
            int updated = ps.executeUpdate();
            if (updated == 0) {
                return "No se encontro producto con ID: " + id;
            }
            return "Producto actualizado. ID: " + id;
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        } catch (NumberFormatException e) {
            return "Error: precio debe ser numero.";
        }
    }

    public String delete(String id) {
        if (isBlank(id)) {
            return "Error: se requiere id para eliminar.";
        }
        String sql = "DELETE FROM producto WHERE id = ?";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
            int deleted = ps.executeUpdate();
            if (deleted == 0) {
                return "No se encontro producto con ID: " + id;
            }
            return "Producto eliminado. ID: " + id;
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

    private static java.math.BigDecimal parseDecimal(String value) {
        return new java.math.BigDecimal(value);
    }

    private static String nvl(String value) {
        return value == null ? "" : value;
    }
}
