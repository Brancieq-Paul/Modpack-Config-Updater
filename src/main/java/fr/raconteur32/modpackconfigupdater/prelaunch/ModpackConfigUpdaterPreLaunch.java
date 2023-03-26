package fr.raconteur32.modpackconfigupdater.prelaunch;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class ModpackConfigUpdaterPreLaunch implements PreLaunchEntrypoint {
    public void onPreLaunch() {
        AbstractPrelaunch prelaunch = new AbstractPrelaunch();
        prelaunch.onPreLaunch();
    }
}
