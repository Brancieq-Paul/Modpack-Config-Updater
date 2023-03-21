package fr.raconteur32.modpackconfigupdater.jsonAdapters;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import fr.raconteur32.modpackconfigupdater.values.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VMapTypeAdapter extends AbstractValueTypeAdapter<VMap> {

    public VMapTypeAdapter(Gson gson) {
        super(gson);
    }

    @Override
    public void write(JsonWriter out, VMap value) throws IOException {
        out.beginObject();
        for (Map.Entry<String, AValue<?>> entry : value.get_raw_value().entrySet()) {
            out.name(entry.getKey());
            gson.toJson(entry.getValue(), entry.getValue().getClass(), out);
        }
        out.endObject();
    }

    @Override
    public VMap read(JsonReader in) throws IOException {
        Map<String, AValue<?>> map = new HashMap<>();

        in.beginObject();
        while (in.hasNext()) {
            String key = in.nextName();
            AValue<?> value = readValue(in);
            map.put(key, value);
        }
        in.endObject();

        return new VMap(map);
    }

    private AValue<?> readValue(JsonReader in) throws IOException {
        switch (in.peek()) {
            case STRING -> {
                return new VString(in.nextString());
            }
            case NUMBER -> {
                String numberStr = in.nextString();
                if (numberStr.contains(".") || numberStr.contains("e") || numberStr.contains("E")) {
                    return new VNumber(Double.parseDouble(numberStr));
                } else {
                    try {
                        return new VNumber(Integer.parseInt(numberStr));
                    } catch (NumberFormatException ex) {
                        return new VNumber(Long.parseLong(numberStr));
                    }
                }
            }
            case BOOLEAN -> {
                return new VBoolean(in.nextBoolean());
            }
            case BEGIN_OBJECT -> {
                return read(in);
            }
            case BEGIN_ARRAY -> {
                return new VArrayTypeAdapter(gson).read(in);
            }
            default -> {
                in.skipValue();
                return null;
            }
        }
    }
}
