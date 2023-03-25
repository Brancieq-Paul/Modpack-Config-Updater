package fr.raconteur32.modpackconfigupdater;

import fr.raconteur32.modpackconfigupdater.consts.Consts;
import fr.raconteur32.modpackconfigupdater.prelaunch.AbstractPrelaunch;
import net.minecraftforge.fml.common.Mod;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(Consts.MOD_ID)
public class ModpackConfigUpdater {
    public ModpackConfigUpdater() {
        AbstractPrelaunch prelaunch = new AbstractPrelaunch();
        prelaunch.onPreLaunch();
    }
}
