package ch.linst.hoatutahi.components.playgroundModel;

/**
 * Represents a single Item on the Playground
 * The PlaygroundItem knows where it's placed on the playground and where to move (if applicable)
 */
public final class PlaygroundItem {

    public int value = 0;
    public int moveX = 0;
    public int moveY = 0;

    /**
     * Constructor
     * This constructor is needed for JSON deserialization
     */
    private PlaygroundItem(){
    }

    /**
     * Constructor
     */
    public PlaygroundItem(int valueArg){
        value = valueArg;
    }

    /**
     * Initializes all values
     * @param valueArg input value for attribute value
     */
    public void initAllValues(int valueArg){
        value = valueArg;
        moveX = 0;
        moveY = 0;
    }

    /**
     * Copies all values provided by itemArg
     * @param itemArg object to be copied
     */
    public void setAllValues(PlaygroundItem itemArg){
        value = itemArg.value;
        moveX = itemArg.moveX;
        moveY = itemArg.moveY;
    }
}
