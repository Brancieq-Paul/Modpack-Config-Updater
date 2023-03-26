package fr.raconteur32.modpackconfigupdater.consts;

import java.nio.file.Path;

import net.fabricmc.loader.api.FabricLoader;

public class FabricConsts extends AModLoaderConsts {
    @Override
    public Path getMinecraftDirectoryPath() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public Path getConfigDirectoryPath() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
