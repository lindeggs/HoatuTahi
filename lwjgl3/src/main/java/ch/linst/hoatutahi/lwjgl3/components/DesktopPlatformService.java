package ch.linst.hoatutahi.lwjgl3.components;

import ch.linst.hoatutahi.components.PlatformService;

public class DesktopPlatformService implements PlatformService {

    @Override
    public String getAppVerison() {
        return "---";
    }

    @Override
    public String getBuildConfigBuildType() {
        return "debug";
    }
}
