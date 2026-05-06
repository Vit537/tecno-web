package servicargo.commands;

import servicargo.dao.AlmacenDao;
import servicargo.dao.CotizacionDao;
import servicargo.dao.EncomiendaDao;
import servicargo.dao.FacturaDao;
import servicargo.dao.InventarioDao;
import servicargo.dao.PagoDao;
import servicargo.dao.ProductoDao;
import servicargo.dao.ReporteDao;
import servicargo.dao.UsuarioDao;
import servicargo.dao.VentaDao;
import servicargo.util.BodyParser;

import java.util.Map;

public class CommandProcessor {
    private final UsuarioDao usuarioDao = new UsuarioDao();
    private final AlmacenDao almacenDao = new AlmacenDao();
    private final ProductoDao productoDao = new ProductoDao();
    private final InventarioDao inventarioDao = new InventarioDao();
    private final EncomiendaDao encomiendaDao = new EncomiendaDao();
    private final CotizacionDao cotizacionDao = new CotizacionDao();
    private final VentaDao ventaDao = new VentaDao();
    private final PagoDao pagoDao = new PagoDao();
    private final FacturaDao facturaDao = new FacturaDao();
    private final ReporteDao reporteDao = new ReporteDao();

    public String process(String subject, String body) {
        String command = normalizeCommand(subject);
        Map<String, String> data = BodyParser.parse(body);

        switch (command) {
            case "HELP":
                return help();
            case "INSUSU":
                return usuarioDao.insert(data);
            case "LISUSU":
                return usuarioDao.list(data.get("rol"));
            case "GETUSU":
                return usuarioDao.getByIdOrCi(getIdOrCi(data));
            case "UPDUSU":
                return usuarioDao.update(data);
            case "DELUSU":
                return usuarioDao.delete(data.get("id"));
            case "INSALM":
                return almacenDao.insert(data);
            case "LISALM":
                return almacenDao.list();
            case "GETALM":
                return almacenDao.getById(data.get("id"));
            case "UPDALM":
                return almacenDao.update(data);
            case "DELALM":
                return almacenDao.delete(data.get("id"));
            case "INSPRO":
                return productoDao.insert(data);
            case "LISPRO":
                return productoDao.list();
            case "GETPRO":
                return productoDao.getById(data.get("id"));
            case "UPDPRO":
                return productoDao.update(data);
            case "DELPRO":
                return productoDao.delete(data.get("id"));
            case "INSINV":
                return inventarioDao.insert(data);
            case "LISINV":
                return inventarioDao.list();
            case "GETINV":
                return inventarioDao.getById(data.get("id"));
            case "UPDINV":
                return inventarioDao.update(data);
            case "DELINV":
                return inventarioDao.delete(data.get("id"));
            case "INSENC":
                return encomiendaDao.insert(data);
            case "LISENC":
                return encomiendaDao.list();
            case "GETENC":
                return encomiendaDao.getById(data.get("id"));
            case "UPDENC":
                return encomiendaDao.update(data);
            case "DELENC":
                return encomiendaDao.delete(data.get("id"));
            case "INSCOT":
                return cotizacionDao.insert(data);
            case "LISCOT":
                return cotizacionDao.list();
            case "GETCOT":
                return cotizacionDao.getById(data.get("id"));
            case "UPDCOT":
                return cotizacionDao.update(data);
            case "DELCOT":
                return cotizacionDao.delete(data.get("id"));
            case "INSVEN":
                return ventaDao.insert(data);
            case "LISVEN":
                return ventaDao.list();
            case "GETVEN":
                return ventaDao.getById(data.get("id"));
            case "UPDVEN":
                return ventaDao.update(data);
            case "DELVEN":
                return ventaDao.delete(data.get("id"));
            case "INSPAG":
                return pagoDao.insert(data);
            case "LISPAG":
                return pagoDao.list();
            case "GETPAG":
                return pagoDao.getById(data.get("id"));
            case "UPDPAG":
                return pagoDao.update(data);
            case "DELPAG":
                return pagoDao.delete(data.get("id"));
            case "INSFAC":
                return facturaDao.insert(data);
            case "LISFAC":
                return facturaDao.list();
            case "GETFAC":
                return facturaDao.getById(data.get("id"));
            case "UPDFAC":
                return facturaDao.update(data);
            case "DELFAC":
                return facturaDao.delete(data.get("id"));
            case "REPVEN":
                return reporteDao.reporteVentas();
            case "REPENC":
                return reporteDao.reporteEncomiendas();
            default:
                return "Comando no reconocido. Use HELP[].";
        }
    }

    private static String normalizeCommand(String subject) {
        if (subject == null) {
            return "";
        }
        String trimmed = subject.trim();
        int bracket = trimmed.indexOf('[');
        if (bracket > -1) {
            trimmed = trimmed.substring(0, bracket);
        }
        return trimmed.replaceAll("\\s+", "").toUpperCase();
    }

    private static String getIdOrCi(Map<String, String> data) {
        if (data.containsKey("id")) {
            return data.get("id");
        }
        return data.get("ci");
    }

    private static String help() {
        return "=== COMANDOS DISPONIBLES - SERVICARGO ===\n" +
            "[USU] Usuarios:\n" +
            "  INSUSU[] - Insertar usuario\n" +
            "  LISUSU[] - Listar usuarios\n" +
            "  GETUSU[] - Obtener usuario\n" +
            "  UPDUSU[] - Actualizar usuario\n" +
            "  DELUSU[] - Eliminar usuario\n\n" +
            "[ALM] Almacenes:\n" +
            "  INSALM[] - Insertar almacen\n" +
            "  LISALM[] - Listar almacenes\n" +
            "  GETALM[] - Obtener almacen\n" +
            "  UPDALM[] - Actualizar almacen\n" +
            "  DELALM[] - Eliminar almacen\n"+
            "\n" +
            "[PRO] Productos:\n" +
            "  INSPRO[] - Insertar producto\n" +
            "  LISPRO[] - Listar productos\n" +
            "  GETPRO[] - Obtener producto\n" +
            "  UPDPRO[] - Actualizar producto\n" +
            "  DELPRO[] - Eliminar producto\n\n" +
            "[INV] Inventario:\n" +
            "  INSINV[] - Insertar inventario\n" +
            "  LISINV[] - Listar inventario\n" +
            "  GETINV[] - Obtener inventario\n" +
            "  UPDINV[] - Actualizar inventario\n" +
            "  DELINV[] - Eliminar inventario\n\n" +
            "[ENC] Encomiendas:\n" +
            "  INSENC[] - Insertar encomienda\n" +
            "  LISENC[] - Listar encomiendas\n" +
            "  GETENC[] - Obtener encomienda\n" +
            "  UPDENC[] - Actualizar encomienda\n" +
            "  DELENC[] - Eliminar encomienda\n\n" +
            "[COT] Cotizaciones:\n" +
            "  INSCOT[] - Insertar cotizacion\n" +
            "  LISCOT[] - Listar cotizaciones\n" +
            "  GETCOT[] - Obtener cotizacion\n" +
            "  UPDCOT[] - Actualizar cotizacion\n" +
            "  DELCOT[] - Eliminar cotizacion\n\n" +
            "[VEN] Ventas:\n" +
            "  INSVEN[] - Insertar venta\n" +
            "  LISVEN[] - Listar ventas\n" +
            "  GETVEN[] - Obtener venta\n" +
            "  UPDVEN[] - Actualizar venta\n" +
            "  DELVEN[] - Eliminar venta\n\n" +
            "[PAG] Pagos:\n" +
            "  INSPAG[] - Registrar pago\n" +
            "  LISPAG[] - Listar pagos\n" +
            "  GETPAG[] - Obtener pago\n" +
            "  UPDPAG[] - Actualizar pago\n" +
            "  DELPAG[] - Eliminar pago\n\n" +
            "[FAC] Facturas:\n" +
            "  INSFAC[] - Insertar factura\n" +
            "  LISFAC[] - Listar facturas\n" +
            "  GETFAC[] - Obtener factura\n" +
            "  UPDFAC[] - Actualizar factura\n" +
            "  DELFAC[] - Eliminar factura\n\n" +
            "[REP] Reportes:\n" +
            "  REPVEN[] - Reporte de ventas\n" +
            "  REPENC[] - Reporte de encomiendas\n";
    }
}
