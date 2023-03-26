package fr.raconteur32.modpackconfigupdater.files;

import com.google.gson.GsonBuilder;
import fr.raconteur32.modpackconfigupdater.jsonAdapters.AbstractValueTypeAdapter;
import fr.raconteur32.modpackconfigupdater.logs.Logs;
import fr.raconteur32.modpackconfigupdater.values.IMergeable;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Objects;

import com.google.gson.Gson;
import fr.raconteur32.modpackconfigupdater.values.VMap;

public class JsonFile implements IMergeableFile<JsonFile> {

    private Path FilePath;
    protected VMap value;

    protected JsonFile() {
    }

    public JsonFile(Path NewFilePath) {
        this.init(NewFilePath);
    }

    public void init(Path FilePath) {
        setFilePath(FilePath);
        value = Objects.requireNonNull(fileToJson());
    }

    protected VMap fileToJson() {
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new AbstractValueTypeAdapter.Factory())
                .create();
        try (BufferedReader br = new BufferedReader(new FileReader(this.FilePath.toString()))) {
            // Read file contents into a StringBuilder
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            // Convert StringBuilder to String
            String fileContents = sb.toString();
            // Use Gson to convert String to JSON
            return gson.fromJson(fileContents, VMap.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't load json data from file");
        }
    }

    public VMap getValue() {
        return value;
    }

    public void setFilePath(Path filePath) {
        FilePath = filePath;
    }

    @Override
    public Path getFilePath() {
        return this.FilePath;
    }

    @Override
    public void write() {
        try {
            Gson gson = new GsonBuilder().registerTypeAdapterFactory(new AbstractValueTypeAdapter.Factory())
                    .setPrettyPrinting().create();
            String toWrite = gson.toJson(value.get_raw_value());
            FileWriter fw = new FileWriter(this.FilePath.toFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(toWrite);
            bw.close();
        } catch (IOException e) {
            Logs.LOGGER.error("Could not write File", e);
        }
    }

    @Override
    public IMergeable<JsonFile> merge(IMergeable<JsonFile> other) {
        if (!(other instanceof JsonFile otherJsonFile)) {
            throw new IllegalArgumentException("Cannot merge with a non-JsonFile object");
        }
        Class<? extends JsonFile> clazz = getClass();
        JsonFile mergedFile = null;
        try {
            mergedFile = clazz.getConstructor(Path.class).newInstance(this.FilePath);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        mergedFile.value = this.value.merge(otherJsonFile.getValue());

        return mergedFile;
    }
}
