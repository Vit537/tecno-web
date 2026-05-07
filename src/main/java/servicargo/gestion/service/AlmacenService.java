package servicargo.gestion.service;

import servicargo.gestion.repository.AlmacenRepository;

import java.util.List;

public class AlmacenService {
    private final AlmacenRepository repository = new AlmacenRepository();

    public String insert(List<String> params) {
        if (params == null || params.size() < 5) {
            return "Error: INSALM requiere 5 parametros: INSALM[\"nombre\",\"direccion\",capacidad,\"responsable\",\"telefono\"]";
        }
        return repository.insert(params.get(0), params.get(1), params.get(2), params.get(3), params.get(4));
    }

    public String list(List<String> params) {
        return repository.list();
    }

    public String get(List<String> params) {
        if (params == null || params.isEmpty() || isBlank(params.get(0))) {
            return "Error: GETALM requiere 1 parametro: GETALM[\"id\"]";
        }
        return repository.getById(params.get(0));
    }

    public String update(List<String> params) {
        if (params == null || params.size() < 6) {
            return "Error: UPDALM requiere 6 parametros: UPDALM[\"id\",\"nombre\",\"direccion\",capacidad,\"responsable\",\"telefono\"]";
        }
        return repository.update(params.get(0), params.get(1), params.get(2), params.get(3), params.get(4), params.get(5));
    }

    public String delete(List<String> params) {
        if (params == null || params.isEmpty() || isBlank(params.get(0))) {
            return "Error: DELALM requiere 1 parametro: DELALM[\"id\"]";
        }
        return repository.delete(params.get(0));
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
