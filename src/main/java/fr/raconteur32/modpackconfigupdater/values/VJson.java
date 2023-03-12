package fr.raconteur32.modpackconfigupdater.values;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class VJson extends AValue<JsonObject>{

    public VJson() {
        super();
    }
    public VJson(JsonObject _raw_value) {
        super(_raw_value);
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().create();

        return gson.toJson(get_raw_value());
    }

    @Override
    public String formatted() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        return gson.toJson(get_raw_value());
    }
}
