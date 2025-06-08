package ch.linst.hoatutahi.components.gameBackup;

import ch.linst.hoatutahi.components.audio.AudioProviderSettings;

/**
 * This class represents the backup File
 */
@SuppressWarnings("unused")
public class BackupFile {
    public BackupVersion gameFactoryVersion = null;
    public GameFactory gameFactory = null;

    public BackupVersion gameViewFactoryVersion = null;
    public GameViewFactory gameViewFactory = null;

    public BackupVersion audioProviderSettingsVersion = null;
    public AudioProviderSettings audioProviderSettings = null;
}
