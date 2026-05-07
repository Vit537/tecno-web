package servicargo.gestion.service;

import servicargo.gestion.repository.InventarioRepository;

import java.util.List;

public class InventarioService {
    private final InventarioRepository repository = new InventarioRepository();

    public String insert(List<String> params) {
        if (params == null || params.size() < 4) {
            return "Error: INSINV requiere 4 parametros: INSINV[\"producto_id\",\"almacen_id\",cantidad,\"INGRESO\"|\"SALIDA\"]";
        }
        String tipo = params.get(3) == null ? "" : params.get(3).trim().toUpperCase();
        if (!"INGRESO".equals(tipo) && !"SALIDA".equals(tipo)) {
            return "Error: tipo debe ser INGRESO o SALIDA.";
        }

        try {
            int productoId = Integer.parseInt(params.get(0));
            int almacenId = Integer.parseInt(params.get(1));
            int cantidad = Integer.parseInt(params.get(2));
            if (cantidad <= 0) {
                return "Error: cantidad debe ser mayor a 0.";
            }

            InventarioRepository.InventarioItem item = repository.findByProductoAlmacen(productoId, almacenId);
            if (item == null) {
                if ("SALIDA".equals(tipo)) {
                    return "Error: no hay inventario para salida.";
                }
                return repository.insert(productoId, almacenId, cantidad);
            }

            int nuevaCantidad = "INGRESO".equals(tipo)
                ? item.cantidad + cantidad
                : item.cantidad - cantidad;
            if (nuevaCantidad < 0) {
                return "Error: stock insuficiente.";
            }
            return repository.updateCantidad(item.id, nuevaCantidad);
        } catch (NumberFormatException e) {
            return "Error: producto_id, almacen_id y cantidad deben ser numeros.";
        }
    }

    public String list(List<String> params) {
        if (params == null || params.isEmpty() || "*".equals(params.get(0))) {
            return repository.listAll();
        }
        try {
            int almacenId = Integer.parseInt(params.get(0));
            return repository.listByAlmacen(almacenId);
        } catch (NumberFormatException e) {
            return "Error: almacen_id debe ser numero.";
        }
    }
}
