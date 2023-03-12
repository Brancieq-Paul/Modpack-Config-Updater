package fr.raconteur32.modpackconfigupdater.prelaunch;

import com.google.gson.*;
import fr.raconteur32.modpackconfigupdater.ModpackConfigUpdater;
import fr.raconteur32.modpackconfigupdater.files.filemergesystem.MergeableFileSystem;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ModpackConfigUpdaterPreLaunch implements PreLaunchEntrypoint {
    public static final Path RUN_DIR = FabricLoader.getInstance().getGameDir();
    public static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir();
    public static final Path MODPACK_CONFIG_UPDATER_DIR = Paths.get(CONFIG_DIR.toString(), "modpackconfigupdater");
    public static final Path MODPACK_CONFIG_UPDATER_JSON = Paths.get(MODPACK_CONFIG_UPDATER_DIR.toString(), "modpackconfigupdater.json");
    public static final Path OVERRIDES_DIR = Paths.get(MODPACK_CONFIG_UPDATER_DIR.toString(), "overrides");
    public static final String VERSION_FILE_NAME = "modpack_version.txt";
    public static final Path VERSION_FILE = Paths.get(VERSION_FILE_NAME);
    @Override
    public void onPreLaunch() {
        ModpackConfigUpdater.LOGGER.info("Prelaunch verifications !");
        handleDefaults();
        verifyVersions();
        applyChanges();
    }

    private void verifyVersions() {
        try {
            File version_file = VERSION_FILE.toFile();

            ModpackConfigUpdater.LOGGER.info("Verifying versions states...");
            // Version file
            if (!version_file.exists()) {
                ModpackConfigUpdater.LOGGER.debug("Creating version file...");
                if (!version_file.createNewFile()) {
                    throw new IllegalStateException("Could not create file: " + version_file.getAbsolutePath());
                }
                FileWriter fw = new FileWriter(version_file);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write("unknown");
                bw.close();
            }
            // Get list of versions
            String[] versions = getOverridesList();
            // Create override for each version
            for (String version : versions) {
                version_file = Paths.get(OVERRIDES_DIR.toString(), version, VERSION_FILE_NAME).toFile();
                if (!version_file.exists()) {
                    File version_dir = Paths.get(OVERRIDES_DIR.toString(), version).toFile();
                    ModpackConfigUpdater.LOGGER.debug("Creating override version file for '" + version + "'...");
                    if (!version_dir.exists()) {
                        if (!version_dir.mkdirs()) {
                            throw new IllegalStateException("Could not create directory: " + version_dir.getAbsolutePath());
                        }
                    }
                    if (!version_file.createNewFile()) {
                        throw new IllegalStateException("Could not create file: " + version_file.getAbsolutePath());
                    }
                    FileWriter fw = new FileWriter(version_file);
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(version);
                    bw.close();
                }
            }
        } catch (Exception e) {
            ModpackConfigUpdater.LOGGER.error("Failed to verify versions states", e);
        }
    }

    private void applyChanges() {
        try {
            MergeableFileSystem origin = new MergeableFileSystem(RUN_DIR);
            String actual_version = getActualVersion();
            List<String> override_list = Arrays.asList(getOverridesList());
            boolean is_after_actual_version = !override_list.contains(actual_version);

            for (String override_str : override_list) {
                if (is_after_actual_version) {
                    MergeableFileSystem override_fs = new MergeableFileSystem(Paths.get(OVERRIDES_DIR.toString(), override_str));
                    origin.merge(override_fs);
                } else if (actual_version.equals(override_str)) {
                    is_after_actual_version = true;
                }
            }
        } catch (IOException e) {
            ModpackConfigUpdater.LOGGER.error("Can't apply changes", e);
        }
    }

    private String getActualVersion() throws IOException {
        File version_file = VERSION_FILE.toFile();
        FileReader fr = new FileReader(version_file);
        BufferedReader br = new BufferedReader(fr);

        return br.readLine();
    }

    private String[] getOverridesList() throws IOException {
        File config_file = MODPACK_CONFIG_UPDATER_JSON.toFile();
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

            ModpackConfigUpdater.LOGGER.info("Verify default...");
            // Create config directory if not exist
            File modpackconfigupdater = MODPACK_CONFIG_UPDATER_DIR.toFile();
            if (!modpackconfigupdater.exists() && !modpackconfigupdater.isDirectory()) {
                ModpackConfigUpdater.LOGGER.debug("Creating default config directory...");
                if (!modpackconfigupdater.mkdirs()) {
                    throw new IllegalStateException("Could not create directory: " + modpackconfigupdater.getAbsolutePath());
                }
            }
            // Create default file config if not exist
            File modpackconfigupdater_json = MODPACK_CONFIG_UPDATER_JSON.toFile();
            if (!modpackconfigupdater_json.exists() && !modpackconfigupdater_json.isFile()) {
                // Config file creation
                ModpackConfigUpdater.LOGGER.debug("Creating default config file...");
                if (!modpackconfigupdater_json.createNewFile()) {
                    throw new IllegalStateException("Could not create file: " + modpackconfigupdater_json.getAbsolutePath());
                }
                // Config file content
                ModpackConfigUpdater.LOGGER.debug("Fill default content in config file...");
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
            ModpackConfigUpdater.LOGGER.error("Failed to create default updater config", e);
        }
    }
}
