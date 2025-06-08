package ch.linst.hoatutahi.components.gameBackup;

import java.util.HashMap;

import ch.linst.hoatutahi.components.ProductId;
import ch.linst.hoatutahi.components.playgroundModel.IntRef;
import ch.linst.hoatutahi.components.playgroundModel.Playground;

/**
 * This class contains all information which is needed to backup all playgrounds persistently
 */
public class GameFactory extends BaseFactory {

    // Annotation: JSON deserialization of HashMaps does only work if the key is of type String
    private final HashMap<String, Playground> playgroundHashMap = new HashMap<>();
    private final HashMap<String, IntRef> playgroundHighScoreHashMap = new HashMap<>();

    private int playgroundSizeSelectorIndex = 2;
    private int gameLevel = 0; // Level 0..2 (level 0 is the easyest)

    // highestItemsUnveiled contains for each itemset (product) the highest item that has been unveiled
    private final IntRef[] highestItemsUnveiled = new IntRef[ProductId.values().length];

    private transient GameViewFactory gameViewFactory;

    /**
     * This default constructor is needed in order to de-serialize this class
     * Annotation: The constructor is called pre deserialization.
     */
    private GameFactory() {
        for(int i = 0; i < highestItemsUnveiled.length; i++){
            highestItemsUnveiled[i] = new IntRef(6);
        }
    }

    /**
     * Constructor
     */
    public GameFactory(GameViewFactory gameViewFactory) {
        this();
        this.gameViewFactory = gameViewFactory;
    }

    public void setGameViewFactory(GameViewFactory gameViewFactory) {
        this.gameViewFactory = gameViewFactory;
    }

    public boolean isPlaygroundStored(int sizeXArg, int sizeYArg, int levelArg){
        String hashKey = getPgHashKey(sizeXArg, sizeYArg, levelArg);
        Playground playground = playgroundHashMap.get(hashKey);
        return playground != null;
    }


    /**
     * Provides a newly created playground object of a certain size, regardless of whether an object already exists
     * @param sizeX X size of the playground
     * @param sizeY Y size of the playground
     * @return The created playground
     */
    public Playground getNewPlayground(int sizeX, int sizeY){
        String hashKey = getPgHashKey(sizeX, sizeY, gameLevel);

        Playground playground = new Playground(sizeX, sizeY, getHighScoreRef(sizeX, sizeY),
                highestItemsUnveiled[gameViewFactory.getItemSetSelected()]);

        playgroundHashMap.put(hashKey, playground);

        return  playground;
    }

    /**
     * Provides a playground object of a certain size. If the factory contains an object it returns
     * it, if not, a new object is returned
     * @param sizeXArg X size of the playground
     * @param sizeYArg Y size of the playground
     * @return The created playground
     */
    public Playground getPlayground(int sizeXArg, int sizeYArg, int levelArg){
        String hashKey = getPgHashKey(sizeXArg, sizeYArg, levelArg);
        Playground playground = playgroundHashMap.get(hashKey);

        if(playground != null){
            // Inject highscoreBackup object
            playground.setHighScoreRef(getHighScoreRef(sizeXArg, sizeYArg));
            // Inject highest unveiled item
            playground.setHighestItemUnveiledRef(highestItemsUnveiled[gameViewFactory.getItemSetSelected()]);
        }
        else{
            // Create new playground
            playground = getNewPlayground(sizeXArg, sizeYArg);
        }

        return playground;
    }

    public void removePlayground(Playground pgArg){
        String hashKey = getPgHashKey(pgArg.getSizeX(), pgArg.getSizeY(), gameLevel);
        playgroundHashMap.remove(hashKey);

    }

    public int getHighScore(int sizeX, int sizeY){
        return getHighScoreRef(sizeX, sizeY).value;
    }

    private IntRef getHighScoreRef(int sizeX, int sizeY){
        String hashKey = getPgHashKey(sizeX, sizeY, gameLevel);
        return playgroundHighScoreHashMap.computeIfAbsent(hashKey, k -> new IntRef(0));
    }

    public int getPlaygroundSizeSelectorIndex() {
        return playgroundSizeSelectorIndex;
    }

    public void setPlaygroundSizeSelectorIndex(int playgroundSizeSelectorIndex) {
        this.playgroundSizeSelectorIndex = playgroundSizeSelectorIndex;
    }

    public int getGameLevel() {
        return gameLevel;
    }

    public void setGameLevel(int gameLevel) {
        this.gameLevel = gameLevel;
    }

    public int getHighestItemUnveiled(int itemSetIndex) {
        return highestItemsUnveiled[itemSetIndex].value;
    }

}
