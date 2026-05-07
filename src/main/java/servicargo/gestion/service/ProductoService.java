package servicargo.gestion.service;

import servicargo.gestion.repository.ProductoRepository;

import java.util.List;

public class ProductoService {
    private final ProductoRepository repository = new ProductoRepository();

    public String insert(List<String> params) {
        if (params == null || params.size() < 4) {
            return "Error: INSPRO requiere 4 parametros: INSPRO[\"codigo\",\"nombre\",\"descripcion\",precio]";
        }
        return repository.insert(params.get(0), params.get(1), params.get(2), params.get(3));
    }

    public String list(List<String> params) {
        return repository.list();
    }

    public String get(List<String> params) {
        if (params == null || params.isEmpty() || isBlank(params.get(0))) {
            return "Error: GETPRO requiere 1 parametro: GETPRO[\"codigo\"]";
        }
        return repository.getByCodigo(params.get(0));
    }

    public String update(List<String> params) {
        if (params == null || params.size() < 4) {
            return "Error: UPDPRO requiere 4 parametros: UPDPRO[\"codigo\",\"nombre\",\"descripcion\",precio]";
        }
        return repository.updateByCodigo(params.get(0), params.get(1), params.get(2), params.get(3));
    }

    public String delete(List<String> params) {
        if (params == null || params.isEmpty() || isBlank(params.get(0))) {
            return "Error: DELPRO requiere 1 parametro: DELPRO[\"codigo\"]";
        }
        return repository.deleteByCodigo(params.get(0));
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
