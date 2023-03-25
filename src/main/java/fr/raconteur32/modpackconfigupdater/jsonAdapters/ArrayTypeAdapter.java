package fr.raconteur32.modpackconfigupdater.jsonAdapters;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArrayTypeAdapter extends AbstractCustomTypeAdapter<List<Object>> {

    public ArrayTypeAdapter(Gson gson) {
        super(gson);
    }

    @Override
    public void write(JsonWriter out, List<Object> value) throws IOException {
        out.beginArray();
        for (Object element : value) {
            gson.toJson(element, element.getClass(), out);
        }
        out.endArray();
    }

    @Override
    public List<Object> read(JsonReader in) throws IOException {
        List<Object> list = new ArrayList<>();

        in.beginArray();
        while (in.hasNext()) {
            Object value = readValue(in);
            list.add(value);
        }
        in.endArray();

        return list;
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
                return new MapTypeAdapter(gson).read(in);
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
