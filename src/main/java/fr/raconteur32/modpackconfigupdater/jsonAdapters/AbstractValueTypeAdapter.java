package fr.raconteur32.modpackconfigupdater.jsonAdapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import fr.raconteur32.modpackconfigupdater.values.*;

public abstract class AbstractValueTypeAdapter<T extends AValue<?>> extends TypeAdapter<T> {

    protected final Gson gson;

    public AbstractValueTypeAdapter(Gson gson) {
        this.gson = gson;
    }

    public static class Factory implements TypeAdapterFactory {

        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            Class<?> rawType = typeToken.getRawType();
            if (VMap.class.isAssignableFrom(rawType)) {
                return (TypeAdapter<T>) new VMapTypeAdapter(gson);
            } else if (VArray.class.isAssignableFrom(rawType)) {
                return (TypeAdapter<T>) new VArrayTypeAdapter(gson);
            } else if (VString.class.isAssignableFrom(rawType)) {
                return (TypeAdapter<T>) new VStringTypeAdapter(gson);
            } else if (VNumber.class.isAssignableFrom(rawType)) {
                return (TypeAdapter<T>) new VNumberTypeAdapter(gson);
            } else if (VBoolean.class.isAssignableFrom(rawType)) {
                return (TypeAdapter<T>) new VBooleanTypeAdapter(gson);
            }
            return null;
        }
    }
}