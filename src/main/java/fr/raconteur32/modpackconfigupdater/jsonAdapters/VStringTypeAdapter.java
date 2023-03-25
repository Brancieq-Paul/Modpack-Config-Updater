package fr.raconteur32.modpackconfigupdater.jsonAdapters;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import fr.raconteur32.modpackconfigupdater.values.VString;

import java.io.IOException;

public class VStringTypeAdapter extends AbstractValueTypeAdapter<VString> {

    public VStringTypeAdapter(Gson gson) {
        super(gson);
    }

    @Override
    public void write(JsonWriter out, VString value) throws IOException {
        out.value(value.get_raw_value());
    }

    @Override
    public VString read(JsonReader in) throws IOException {
        return new VString(in.nextString());
    }
}
