package servicargo.comercial.service;

import servicargo.comercial.repository.VentaRepository;

import java.util.List;

public class VentaService {
    private final VentaRepository repository = new VentaRepository();

    public String insert(List<String> params) {
        if (params == null || params.size() < 5) {
            return "Error: INSVEN requiere 5 o 6 parametros: INSVEN[\"cliente_id\",\"vendedor_id\",\"cotizacion_id\",total,\"CONTADO\"|\"CREDITO\",cuotas]";
        }
        String estado = params.get(4);
        if (estado != null && "CREDITO".equalsIgnoreCase(estado) && params.size() < 6) {
            return "Error: INSVEN con CREDITO requiere cuotas: INSVEN[\"cliente_id\",\"vendedor_id\",\"cotizacion_id\",total,\"CREDITO\",cuotas]";
        }

        try {
            int clienteId = Integer.parseInt(params.get(0));
            int vendedorId = Integer.parseInt(params.get(1));
            Integer cotizacionId = parseOptionalInt(params.get(2));
            String total = params.get(3);
            new java.math.BigDecimal(total);
            return repository.insert(clienteId, vendedorId, cotizacionId, total, estado);
        } catch (NumberFormatException e) {
            return "Error: ids, total o cuotas deben ser numeros.";
        }
    }

    private static Integer parseOptionalInt(String value) {
        if (value == null || value.trim().isEmpty() || "*".equals(value)) {
            return null;
        }
        return Integer.parseInt(value);
    }
}
