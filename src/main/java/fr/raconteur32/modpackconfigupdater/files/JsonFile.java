package fr.raconteur32.modpackconfigupdater.files;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import fr.raconteur32.modpackconfigupdater.ModpackConfigUpdater;
import fr.raconteur32.modpackconfigupdater.utils.GsonTools;
import fr.raconteur32.modpackconfigupdater.values.VJson;

import java.io.*;
import java.nio.file.Path;

import com.google.gson.Gson;

public class JsonFile extends VJson implements IMergeableFile<JsonFile> {

    private Path FilePath;

    public JsonFile() {
        super();
    }

    public JsonFile(Path NewFilePath) {
        super();
        this.init(NewFilePath);
    }


    @Override
    public void init(Path FilePath) {
        setFilePath(FilePath);
        this.set_raw_value(fileToJson());
    }

    private JsonObject fileToJson() {
        Gson gson = new Gson();
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
            return gson.fromJson(fileContents, JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String toWrite = gson.toJson(this.get_raw_value());
            FileWriter fw = new FileWriter(this.FilePath.toFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(toWrite);
            bw.close();
        } catch (IOException e) {
            ModpackConfigUpdater.LOGGER.error("Could not write File", e);
        }
    }

    @Override
    public JsonFile merge(JsonFile other) {
        try {
            JsonFile merged = new JsonFile(getFilePath());

            GsonTools.extendJsonObject(merged.get_raw_value(), other.get_raw_value(), GsonTools.ConflictStrategy.PREFER_SECOND_OBJ);
            return merged;
        } catch (GsonTools.JsonObjectExtensionConflictException e) {
            e.printStackTrace();
            ModpackConfigUpdater.LOGGER.error("Couldn't merge files");
        }
        return null;
    }
}
