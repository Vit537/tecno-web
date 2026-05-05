package servicargo.dao;

import servicargo.db.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlmacenDao {
    public String insert(Map<String, String> data) {
        String nombre = data.get("nombre");
        String direccion = data.get("direccion");
        String capacidad = data.get("capacidad");
        String responsable = data.get("responsable");
        String telefono = data.get("telefono");

        if (isBlank(nombre) || isBlank(direccion) || isBlank(capacidad) || isBlank(responsable)) {
            return "Error: faltan campos obligatorios (nombre, direccion, capacidad, responsable).";
        }

        String sql = "INSERT INTO almacenes (nombre, direccion, capacidad, responsable, telefono) " +
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
        String sql = "SELECT id, nombre, direccion, capacidad, responsable, telefono FROM almacenes ORDER BY id";
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
        String sql = "SELECT id, nombre, direccion, capacidad, responsable, telefono FROM almacenes WHERE id = ?";
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

    public String update(Map<String, String> data) {
        String id = data.get("id");
        if (isBlank(id)) {
            return "Error: se requiere id para actualizar.";
        }

        List<String> fields = new ArrayList<>();
        List<String> values = new ArrayList<>();
        addField(fields, values, "nombre", data.get("nombre"));
        addField(fields, values, "direccion", data.get("direccion"));
        addField(fields, values, "capacidad", data.get("capacidad"));
        addField(fields, values, "responsable", data.get("responsable"));
        addField(fields, values, "telefono", data.get("telefono"));

        if (fields.isEmpty()) {
            return "Error: no hay campos para actualizar.";
        }

        StringBuilder sql = new StringBuilder("UPDATE almacenes SET ");
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) sql.append(", ");
            sql.append(fields.get(i)).append(" = ?");
        }
        sql.append(" WHERE id = ?");

        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            for (String val : values) {
                if ("capacidad".equals(fields.get(idx - 1))) {
                    ps.setInt(idx++, Integer.parseInt(val));
                } else {
                    ps.setString(idx++, val);
                }
            }
            ps.setInt(idx, Integer.parseInt(id));
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
        String sql = "DELETE FROM almacenes WHERE id = ?";
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
