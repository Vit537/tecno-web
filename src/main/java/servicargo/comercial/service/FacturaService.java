package servicargo.comercial.service;

import servicargo.comercial.repository.FacturaRepository;

import java.util.Map;

public class FacturaService {
    private final FacturaRepository repository = new FacturaRepository();

    public String insert(Map<String, String> data) {
        return repository.insert(data);
    }

    public String list() {
        return repository.list();
    }

    public String getById(String id) {
        return repository.getById(id);
    }

    public String update(Map<String, String> data) {
        return repository.update(data);
    }

    public String delete(String id) {
        return repository.delete(id);
    }
}
