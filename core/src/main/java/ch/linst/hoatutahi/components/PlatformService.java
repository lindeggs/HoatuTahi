package ch.linst.hoatutahi.components;

public interface PlatformService {

    /**
     * Reads the app version and returns it as a String
     * @return app version
     */
    String getAppVerison();

    /**
     * Reads the build type and returns it as a String
     * @return build type
     */
    String getBuildConfigBuildType();
}
