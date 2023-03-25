package fr.raconteur32.modpackconfigupdater.jsonAdapters;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import fr.raconteur32.modpackconfigupdater.values.VNumber;

import java.io.IOException;

public class VNumberTypeAdapter extends AbstractValueTypeAdapter<VNumber> {

    public VNumberTypeAdapter(Gson gson) {
        super(gson);
    }

    @Override
    public void write(JsonWriter out, VNumber value) throws IOException {
        if (value.get_raw_value() instanceof Integer) {
            out.value((Integer) value.get_raw_value());
        } else if (value.get_raw_value() instanceof Double) {
            out.value((Double) value.get_raw_value());
        } else {
            out.value(value.get_raw_value());
        }
    }

    @Override
    public VNumber read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NUMBER) {
            double doubleValue = in.nextDouble();
            int intValue = (int) doubleValue;
            if (doubleValue == intValue) {
                return new VNumber(intValue);
            } else {
                return new VNumber(doubleValue);
            }
        }
        return null;
    }
}
