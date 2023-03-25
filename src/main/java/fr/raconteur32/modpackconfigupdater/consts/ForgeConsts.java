package fr.raconteur32.modpackconfigupdater.consts;

import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class ForgeConsts extends AModLoaderConsts {
    @Override
    public Path getMinecraftDirectoryPath() {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public Path getConfigDirectoryPath() {
        return FMLPaths.CONFIGDIR.get();
    }
}
