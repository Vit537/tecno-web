package servicargo.gestion.service;

import servicargo.gestion.repository.UsuarioRepository;

import java.util.List;

public class UsuarioService {
    private final UsuarioRepository repository = new UsuarioRepository();

    public String insert(List<String> params) {
        if (params == null || params.size() < 7) {
            return "Error: INSUSU requiere 7 parametros: INSUSU[\"ci\",\"nombre\",\"apellido\",\"rol\",\"telefono\",\"email\",\"password\"]";
        }
        String ci = params.get(0);
        String nombre = params.get(1);
        String apellido = params.get(2);
        String rol = params.get(3);
        String telefono = params.get(4);
        String email = params.get(5);
        String password = params.get(6);
        return repository.insert(ci, nombre, apellido, rol, telefono, email, password);
    }

    public String list(List<String> params) {
        if (params == null || params.isEmpty() || "*".equals(params.get(0))) {
            return repository.list(null);
        }
        return repository.list(params.get(0));
    }

    public String get(List<String> params) {
        if (params == null || params.isEmpty() || isBlank(params.get(0))) {
            return "Error: GETUSU requiere 1 parametro: GETUSU[\"ci\"]";
        }
        return repository.getByIdOrCi(params.get(0));
    }

    public String update(List<String> params) {
        if (params == null || params.size() < 6) {
            return "Error: UPDUSU requiere 6 parametros: UPDUSU[\"ci\",\"nombre\",\"apellido\",\"rol\",\"telefono\",\"email\"]";
        }
        String ci = params.get(0);
        String nombre = params.get(1);
        String apellido = params.get(2);
        String rol = params.get(3);
        String telefono = params.get(4);
        String email = params.get(5);
        return repository.updateByCi(ci, nombre, apellido, rol, telefono, email);
    }

    public String delete(List<String> params) {
        if (params == null || params.isEmpty() || isBlank(params.get(0))) {
            return "Error: DELUSU requiere 1 parametro: DELUSU[\"ci\"]";
        }
        return repository.deleteByCi(params.get(0));
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
