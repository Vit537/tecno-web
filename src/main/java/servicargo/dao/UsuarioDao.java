package servicargo.dao;

import servicargo.db.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UsuarioDao {
    public String insert(Map<String, String> data) {
        String ci = data.get("ci");
        String nombre = data.get("nombre");
        String apellido = data.get("apellido");
        String rol = data.get("rol");
        String telefono = data.get("telefono");
        String email = data.get("email");
        String password = data.get("password");

        if (isBlank(ci) || isBlank(nombre) || isBlank(apellido) || isBlank(rol)
                || isBlank(email) || isBlank(password)) {
            return "Error: faltan campos obligatorios (ci, nombre, apellido, rol, email, password).";
        }

        String sql = "INSERT INTO usuario (ci, nombre, apellido, rol, telefono, email, password) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ci);
            ps.setString(2, nombre);
            ps.setString(3, apellido);
            ps.setString(4, rol);
            ps.setString(5, telefono);
            ps.setString(6, email);
            ps.setString(7, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                return "Usuario creado. ID: " + id;
            }
            return "Error: no se pudo crear el usuario.";
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    public String list(String rol) {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT id, ci, nombre, apellido, rol, telefono, email FROM usuario";
        if (!isBlank(rol)) {
            sql += " WHERE rol = ?";
        }
        sql += " ORDER BY id";

        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (!isBlank(rol)) {
                ps.setString(1, rol);
            }
            ResultSet rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
                sb.append("ID: ").append(rs.getInt("id")).append("\n");
                sb.append("CI: ").append(rs.getString("ci")).append("\n");
                sb.append("Nombre: ").append(rs.getString("nombre")).append(" ")
                    .append(rs.getString("apellido")).append("\n");
                sb.append("Rol: ").append(rs.getString("rol")).append("\n");
                sb.append("Telefono: ").append(nvl(rs.getString("telefono"))).append("\n");
                sb.append("Email: ").append(rs.getString("email")).append("\n");
                sb.append("------------------\n");
            }
            if (count == 0) {
                return "No hay usuarios.";
            }
            return sb.toString();
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    public String getByIdOrCi(String idOrCi) {
        if (isBlank(idOrCi)) {
            return "Error: se requiere id o ci.";
        }

        boolean isNumeric = idOrCi.chars().allMatch(Character::isDigit);
        String sql = isNumeric
            ? "SELECT id, ci, nombre, apellido, rol, telefono, email FROM usuario WHERE id = ?"
            : "SELECT id, ci, nombre, apellido, rol, telefono, email FROM usuario WHERE ci = ?";

        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (isNumeric) {
                ps.setInt(1, Integer.parseInt(idOrCi));
            } else {
                ps.setString(1, idOrCi);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                StringBuilder sb = new StringBuilder();
                sb.append("ID: ").append(rs.getInt("id")).append("\n");
                sb.append("CI: ").append(rs.getString("ci")).append("\n");
                sb.append("Nombre: ").append(rs.getString("nombre")).append(" ")
                    .append(rs.getString("apellido")).append("\n");
                sb.append("Rol: ").append(rs.getString("rol")).append("\n");
                sb.append("Telefono: ").append(nvl(rs.getString("telefono"))).append("\n");
                sb.append("Email: ").append(rs.getString("email")).append("\n");
                return sb.toString();
            }
            return "No se encontro usuario.";
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
        addField(fields, values, "ci", data.get("ci"));
        addField(fields, values, "nombre", data.get("nombre"));
        addField(fields, values, "apellido", data.get("apellido"));
        addField(fields, values, "rol", data.get("rol"));
        addField(fields, values, "telefono", data.get("telefono"));
        addField(fields, values, "email", data.get("email"));
        addField(fields, values, "password", data.get("password"));

        if (fields.isEmpty()) {
            return "Error: no hay campos para actualizar.";
        }

        StringBuilder sql = new StringBuilder("UPDATE usuario SET ");
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) sql.append(", ");
            sql.append(fields.get(i)).append(" = ?");
        }
        sql.append(" WHERE id = ?");

        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            for (String val : values) {
                ps.setString(idx++, val);
            }
            ps.setInt(idx, Integer.parseInt(id));
            int updated = ps.executeUpdate();
            if (updated == 0) {
                return "No se encontro usuario con ID: " + id;
            }
            return "Usuario actualizado. ID: " + id;
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    public String delete(String id) {
        if (isBlank(id)) {
            return "Error: se requiere id para eliminar.";
        }
        String sql = "DELETE FROM usuario WHERE id = ?";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
            int deleted = ps.executeUpdate();
            if (deleted == 0) {
                return "No se encontro usuario con ID: " + id;
            }
            return "Usuario eliminado. ID: " + id;
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

    private static String nvl(String value) {
        return value == null ? "" : value;
    }
}
