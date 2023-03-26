package fr.raconteur32.modpackconfigupdater.consts;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Consts {
    // To change for other modloaders
    private static final AModLoaderConsts consts = new FabricConsts();

    public final static String MOD_ID = "modpackconfigupdater";
    private final static String JSON_CONFIG_FILENAME = "modpackconfigupdater.json";
    private final static String OVERRIDES_DIRECTORY_NAME = "overrides";
    private static final String VERSION_FILE_NAME = "modpack_version.txt";
    public static Path getMinecraftDirectoryPath() {
        return consts.getMinecraftDirectoryPath();
    }
    public static Path getConfigDirectoryPath() {
        return consts.getConfigDirectoryPath();
    }
    public static Path getConfigUpdaterDirectoryPath() {
        return Paths.get(getConfigDirectoryPath().toString(), MOD_ID);
    }

    public static Path getConfigUpdaterJsonConfigPath() {
        return Paths.get(getConfigUpdaterDirectoryPath().toString(), JSON_CONFIG_FILENAME);
    }

    public static Path getConfigUpdaterOverridesPath() {
        return Paths.get(getConfigUpdaterDirectoryPath().toString(), OVERRIDES_DIRECTORY_NAME);
    }

    public static Path getVersionFile() {
        return Paths.get(getMinecraftDirectoryPath().toString(), VERSION_FILE_NAME);
    }
}
