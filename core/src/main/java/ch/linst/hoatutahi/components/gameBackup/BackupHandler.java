package ch.linst.hoatutahi.components.gameBackup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ch.linst.hoatutahi.components.MyAssetManager;
import ch.linst.hoatutahi.components.audio.AudioProvider;
import ch.linst.hoatutahi.components.audio.AudioProviderSettings;

/**
 * Manages the persistent Backup of the game state.
 * Supports versioning and compatibility
 */
public class BackupHandler {

    private static final String GAMESTATE_BACKUP_FILENAME = "localFiles/HoatuTahi_GameState.txt";

    public static final int GAME_FACTORY_MAYOR_VERSION = 0;
    public static final int GAME_FACTORY_MINOR_VERSION = 3;

    public static final int GAME_VIEW_FACTORY_MAYOR_VERSION = 0;
    public static final int GAME_VIEW_FACTORY_MINOR_VERSION = 2;

    public static final int AUDIO_PROVIDER_SETTINGS_MAYOR_VERSION = 0;
    public static final int AUDIO_PROVIDER_SETTINGS_MINOR_VERSION = 3;

    private final BackupFile backupFile = new BackupFile();

    // No need to backup the asset manager, but this class is instantiated here because some backups need it
    private final MyAssetManager assetManager = new MyAssetManager();

    private AudioProvider audioProvider = null;

    private String backupFileMD5 = "";

    private final FileHandle fileHandle = Gdx.files.local(GAMESTATE_BACKUP_FILENAME);



    public BackupHandler() {

        if(fileHandle.exists()){
            recoverGameViewFactory();
            recoverGameFactory(); // Do recover after GameViewFactory because it depends on it
            recoverAudioProviderSettings();
        }
        else{
            backupFile.gameViewFactoryVersion = new BackupVersion(GAME_VIEW_FACTORY_MAYOR_VERSION, GAME_VIEW_FACTORY_MINOR_VERSION);
            backupFile.gameViewFactory = new GameViewFactory(assetManager);

            backupFile.gameFactoryVersion = new BackupVersion(GAME_FACTORY_MAYOR_VERSION, GAME_FACTORY_MINOR_VERSION);
            backupFile.gameFactory = new GameFactory(backupFile.gameViewFactory);

            backupFile.audioProviderSettingsVersion = new BackupVersion(AUDIO_PROVIDER_SETTINGS_MAYOR_VERSION, AUDIO_PROVIDER_SETTINGS_MINOR_VERSION);
            backupFile.audioProviderSettings = new AudioProviderSettings();
        }
    }

    public MyAssetManager getAssetManager() {
        return assetManager;
    }

    public GameFactory getGameFactory() {
        return backupFile.gameFactory;
    }

    public GameViewFactory getGameViewFactory(){
        return backupFile.gameViewFactory;
    }

    public AudioProvider getAudioProvider(){
        if(audioProvider == null){
            audioProvider = new AudioProvider(backupFile.audioProviderSettings);
        }
        return audioProvider;
    }

    public void doBackup(){

        // Save current game state to local file
        if(Gdx.files.isLocalStorageAvailable()){

            Json json = new Json();
            String serObj = json.toJson(backupFile);
            String serObjMD5 = md5(serObj);

            // Save the serialized backup file, only if the object has changed
            if(!backupFileMD5.equals(serObjMD5)){
                fileHandle.writeString(serObj, false);
                backupFileMD5 = serObjMD5;
            }
        }
    }

    private void recoverGameFactory(){
        backupFile.gameFactoryVersion = new BackupVersion(GAME_FACTORY_MAYOR_VERSION, GAME_FACTORY_MINOR_VERSION);

        if(isBackupCompatible("gameFactoryVersion", GAME_FACTORY_MAYOR_VERSION, GAME_FACTORY_MINOR_VERSION)){
            JsonValue rootDom = new JsonReader().parse(fileHandle);

            JsonValue gameFactoryDom = rootDom.get("gameFactory");
            if(gameFactoryDom != null){
                backupFile.gameFactory = new Json(){
                    // TODO Remove this code sometime later
                    // Currently it is needed to ignore "highestItemUnveiled" because this field may still be in the backup json data
                    @Override
                    protected boolean ignoreUnknownField(Class type, String fieldName) {
                        if(fieldName.equals("highestItemUnveiled")){
                            return true;
                        }
                        return super.ignoreUnknownField(type, fieldName);
                    }
                }.readValue(GameFactory.class, gameFactoryDom);
                backupFile.gameFactory.setGameViewFactory(backupFile.gameViewFactory);
            }
            else{
                backupFile.gameFactory = new GameFactory(backupFile.gameViewFactory);
            }
        }
        else{
            backupFile.gameFactory = new GameFactory(backupFile.gameViewFactory);
        }
    }

    private void recoverGameViewFactory(){
        backupFile.gameViewFactoryVersion = new BackupVersion(GAME_VIEW_FACTORY_MAYOR_VERSION, GAME_VIEW_FACTORY_MINOR_VERSION);

        if(isBackupCompatible("gameViewFactoryVersion", GAME_VIEW_FACTORY_MAYOR_VERSION, GAME_VIEW_FACTORY_MINOR_VERSION)){
            JsonValue rootDom = new JsonReader().parse(fileHandle);
            JsonValue gameViewFactoryDom = rootDom.get("gameViewFactory");
            if(gameViewFactoryDom != null){
                backupFile.gameViewFactory = new Json().readValue(GameViewFactory.class, gameViewFactoryDom);
                backupFile.gameViewFactory.setAssetManager(assetManager);
            }
            else{
                backupFile.gameViewFactory = new GameViewFactory(assetManager);
            }
        }
        else{
            backupFile.gameViewFactory = new GameViewFactory(assetManager);
        }
    }

    private void recoverAudioProviderSettings(){
        backupFile.audioProviderSettingsVersion = new BackupVersion(AUDIO_PROVIDER_SETTINGS_MAYOR_VERSION, AUDIO_PROVIDER_SETTINGS_MINOR_VERSION);

        if(isBackupCompatible("audioProviderSettingsVersion", AUDIO_PROVIDER_SETTINGS_MAYOR_VERSION, AUDIO_PROVIDER_SETTINGS_MINOR_VERSION)){
            JsonValue rootDom = new JsonReader().parse(fileHandle);

            JsonValue audioProviderSettingsDom = rootDom.get("audioProviderSettings");
            if(audioProviderSettingsDom != null){
                backupFile.audioProviderSettings = new Json().readValue(AudioProviderSettings.class, audioProviderSettingsDom);
            }
            else{
                backupFile.audioProviderSettings = new AudioProviderSettings();
            }
        }
        else{
            backupFile.audioProviderSettings = new AudioProviderSettings();
        }
    }

    private String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) hexString.append(String.format("%02x", (0xFF & b)));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        return "";
    }

    private boolean isBackupCompatible(String domName, int mayorVersion, int minorVersion){

        boolean retVal = false;

        JsonValue rootDom = new JsonReader().parse(fileHandle);
        JsonValue versionDom = rootDom.get(domName);
        BackupVersion version = new Json().readValue(BackupVersion.class, versionDom);

        if(version != null){
            if((version.getMayorVersion() == mayorVersion) && (version.getMinorVersion() <= minorVersion)){
                retVal = true;
            }
        }

        return retVal;
    }
}
