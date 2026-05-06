package servicargo.dao;

import servicargo.db.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReporteDao {
    public String reporteVentas() {
        String sql = "SELECT COUNT(*) AS total_ventas, COALESCE(SUM(total),0) AS monto_total FROM venta";
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return "Total ventas: " + rs.getInt("total_ventas") + "\n" +
                    "Monto total: " + rs.getBigDecimal("monto_total");
            }
            return "No hay datos de ventas.";
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }

    public String reporteEncomiendas() {
        String sql = "SELECT estado, COUNT(*) AS total FROM encomienda GROUP BY estado ORDER BY estado";
        StringBuilder sb = new StringBuilder();
        try (Connection conn = DbConnection.open();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int count = 0;
            while (rs.next()) {
                count++;
                sb.append(rs.getString("estado")).append(": ")
                    .append(rs.getInt("total")).append("\n");
            }
            if (count == 0) {
                return "No hay encomiendas.";
            }
            return sb.toString().trim();
        } catch (SQLException e) {
            return "Error BD: " + e.getMessage();
        }
    }
}
