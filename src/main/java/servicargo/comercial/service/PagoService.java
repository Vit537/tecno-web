package servicargo.comercial.service;

import servicargo.comercial.repository.PagoRepository;

import java.util.List;

public class PagoService {
    private final PagoRepository repository = new PagoRepository();

    public String insert(List<String> params) {
        if (params == null || params.size() < 3) {
            return "Error: INSPAG requiere 3 parametros: INSPAG[\"venta_id\",monto,\"metodo\"]";
        }
        try {
            int ventaId = Integer.parseInt(params.get(0));
            String monto = params.get(1);
            String metodo = params.get(2);
            new java.math.BigDecimal(monto);
            return repository.insert(ventaId, monto, metodo);
        } catch (NumberFormatException e) {
            return "Error: venta_id y monto deben ser numeros.";
        }
    }

    public String list() {
        return repository.list();
    }

    public String getById(String id) {
        return repository.getById(id);
    }

    public String update(String id) {
        return repository.update(id);
    }

    public String delete(String id) {
        return repository.delete(id);
    }
}
