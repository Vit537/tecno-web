package servicargo.gestion.repository;

import servicargo.db.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AlmacenRepository {
    public String insert(String nombre, String direccion, String capacidad, String responsable, String telefono) {

        if (isBlank(nombre) || isBlank(direccion) || isBlank(capacidad) || isBlank(responsable)) {
            return "Error: faltan campos obligatorios (nombre, direccion, capacidad, responsable).";
        }

        String sql = "INSERT INTO almacen (nombre, direccion, capacidad, responsable, telefono) " +
            "VALUES (?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, direccion);
            ps.setInt(3, Integer.parseInt(capacidad));
            ps.setString(4, responsable);
            ps.setString(5, telefono);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                return "Almacen creado. ID: " + id;
            }
            return "Error: no se pudo crear el almacen.";
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        } catch (NumberFormatException e) {
            return "Error: capacidad debe ser numero.";
        }
    }

    public String list() {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT id, nombre, direccion, capacidad, responsable, telefono FROM almacen ORDER BY id";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
                sb.append("ID: ").append(rs.getInt("id")).append("\n");
                sb.append("Nombre: ").append(rs.getString("nombre")).append("\n");
                sb.append("Direccion: ").append(rs.getString("direccion")).append("\n");
                sb.append("Capacidad: ").append(rs.getInt("capacidad")).append("\n");
                sb.append("Responsable: ").append(rs.getString("responsable")).append("\n");
                sb.append("Telefono: ").append(nvl(rs.getString("telefono"))).append("\n");
                sb.append("------------------\n");
            }
            if (count == 0) {
                return "No hay almacenes.";
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
        String sql = "SELECT id, nombre, direccion, capacidad, responsable, telefono FROM almacen WHERE id = ?";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                StringBuilder sb = new StringBuilder();
                sb.append("ID: ").append(rs.getInt("id")).append("\n");
                sb.append("Nombre: ").append(rs.getString("nombre")).append("\n");
                sb.append("Direccion: ").append(rs.getString("direccion")).append("\n");
                sb.append("Capacidad: ").append(rs.getInt("capacidad")).append("\n");
                sb.append("Responsable: ").append(rs.getString("responsable")).append("\n");
                sb.append("Telefono: ").append(nvl(rs.getString("telefono"))).append("\n");
                return sb.toString();
            }
            return "No se encontro almacen.";
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    public String update(String id, String nombre, String direccion, String capacidad,
                         String responsable, String telefono) {
        if (isBlank(id)) {
            return "Error: se requiere id para actualizar.";
        }
        if (isBlank(nombre) || isBlank(direccion) || isBlank(capacidad) || isBlank(responsable)) {
            return "Error: faltan campos obligatorios (nombre, direccion, capacidad, responsable).";
        }

        String sql = "UPDATE almacen SET nombre = ?, direccion = ?, capacidad = ?, responsable = ?, telefono = ? WHERE id = ?";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, direccion);
            ps.setInt(3, Integer.parseInt(capacidad));
            ps.setString(4, responsable);
            ps.setString(5, telefono);
            ps.setInt(6, Integer.parseInt(id));
            int updated = ps.executeUpdate();
            if (updated == 0) {
                return "No se encontro almacen con ID: " + id;
            }
            return "Almacen actualizado. ID: " + id;
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        } catch (NumberFormatException e) {
            return "Error: capacidad debe ser numero.";
        }
    }

    public String delete(String id) {
        if (isBlank(id)) {
            return "Error: se requiere id para eliminar.";
        }
        String sql = "DELETE FROM almacen WHERE id = ?";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
            int deleted = ps.executeUpdate();
            if (deleted == 0) {
                return "No se encontro almacen con ID: " + id;
            }
            return "Almacen eliminado. ID: " + id;
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
}
