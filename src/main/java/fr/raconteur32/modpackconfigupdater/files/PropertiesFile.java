package fr.raconteur32.modpackconfigupdater.files;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.raconteur32.modpackconfigupdater.jsonAdapters.AbstractValueTypeAdapter;
import fr.raconteur32.modpackconfigupdater.values.VMap;

import java.io.*;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class PropertiesFile extends JsonFile {

    public PropertiesFile(Path newFilePath) {
        super();
        init(newFilePath);
    }

    @Override
    public void init(Path FilePath) {
        setFilePath(FilePath);
        value = Objects.requireNonNull(fileToJson());
    }

    @Override
    protected VMap fileToJson() {
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream(this.getFilePath().toString())) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't load properties from file");
        }

        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new AbstractValueTypeAdapter.Factory()).create();
        String jsonString = gson.toJson(properties);
        return gson.fromJson(jsonString, VMap.class);
    }

    @Override
    public void write() {
        Properties properties = new Properties();
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new AbstractValueTypeAdapter.Factory())
                .setPrettyPrinting().create();
        Map<String, ?> map = gson.fromJson(gson.toJson(this.getValue().get_raw_value()), Map.class);

        for (Map.Entry<String, ?> entry : map.entrySet()) {
            properties.setProperty(entry.getKey(), String.valueOf(entry.getValue()));
        }

        try (OutputStream outputStream = new FileOutputStream(this.getFilePath().toFile())) {
            properties.store(outputStream, null);
        } catch (IOException e) {
            System.err.println("Could not write File");
            e.printStackTrace();
        }
    }
}
