package fr.raconteur32.modpackconfigupdater.prelaunch;

import com.google.gson.*;
import fr.raconteur32.modpackconfigupdater.consts.Consts;
import fr.raconteur32.modpackconfigupdater.files.IFile;
import fr.raconteur32.modpackconfigupdater.files.JsonFile;
import fr.raconteur32.modpackconfigupdater.files.PropertiesFile;
import fr.raconteur32.modpackconfigupdater.files.YAMLFile;
import fr.raconteur32.modpackconfigupdater.files.filemergesystem.MergeableFileSystem;
import fr.raconteur32.modpackconfigupdater.logs.Logs;

import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class AbstractPrelaunch {

    // Priorities
    public static final int JSON_FORMAT_PRIORITY = 20;
    public static final int YAML_FORMAT_PRIORITY = 15;
    public static final int PROPERTIES_FORMAT_PRIORITY = 10;

    public void onPreLaunch() {
        Logs.LOGGER.info("Prelaunch verifications !");

        // Register files types
        IFile.register(JSON_FORMAT_PRIORITY, JsonFile.class);
        IFile.register(PROPERTIES_FORMAT_PRIORITY, PropertiesFile.class);
        IFile.register(YAML_FORMAT_PRIORITY, YAMLFile.class);

        // Begin process
        handleDefaults();
        verifyVersions();
        applyChanges();
    }

    private void verifyVersions() {
        try {
            Logs.LOGGER.info("Verifying versions states...");
            // Get list of versions
            String[] versions = getOverridesList();
            // Create override for each version
            for (String version : versions) {
                File version_dir = Paths.get(Consts.getConfigUpdaterOverridesPath().toString(), version).toFile();
                Logs.LOGGER.debug("Creating override version dir for '" + version + "'...");
                if (!version_dir.exists()) {
                    if (!version_dir.mkdirs()) {
                        throw new IllegalStateException("Could not create directory: " + version_dir.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            Logs.LOGGER.error("Failed to verify versions states", e);
        }
    }

    private void applyChanges() {
        try {String actual_version;
            MergeableFileSystem origin = new MergeableFileSystem(Consts.getMinecraftDirectoryPath());
            try {
                actual_version = getActualVersion();
            } catch (Exception e) {
                actual_version = "unknown";
                setActualVersion(actual_version);
            }
            List<String> override_list = Arrays.asList(getOverridesList());
            boolean is_after_actual_version = !override_list.contains(actual_version);

            for (String override_str : override_list) {
                if (is_after_actual_version) {
                    MergeableFileSystem override_fs = new MergeableFileSystem(Paths.get(Consts.getConfigUpdaterOverridesPath().toString(), override_str));
                    origin.merge(override_fs);
                    setActualVersion(override_str);
                } else if (actual_version.equals(override_str)) {
                    is_after_actual_version = true;
                }
            }
        } catch (IOException e) {
            Logs.LOGGER.error("Can't apply changes", e);
        }
    }

    private String getActualVersion() throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(Consts.getVersionFile().toFile())) {
            properties.load(fis);
        }
        return properties.getProperty("version");
    }

    private void setActualVersion(String newVersion) throws IOException {
        Properties properties = new Properties();
        File versionFile = Consts.getVersionFile().toFile();

        if (!versionFile.exists()) {
            if (!versionFile.createNewFile()) {
                throw new IOException("Failed to create version file");
            }
        } else {
            try (FileInputStream fis = new FileInputStream(versionFile)) {
                properties.load(fis);
            } catch (IOException e) {
                // Fichier non valide, on le vide
                properties.clear();
            }
        }

        properties.setProperty("version", newVersion);

        try (FileOutputStream fos = new FileOutputStream(versionFile)) {
            properties.store(fos, "Updated version");
        }
    }

    private String[] getOverridesList() throws IOException {
        File config_file = Consts.getConfigUpdaterJsonConfigPath().toFile();
        FileReader fr = new FileReader(config_file);
        BufferedReader br = new BufferedReader(fr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        String config_content = sb.toString();
        JsonObject config = new Gson().fromJson(config_content, JsonObject.class);
        JsonArray version_list = (JsonArray) config.get("overrides");
        String[] stringArray = new String[version_list.size()];

        for (int i = 0; i < version_list.size(); i++) {
            JsonElement jsonElement = version_list.get(i);
            stringArray[i] = jsonElement.getAsString();
        }

        return stringArray;
    }

    private void handleDefaults() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            Logs.LOGGER.info("Verify default...");
            // Create config directory if not exist
            File modpackconfigupdater = Consts.getConfigUpdaterDirectoryPath().toFile();
            if (!modpackconfigupdater.exists() && !modpackconfigupdater.isDirectory()) {
                Logs.LOGGER.debug("Creating default config directory...");
                if (!modpackconfigupdater.mkdirs()) {
                    throw new IllegalStateException("Could not create directory: " + modpackconfigupdater.getAbsolutePath());
                }
            }
            // Create default file config if not exist
            File modpackconfigupdater_json = Consts.getConfigUpdaterJsonConfigPath().toFile();
            if (!modpackconfigupdater_json.exists() && !modpackconfigupdater_json.isFile()) {
                // Config file creation
                Logs.LOGGER.debug("Creating default config file...");
                if (!modpackconfigupdater_json.createNewFile()) {
                    throw new IllegalStateException("Could not create file: " + modpackconfigupdater_json.getAbsolutePath());
                }
                // Config file content
                Logs.LOGGER.debug("Fill default content in config file...");
                JsonObject default_content = new JsonObject();
                JsonArray default_update_list = new JsonArray();
                default_update_list.add("example_version1");
                default_update_list.add("example_version2");
                default_content.add("overrides", default_update_list);
                FileWriter fw = new FileWriter(modpackconfigupdater_json);
                BufferedWriter bw = new BufferedWriter(fw);
                String toWrite = gson.toJson(default_content);
                bw.write(toWrite);
                bw.close();
            }
        } catch (Exception e) {
            Logs.LOGGER.error("Failed to create default updater config", e);
        }
    }
}
