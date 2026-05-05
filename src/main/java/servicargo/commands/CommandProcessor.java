package servicargo.commands;

import servicargo.dao.AlmacenDao;
import servicargo.dao.UsuarioDao;
import servicargo.util.BodyParser;

import java.util.Map;

public class CommandProcessor {
    private final UsuarioDao usuarioDao = new UsuarioDao();
    private final AlmacenDao almacenDao = new AlmacenDao();

    public String process(String subject, String body) {
        String command = normalizeCommand(subject);
        Map<String, String> data = BodyParser.parse(body);

        switch (command) {
            case "HELP":
                return help();
            case "INSUSU":
                return usuarioDao.insert(data);
            case "LISUSU":
                return usuarioDao.list(data.get("rol"));
            case "GETUSU":
                return usuarioDao.getByIdOrCi(getIdOrCi(data));
            case "UPDUSU":
                return usuarioDao.update(data);
            case "DELUSU":
                return usuarioDao.delete(data.get("id"));
            case "INSALM":
                return almacenDao.insert(data);
            case "LISALM":
                return almacenDao.list();
            case "GETALM":
                return almacenDao.getById(data.get("id"));
            case "UPDALM":
                return almacenDao.update(data);
            case "DELALM":
                return almacenDao.delete(data.get("id"));
            default:
                return "Comando no reconocido. Use HELP[].";
        }
    }

    private static String normalizeCommand(String subject) {
        if (subject == null) {
            return "";
        }
        String trimmed = subject.trim();
        int bracket = trimmed.indexOf('[');
        if (bracket > -1) {
            trimmed = trimmed.substring(0, bracket);
        }
        return trimmed.replaceAll("\\s+", "").toUpperCase();
    }

    private static String getIdOrCi(Map<String, String> data) {
        if (data.containsKey("id")) {
            return data.get("id");
        }
        return data.get("ci");
    }

    private static String help() {
        return "=== COMANDOS DISPONIBLES - SERVICARGO ===\n" +
            "[USU] Usuarios:\n" +
            "  INSUSU[] - Insertar usuario\n" +
            "  LISUSU[] - Listar usuarios\n" +
            "  GETUSU[] - Obtener usuario\n" +
            "  UPDUSU[] - Actualizar usuario\n" +
            "  DELUSU[] - Eliminar usuario\n\n" +
            "[ALM] Almacenes:\n" +
            "  INSALM[] - Insertar almacen\n" +
            "  LISALM[] - Listar almacenes\n" +
            "  GETALM[] - Obtener almacen\n" +
            "  UPDALM[] - Actualizar almacen\n" +
            "  DELALM[] - Eliminar almacen\n";
    }
}
