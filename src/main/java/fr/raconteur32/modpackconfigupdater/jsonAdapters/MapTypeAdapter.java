package fr.raconteur32.modpackconfigupdater.jsonAdapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import fr.raconteur32.modpackconfigupdater.values.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MapTypeAdapter extends AbstractCustomTypeAdapter<Map<String, Object>> {

    public MapTypeAdapter(Gson gson) {
        super(gson);
    }

    @Override
    public void write(JsonWriter out, Map<String, Object> value) throws IOException {
        out.beginObject();
        for (Map.Entry<String, Object> entry : value.entrySet()) {
            out.name(entry.getKey());
            gson.toJson(entry.getValue(), entry.getValue().getClass(), out);
        }
        out.endObject();
    }

    @Override
    public Map<String, Object> read(JsonReader in) throws IOException {
        Map<String, Object> map = new HashMap<>();

        in.beginObject();
        while (in.hasNext()) {
            String key = in.nextName();
            Object value = readValue(in);
            if (value == null) {
                continue;
            }
            map.put(key, value);
        }
        in.endObject();

        return map;
    }

    private Object readValue(JsonReader in) throws IOException {
        switch (in.peek()) {
            case STRING -> {
                return in.nextString();
            }
            case NUMBER -> {
                String numberStr = in.nextString();
                if (numberStr.contains(".") || numberStr.contains("e") || numberStr.contains("E")) {
                    return Double.parseDouble(numberStr);
                } else {
                    try {
                        return Integer.parseInt(numberStr);
                    } catch (NumberFormatException ex) {
                        return Long.parseLong(numberStr);
                    }
                }
            }
            case BOOLEAN -> {
                return in.nextBoolean();
            }
            case BEGIN_OBJECT -> {
                return read(in);
            }
            case BEGIN_ARRAY -> {
                return new ArrayTypeAdapter(gson).read(in);
            }
            default -> {
                in.skipValue();
                return null;
            }
        }
    }
}
