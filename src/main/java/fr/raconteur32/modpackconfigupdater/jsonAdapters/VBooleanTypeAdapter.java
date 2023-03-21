package fr.raconteur32.modpackconfigupdater.jsonAdapters;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import fr.raconteur32.modpackconfigupdater.values.VBoolean;

import java.io.IOException;

public class VBooleanTypeAdapter extends AbstractValueTypeAdapter<VBoolean> {

    public VBooleanTypeAdapter(Gson gson) {
        super(gson);
    }

    @Override
    public void write(JsonWriter out, VBoolean value) throws IOException {
        out.value(value.get_raw_value());
    }

    @Override
    public VBoolean read(JsonReader in) throws IOException {
        return new VBoolean(in.nextBoolean());
    }
}
