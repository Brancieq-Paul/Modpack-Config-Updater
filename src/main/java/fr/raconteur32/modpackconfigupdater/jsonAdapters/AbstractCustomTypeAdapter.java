package fr.raconteur32.modpackconfigupdater.jsonAdapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

public abstract class AbstractCustomTypeAdapter<T extends Object> extends TypeAdapter<T> {

    protected final Gson gson;

    public AbstractCustomTypeAdapter(Gson gson) {
        this.gson = gson;
    }

    public static class Factory implements TypeAdapterFactory {

        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            Class<?> rawType = typeToken.getRawType();
            if (Map.class.isAssignableFrom(rawType)) {
                return (TypeAdapter<T>) new MapTypeAdapter(gson);
            } else if (List.class.isAssignableFrom(rawType)) {
                return (TypeAdapter<T>) new ArrayTypeAdapter(gson);
            } else if (Number.class.isAssignableFrom(rawType)) {
                return (TypeAdapter<T>) new NumberTypeAdapter(gson);
            }
            return null;
        }
    }
}