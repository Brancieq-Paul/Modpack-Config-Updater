package fr.raconteur32.modpackconfigupdater.jsonAdapters;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import fr.raconteur32.modpackconfigupdater.values.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VArrayTypeAdapter extends AbstractValueTypeAdapter<VArray> {

    public VArrayTypeAdapter(Gson gson) {
        super(gson);
    }

    @Override
    public void write(JsonWriter out, VArray value) throws IOException {
        out.beginArray();
        for (AValue<?> element : value.get_raw_value()) {
            gson.toJson(element, element.getClass(), out);
        }
        out.endArray();
    }

    @Override
    public VArray read(JsonReader in) throws IOException {
        List<AValue<?>> list = new ArrayList<>();

        in.beginArray();
        while (in.hasNext()) {
            AValue<?> value = readValue(in);
            list.add(value);
        }
        in.endArray();

        return new VArray(list);
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
                return new VMapTypeAdapter(gson).read(in);
            }
            case BEGIN_ARRAY -> {
                return read(in);
            }
            default -> {
                in.skipValue();
                return null;
            }
        }
    }
}
