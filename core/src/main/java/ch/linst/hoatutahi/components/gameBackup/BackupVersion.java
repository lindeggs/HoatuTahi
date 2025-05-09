package ch.linst.hoatutahi.components.gameBackup;

/**
 * Represents the version of the backup
 */
public class BackupVersion {

    private int mayorVersion;
    private int minorVersion;

    /**
     * Constructor
     */
    private BackupVersion() {
    }

    /**
     * Constructor
     */
    public BackupVersion(int mayorVersion, int minorVersion) {
        this.mayorVersion = mayorVersion;
        this.minorVersion = minorVersion;
    }

    public int getMayorVersion() {
        return mayorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }
}
