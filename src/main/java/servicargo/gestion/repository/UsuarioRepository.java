package servicargo.gestion.repository;

import servicargo.db.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioRepository {
    public String insert(String ci, String nombre, String apellido, String rol,
                         String telefono, String email, String password) {
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

    public String updateByCi(String ci, String nombre, String apellido, String rol,
                             String telefono, String email) {
        if (isBlank(ci)) {
            return "Error: se requiere ci para actualizar.";
        }
        if (isBlank(nombre) || isBlank(apellido) || isBlank(rol) || isBlank(email)) {
            return "Error: faltan campos obligatorios (nombre, apellido, rol, email).";
        }

        String sql = "UPDATE usuario SET nombre = ?, apellido = ?, rol = ?, telefono = ?, email = ? WHERE ci = ?";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setString(3, rol);
            ps.setString(4, emptyToNull(telefono));
            ps.setString(5, email);
            ps.setString(6, ci);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                return "No se encontro usuario con CI: " + ci;
            }
            return "Usuario actualizado. CI: " + ci;
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    public String deleteByCi(String ci) {
        if (isBlank(ci)) {
            return "Error: se requiere ci para eliminar.";
        }
        String sql = "DELETE FROM usuario WHERE ci = ?";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ci);
            int deleted = ps.executeUpdate();
            if (deleted == 0) {
                return "No se encontro usuario con CI: " + ci;
            }
            return "Usuario eliminado. CI: " + ci;
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static String nvl(String value) {
        return value == null ? "" : value;
    }

    private static String emptyToNull(String value) {
        return isBlank(value) ? null : value;
    }
}
