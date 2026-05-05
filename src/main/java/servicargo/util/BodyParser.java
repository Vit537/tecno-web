package servicargo.util;

import java.util.HashMap;
import java.util.Map;

public final class BodyParser {
    private BodyParser() {}

    public static Map<String, String> parse(String body) {
        Map<String, String> data = new HashMap<>();
        if (body == null || body.trim().isEmpty()) {
            return data;
        }
        String[] lines = body.split("\\r?\\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            int idx = trimmed.indexOf('=');
            if (idx <= 0) {
                continue;
            }
            String key = trimmed.substring(0, idx).trim().toLowerCase();
            String value = trimmed.substring(idx + 1).trim();
            data.put(key, value);
        }
        return data;
    }
}
