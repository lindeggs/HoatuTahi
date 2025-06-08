package ch.linst.hoatutahi.components.gameBackup;

import java.util.HashMap;

import ch.linst.hoatutahi.components.ItemSetContainer;
import ch.linst.hoatutahi.components.MyAssetManager;
import ch.linst.hoatutahi.components.playgroundModel.Playground;
import ch.linst.hoatutahi.view.actors.PlaygroundActor;
import ch.linst.hoatutahi.view.actors.selectables.GlassGlobeActor;


/**
 * This class contains all information which is needed to backup all playgrounds persistently
 */
public class GameViewFactory extends BaseFactory{

    private transient MyAssetManager assetManager;

    private int itemSetSelected = 0; // Currently there are two item sets defined

    // Annotation: JSON deserialization of HashMaps does only work if the key is of type String
    private HashMap<String, PlaygroundViewSettings> playgroundViewSettingsHashMap;

    /**
     * This default constructor is needed in order to de-serialize this class
     */
    private GameViewFactory() {
    }

    public GameViewFactory(MyAssetManager assetManagerArg){
        playgroundViewSettingsHashMap = new HashMap<>();
        assetManager = assetManagerArg;
    }

    public void setAssetManager(MyAssetManager assetManagerArg){
        assetManager = assetManagerArg;
    }

    public void removePlaygroundViewSettings(Playground playground, int gameLevel){
        String hashKey = getPgHashKey(playground.getSizeX(), playground.getSizeY(), gameLevel);
        playgroundViewSettingsHashMap.remove(hashKey);
    }

    public PlaygroundActor getPlaygroundActor(Playground playground, int gameLevel){
        PlaygroundViewSettings playgroundViewSettings = getOrCreatePlaygroundViewSettings(playground, gameLevel);
        ItemSetContainer itemSetContainer = assetManager.getItemSetContainer(playgroundViewSettings.itemSet);
        return new PlaygroundActor(playground, itemSetContainer, gameLevel, assetManager);
    }

    public GlassGlobeActor getGlassGlobeActor(Playground playground, int gameLevel){
        PlaygroundViewSettings playgroundViewSettings = getOrCreatePlaygroundViewSettings(playground, gameLevel);
        ItemSetContainer itemSetContainer = assetManager.getItemSetContainer(playgroundViewSettings.itemSet);
        return new GlassGlobeActor(itemSetContainer, assetManager);
    }

    public int getItemSetSelected() {
        return itemSetSelected;
    }

    public void setItemSetSelected(int itemSetSelected) {
        this.itemSetSelected = itemSetSelected;
    }

    private PlaygroundViewSettings getOrCreatePlaygroundViewSettings(Playground playground, int gameLevel){
        String hashKey = getPgHashKey(playground.getSizeX(), playground.getSizeY(), gameLevel);
        PlaygroundViewSettings playgroundViewSettings = playgroundViewSettingsHashMap.get(hashKey);
        if(playgroundViewSettings == null){
            playgroundViewSettings = new PlaygroundViewSettings();
            playgroundViewSettings.itemSet = itemSetSelected;
            playgroundViewSettingsHashMap.put(hashKey, playgroundViewSettings);
        }
        return playgroundViewSettings;
    }
}
