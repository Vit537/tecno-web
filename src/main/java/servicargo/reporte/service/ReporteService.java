package servicargo.reporte.service;

import servicargo.reporte.repository.ReporteRepository;

public class ReporteService {
    private final ReporteRepository repository = new ReporteRepository();

    public String reporteVentas() {
        return repository.reporteVentas();
    }

    public String reporteEncomiendas() {
        return repository.reporteEncomiendas();
    }

    public String reporteInventario() {
        return repository.reporteInventario();
    }
}
