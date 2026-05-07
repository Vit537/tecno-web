package servicargo.comercial.service;

import servicargo.comercial.repository.CotizacionRepository;

import java.util.List;

public class CotizacionService {
    private final CotizacionRepository repository = new CotizacionRepository();

    public String insert(List<String> params) {
        if (params == null || params.size() < 5) {
            return "Error: INSCOT requiere 5 o 6 parametros: INSCOT[\"cliente_id\",\"vendedor_id\",\"producto_id\",cantidad,precio_unit,\"estado\"]";
        }
        try {
            int clienteId = Integer.parseInt(params.get(0));
            int vendedorId = Integer.parseInt(params.get(1));
            int productoId = Integer.parseInt(params.get(2));
            int cantidad = Integer.parseInt(params.get(3));
            String precioUnit = params.get(4);
            String estado = params.size() > 5 ? params.get(5) : null;
            new java.math.BigDecimal(precioUnit);
            return repository.insert(clienteId, vendedorId, productoId, cantidad, precioUnit, estado);
        } catch (NumberFormatException e) {
            return "Error: ids, cantidad y precio_unit deben ser numeros.";
        }
    }

    public String list(List<String> params) {
        return repository.list();
    }

    public String get(List<String> params) {
        if (params == null || params.isEmpty()) {
            return "Error: GETCOT requiere 1 parametro: GETCOT[\"id\"]";
        }
        try {
            return repository.getById(Integer.parseInt(params.get(0)));
        } catch (NumberFormatException e) {
            return "Error: id debe ser numero.";
        }
    }

    public String updateEstado(List<String> params) {
        if (params == null || params.size() < 2) {
            return "Error: UPDCOT requiere 2 parametros: UPDCOT[\"id\",\"estado\"]";
        }
        try {
            int id = Integer.parseInt(params.get(0));
            String estado = params.get(1);
            return repository.updateEstado(id, estado);
        } catch (NumberFormatException e) {
            return "Error: id debe ser numero.";
        }
    }

    public String delete(String id) {
        return repository.delete(id);
    }
}
