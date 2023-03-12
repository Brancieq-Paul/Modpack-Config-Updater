package fr.raconteur32.modpackconfigupdater.values;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.Properties;

public class VProperties extends AValue<Properties>{

    public VProperties() {
        super();
    }
    public VProperties(Properties _raw_value) {
        super(_raw_value);
    }

    @Override
    public String toString() {
        return get_raw_value().toString();
    }

    @Override
    public String formatted() {
        return this.toString();
    }
}
