package servicargo.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SubjectParser {
    private static final Pattern PARAM_PATTERN = Pattern.compile("\"([^\"]*)\"|([^,\\s]+)");

    private SubjectParser() {}

    public static String extraerComando(String subject) {
        if (subject == null || subject.trim().isEmpty()) {
            return "";
        }
        int idx = subject.indexOf("[");
        String raw = idx == -1 ? subject : subject.substring(0, idx);
        return raw.trim().toUpperCase();
    }

    public static List<String> extraerParametros(String subject) {
        List<String> params = new ArrayList<>();
        if (subject == null || !subject.contains("[")) {
            return params;
        }

        int inicio = subject.indexOf("[");
        int fin = subject.lastIndexOf("]");
        if (inicio == -1 || fin == -1 || fin <= inicio) {
            return params;
        }

        String contenido = subject.substring(inicio + 1, fin).trim();
        if (contenido.isEmpty()) {
            return params;
        }
        if ("\"*\"".equals(contenido) || "*".equals(contenido)) {
            params.add("*");
            return params;
        }

        Matcher matcher = PARAM_PATTERN.matcher(contenido);
        while (matcher.find()) {
            String quoted = matcher.group(1);
            String bare = matcher.group(2);
            if (quoted != null) {
                params.add(quoted);
            } else if (bare != null) {
                params.add(bare);
            }
        }
        return params;
    }
}
