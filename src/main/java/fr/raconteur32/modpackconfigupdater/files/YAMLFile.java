package fr.raconteur32.modpackconfigupdater.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.raconteur32.modpackconfigupdater.jsonAdapters.AbstractCustomTypeAdapter;
import fr.raconteur32.modpackconfigupdater.jsonAdapters.AbstractValueTypeAdapter;
import fr.raconteur32.modpackconfigupdater.values.VMap;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

public class YAMLFile extends JsonFile {

    public YAMLFile(Path newFilePath) {
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
        Yaml yaml = new Yaml(new Constructor());
        Map<String, Object> yamlData;
        try (InputStream inputStream = new FileInputStream(this.getFilePath().toString())) {
            yamlData = yaml.load(inputStream);
        } catch (Exception e) {
            throw new IllegalArgumentException("Can't load yaml data from file", e);
        }

        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new AbstractValueTypeAdapter.Factory()).create();
        String jsonString = gson.toJson(yamlData);
        return gson.fromJson(jsonString, VMap.class);
    }

    @Override
    public void write() {
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);
        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Yaml yaml = new Yaml(representer, options);
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new AbstractValueTypeAdapter.Factory()).create();
        Gson vanillagson = new GsonBuilder().registerTypeAdapterFactory(new AbstractCustomTypeAdapter.Factory())
                .create();
        Map<String, Object> map = vanillagson.fromJson(gson.toJson(this.getValue()), Map.class);

        try (Writer writer = new FileWriter(this.getFilePath().toFile())) {
            yaml.dump(map, writer);
        } catch (IOException e) {
            System.err.println("Could not write File");
            e.printStackTrace();
        }
    }
}
