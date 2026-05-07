package servicargo.comercial.service;

import servicargo.comercial.repository.EncomiendaRepository;

import java.util.List;

public class EncomiendaService {
    private final EncomiendaRepository repository = new EncomiendaRepository();

    public String insert(List<String> params) {
        if (params == null || params.size() < 5) {
            return "Error: INSENC requiere 5 o 6 parametros: INSENC[\"cliente_id\",\"producto_id\",\"almacen_id\",\"descripcion\",peso,\"estado\"]";
        }
        try {
            int clienteId = Integer.parseInt(params.get(0));
            int productoId = Integer.parseInt(params.get(1));
            int almacenId = Integer.parseInt(params.get(2));
            String descripcion = params.get(3);
            String peso = params.get(4);
            String estado = params.size() > 5 ? params.get(5) : null;
            if (peso != null && !peso.trim().isEmpty()) {
                new java.math.BigDecimal(peso);
            }
            return repository.insert(clienteId, productoId, almacenId, descripcion, peso, estado);
        } catch (NumberFormatException e) {
            return "Error: ids y peso deben ser numeros.";
        }
    }

    public String list(List<String> params) {
        return repository.list();
    }

    public String get(List<String> params) {
        if (params == null || params.isEmpty()) {
            return "Error: GETENC requiere 1 parametro: GETENC[\"id\"]";
        }
        try {
            return repository.getById(Integer.parseInt(params.get(0)));
        } catch (NumberFormatException e) {
            return "Error: id debe ser numero.";
        }
    }

    public String updateEstado(List<String> params) {
        if (params == null || params.size() < 2) {
            return "Error: UPDENC requiere 2 parametros: UPDENC[\"id\",\"estado\"]";
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
