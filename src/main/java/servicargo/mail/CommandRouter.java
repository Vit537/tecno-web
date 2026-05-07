package servicargo.mail;

import servicargo.comercial.service.CotizacionService;
import servicargo.comercial.service.EncomiendaService;
import servicargo.comercial.service.PagoService;
import servicargo.comercial.service.VentaService;
import servicargo.gestion.service.AlmacenService;
import servicargo.gestion.service.InventarioService;
import servicargo.gestion.service.ProductoService;
import servicargo.gestion.service.UsuarioService;
import servicargo.reporte.service.ReporteService;
import java.util.List;

public class CommandRouter {
    private final UsuarioService usuarioService = new UsuarioService();
    private final AlmacenService almacenService = new AlmacenService();
    private final ProductoService productoService = new ProductoService();
    private final InventarioService inventarioService = new InventarioService();

    private final EncomiendaService encomiendaService = new EncomiendaService();
    private final CotizacionService cotizacionService = new CotizacionService();
    private final VentaService ventaService = new VentaService();
    private final PagoService pagoService = new PagoService();

    private final ReporteService reporteService = new ReporteService();

    public String process(String command, List<String> params) {
        String normalized = normalizeCommand(command);

        switch (normalized) {
            case "HELP":
                return help();
            case "INSUSU":
                return usuarioService.insert(params);
            case "LISUSU":
                return usuarioService.list(params);
            case "GETUSU":
                return usuarioService.get(params);
            case "UPDUSU":
                return usuarioService.update(params);
            case "DELUSU":
                return usuarioService.delete(params);
            case "INSALM":
                return almacenService.insert(params);
            case "LISALM":
                return almacenService.list(params);
            case "GETALM":
                return almacenService.get(params);
            case "UPDALM":
                return almacenService.update(params);
            case "DELALM":
                return almacenService.delete(params);
            case "INSPRO":
                return productoService.insert(params);
            case "LISPRO":
                return productoService.list(params);
            case "GETPRO":
                return productoService.get(params);
            case "UPDPRO":
                return productoService.update(params);
            case "DELPRO":
                return productoService.delete(params);
            case "INSINV":
                return inventarioService.insert(params);
            case "LISINV":
                return inventarioService.list(params);
            case "INSENC":
                return encomiendaService.insert(params);
            case "LISENC":
                return encomiendaService.list(params);
            case "GETENC":
                return encomiendaService.get(params);
            case "UPDENC":
                return encomiendaService.updateEstado(params);
            case "INSCOT":
                return cotizacionService.insert(params);
            case "LISCOT":
                return cotizacionService.list(params);
            case "GETCOT":
                return cotizacionService.get(params);
            case "UPDCOT":
                return cotizacionService.updateEstado(params);
            case "INSVEN":
                return ventaService.insert(params);
            case "INSPAG":
                return pagoService.insert(params);
            case "REPVEN":
                return reporteService.reporteVentas();
            case "REPENC":
                return reporteService.reporteEncomiendas();
            case "REPINV":
                return reporteService.reporteInventario();
            default:
                return "Comando no reconocido. Use HELP[].";
        }
    }

    private static String normalizeCommand(String command) {
        if (command == null) {
            return "";
        }
        String trimmed = command.trim();
        return trimmed.replaceAll("\\s+", "").toUpperCase();
    }

    private static String help() {
        return "=== COMANDOS DISPONIBLES - SERVICARGO ===\n" +
            "Formato: COMANDO[\"param1\",\"param2\",...]\n\n" +
            "[USU] Usuarios:\n" +
            "  INSUSU[\"ci\",\"nombre\",\"apellido\",\"rol\",\"telefono\",\"email\",\"password\"]\n" +
            "  LISUSU[\"*\"] o LISUSU[\"rol\"]\n" +
            "  GETUSU[\"ci\"]\n" +
            "  UPDUSU[\"ci\",\"nombre\",\"apellido\",\"rol\",\"telefono\",\"email\"]\n" +
            "  DELUSU[\"ci\"]\n\n" +
            "[PRO] Productos:\n" +
            "  INSPRO[\"codigo\",\"nombre\",\"descripcion\",precio]\n" +
            "  LISPRO[\"*\"]\n" +
            "  GETPRO[\"codigo\"]\n" +
            "  UPDPRO[\"codigo\",\"nombre\",\"descripcion\",precio]\n" +
            "  DELPRO[\"codigo\"]\n\n" +
            "[ALM] Almacenes:\n" +
            "  INSALM[\"nombre\",\"direccion\",capacidad,\"responsable\",\"telefono\"]\n" +
            "  LISALM[\"*\"]\n" +
            "  GETALM[\"id\"]\n" +
            "  UPDALM[\"id\",\"nombre\",\"direccion\",capacidad,\"responsable\",\"telefono\"]\n" +
            "  DELALM[\"id\"]\n\n" +
            "[INV] Inventario:\n" +
            "  INSINV[\"producto_id\",\"almacen_id\",cantidad,\"INGRESO\"|\"SALIDA\"]\n" +
            "  LISINV[\"*\"] o LISINV[\"almacen_id\"]\n\n" +
            "[COT] Cotizaciones:\n" +
            "  INSCOT[\"cliente_id\",\"vendedor_id\",\"producto_id\",cantidad,precio_unit,\"estado\"]\n" +
            "  LISCOT[\"*\"]\n" +
            "  GETCOT[\"id\"]\n" +
            "  UPDCOT[\"id\",\"estado\"]\n\n" +
            "[ENC] Encomiendas:\n" +
            "  INSENC[\"cliente_id\",\"producto_id\",\"almacen_id\",\"descripcion\",peso,\"estado\"]\n" +
            "  LISENC[\"*\"]\n" +
            "  GETENC[\"id\"]\n" +
            "  UPDENC[\"id\",\"estado\"]\n\n" +
            "[VEN] Ventas:\n" +
            "  INSVEN[\"cliente_id\",\"vendedor_id\",\"cotizacion_id\",total,\"CONTADO\"]\n" +
            "  INSVEN[\"cliente_id\",\"vendedor_id\",\"cotizacion_id\",total,\"CREDITO\",cuotas]\n" +
            "  INSPAG[\"venta_id\",monto,\"metodo\"]\n\n" +
            "[REP] Reportes:\n" +
            "  REPVEN[\"*\"]\n" +
            "  REPENC[\"*\"]\n" +
            "  REPINV[\"*\"]\n";
    }
}
