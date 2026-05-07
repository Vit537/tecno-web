package servicargo.gestion.repository;

import servicargo.db.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductoRepository {
    public String insert(String codigo, String nombre, String descripcion, String precio) {

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

    public String getByCodigo(String codigo) {
        if (isBlank(codigo)) {
            return "Error: se requiere codigo.";
        }
        String sql = "SELECT id, codigo, nombre, descripcion, precio FROM producto WHERE codigo = ?";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigo);
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

    public String updateByCodigo(String codigo, String nombre, String descripcion, String precio) {
        if (isBlank(codigo)) {
            return "Error: se requiere codigo para actualizar.";
        }
        if (isBlank(nombre) || isBlank(precio)) {
            return "Error: faltan campos obligatorios (nombre, precio).";
        }

        String sql = "UPDATE producto SET nombre = ?, descripcion = ?, precio = ? WHERE codigo = ?";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setBigDecimal(3, parseDecimal(precio));
            ps.setString(4, codigo);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                return "No se encontro producto con codigo: " + codigo;
            }
            return "Producto actualizado. Codigo: " + codigo;
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        } catch (NumberFormatException e) {
            return "Error: precio debe ser numero.";
        }
    }

    public String deleteByCodigo(String codigo) {
        if (isBlank(codigo)) {
            return "Error: se requiere codigo para eliminar.";
        }
        String sql = "DELETE FROM producto WHERE codigo = ?";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            int deleted = ps.executeUpdate();
            if (deleted == 0) {
                return "No se encontro producto con codigo: " + codigo;
            }
            return "Producto eliminado. Codigo: " + codigo;
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
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
