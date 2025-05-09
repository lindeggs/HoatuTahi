package ch.linst.hoatutahi.components.playgroundModel;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Represents the model of the Playground
 * - it represents the playground with all it's items
 * - it impements the logical gameplay
 * - it handles gamer inputs and calculates the game states according to the defined rules
 */
public class Playground {

    public enum Event{
        START_GAME,
        MOVE_LEFT,
        MOVE_RIGHT,
        MOVE_UP,
        MOVE_DOWN,
        UPDATE_NEW_ITEM,
        COMMIT,
        ROLLBACK

    }

    public enum State{
        GAME_OVER,
        AWAITING_MOVE,
        AWAITING_COMMIT
    }

    private static final int MAX_ITEM_LEVEL = 16;
    private static final int MAX_PLAYGROUND_FIELDS = 100;
    private static final int MIN_PLAYGROUND_SIZE_XY = 2;

    public static final String STATE_CHANGED_EV_ID = "StateChanged";
    public static final String SCORE_CHANGED_EV_ID = "ScoreChanged";

    protected PlaygroundItem currentItems[][];
    protected PlaygroundItem nextItems[][];
    protected PlaygroundItem nextNewItem;
    protected int nextNewItemPosX = 0;
    protected int nextNewItemPosY = 0;

    private final transient PropertyChangeSupport pcs;
    private final transient Random rndGen;

    // When deserializeing high score is injected from outside, because it must be saved independent from the playground
    private transient IntRef highScore = new IntRef(0);

    // When deserializeing highestItemUnveiled is injected from outside, because it must be saved independent from the playground
    private transient IntRef highestItemUnveiled = new IntRef(6);

    private int currentScore = 0;
    private int nextScore    = 0;

    private State state = State.GAME_OVER;

    private int sizeX;
    private int sizeY;

    private Point2DRotation transLtdRot;
    private Point2DRotation transLtdBaseRot;

    /**
     * Constructor (private)
     * This constructor is needed for JSON deserialization
     */
    private Playground() {
        pcs = new PropertyChangeSupport(this);
        rndGen = new Random();
    }

    public Playground(int sizeXArg, int sizeYArg, IntRef highScoreArg, IntRef highestItemUnveiledArg, boolean initFirstItemArg){
        this();

        if((sizeXArg < MIN_PLAYGROUND_SIZE_XY) || (sizeYArg < MIN_PLAYGROUND_SIZE_XY)){
            throw new IllegalArgumentException("Playground must be minimal " +
                    MIN_PLAYGROUND_SIZE_XY + " x \" + MIN_PLAYGROUND_SIZE_XY + \" fields wide");
        } else if((sizeXArg * sizeYArg) > MAX_PLAYGROUND_FIELDS){
            throw new IllegalArgumentException("Playground max fields must not exceed " + MAX_PLAYGROUND_FIELDS);
        } else if(highScoreArg == null){
            throw new IllegalArgumentException("High score parameter must not be null ");
        }

        sizeX = sizeXArg;
        sizeY = sizeYArg;
        highScore = highScoreArg;
        highestItemUnveiled = highestItemUnveiledArg;

        transLtdRot = new Point2DRotation(sizeX, sizeY);
        transLtdBaseRot = new Point2DRotation(sizeX, sizeY);

        currentItems = new PlaygroundItem[sizeYArg][sizeXArg];
        nextItems = new PlaygroundItem[sizeYArg][sizeXArg];
        nextNewItem = new PlaygroundItem(0);

        initPlaygroundItems(currentItems);
        initPlaygroundItems(nextItems);

        if(initFirstItemArg){
            setRandomNextItem();
            commit();
        }
    }

    public Playground(int sizeXArg, int sizeYArg, IntRef highScoreArg, IntRef highestItemUnveiledArg){
        this(sizeXArg, sizeYArg, highScoreArg, highestItemUnveiledArg, true);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removeAllPropertyChangeListeners(){
        for (PropertyChangeListener listener : pcs.getPropertyChangeListeners()){
            pcs.removePropertyChangeListener(listener);
        }
    }

    /**
     * Handles an event from outside
     * @param ev Event to be handled
     */
    public void event(Event ev){

        switch (state){
            case GAME_OVER:
                eventAtGameOver(ev);
                break;
            case AWAITING_MOVE:
                eventAtReadyToMove(ev);
                break;
            case AWAITING_COMMIT:
                eventAtReadyToCommit(ev);
                break;
            default:
                break;
        }
    }

    public final Iterable<PlaygroundItem> oldGetCurrentItems(){

        List<PlaygroundItem> retVal = new ArrayList<PlaygroundItem>();

        for(int y = 0; y < currentItems.length; y++){
            for(int x = 0; x < currentItems[y].length; x++){
                if(currentItems[y][x].value > 0){
                    retVal.add(currentItems[y][x]);
                }
            }
        }

        return retVal;
    }

    public PlaygroundItem getNextNewItem() {
        return nextNewItem;
    }

    public int getNextNewItemPosX() {
        return nextNewItemPosX;
    }

    public int getNextNewItemPosY() {
        return nextNewItemPosY;
    }

    public PlaygroundItem[][] getCurrentItems() {
        return currentItems;
    }

    private void eventAtGameOver(Event ev){
        if(ev == Event.START_GAME){
            // Reset game score
            nextScore = 0;
            setCurrentScore(0);

            initPlaygroundItems(currentItems);
            initPlaygroundItems(nextItems);

            setRandomNextItem();
            commit();

            setState(State.AWAITING_MOVE);
        }
    }

    private void eventAtReadyToMove(Event ev){
        boolean playfieldHasChanged = false;

        switch (ev){
            case MOVE_LEFT:
                playfieldHasChanged = moveItems(Direction.LEFT, true);
                break;
            case MOVE_RIGHT:
                playfieldHasChanged = moveItems(Direction.RIGHT, true);
                break;
            case MOVE_UP:
                playfieldHasChanged = moveItems(Direction.UP, true);
                break;
            case MOVE_DOWN:
                playfieldHasChanged = moveItems(Direction.DOWN, true);
                break;
            default:
                break;

        }

        // add a new item if the playfield has been changed by moving items
        if(playfieldHasChanged){
            setRandomNextItem();
            setState(State.AWAITING_COMMIT);
        }
    }

    private void eventAtReadyToCommit(Event ev){
        if(ev == Event.COMMIT){
            commit();
            setCurrentScore(nextScore);
            if(checkGameOver()){
                setState(State.GAME_OVER);
            }
            else {
                setState(State.AWAITING_MOVE);
            }
        }
        else if(ev == Event.ROLLBACK){
            rollback();
            nextScore = currentScore;
            setState(State.AWAITING_MOVE);
        }
        else if(ev == Event.UPDATE_NEW_ITEM){
            setRandomNextItem();
        }
    }

    /**
     * Performs a movement of the items on the playground.
     * The source of the movement is currentItems (the current state of the playfield).
     * The movement result is stored in nextItems (stashed next playfield state).
     * @param directionArg is the direction where the items are moved towards.
     * @return true if at least one item could be moved
     */
    private boolean moveItems(Direction directionArg, boolean regardScore){

        copyPlaygroundItems(currentItems, nextItems);

        int transMax = ((directionArg == Direction.LEFT) || (directionArg == Direction.RIGHT))? sizeY : sizeX;
        int ltdMax = ((directionArg == Direction.LEFT) || (directionArg == Direction.RIGHT))? sizeX : sizeY;

        transLtdRot.setDirection(directionArg);
        transLtdRot.setxSize(ltdMax);
        transLtdRot.setySize(transMax);

        transLtdBaseRot.setDirection(directionArg);
        transLtdBaseRot.setxSize(ltdMax);
        transLtdBaseRot.setySize(transMax);

        // Iterate over the "transPos"
        // Transverse to the direction of movement
        for(int transPos = 0; transPos < transMax; transPos++){
            // Iterate over the "ltdBasePos"
            // ltdBasePos is aligned longitudinally to the direction of the movement.
            // Position 0 is the position where the items are moved towards to
            for(int ltdBasePos = 0; ltdBasePos < (ltdMax - 1); ltdBasePos++){
                for(int ltdPos = ltdBasePos + 1; ltdPos < ltdMax; ltdPos++){
                    transLtdRot.setPoint2D(ltdPos, transPos);
                    transLtdBaseRot.setPoint2D(ltdBasePos, transPos);
                    PlaygroundItem nextItems_CurrentBaseItem = nextItems[transLtdBaseRot.getY()][transLtdBaseRot.getX()];
                    PlaygroundItem nextItems_CurrentItem = nextItems[transLtdRot.getY()][transLtdRot.getX()];

                    // Check if the field at ltdPos is not empty
                    if(nextItems_CurrentItem.value > 0){
                        if(nextItems_CurrentBaseItem.value == 0) {
                            // Move the nextItem from ltdPos to ltdBasePos
                            nextItems_CurrentBaseItem.value = nextItems_CurrentItem.value;
                            nextItems_CurrentItem.value = 0;
                            // Set move vector on currentItem
                            currentItems[transLtdRot.getY()][transLtdRot.getX()].moveX = transLtdBaseRot.getX() - transLtdRot.getX();
                            currentItems[transLtdRot.getY()][transLtdRot.getX()].moveY = transLtdBaseRot.getY() - transLtdRot.getY();
                        }
                        else if((nextItems_CurrentItem.value == nextItems_CurrentBaseItem.value) &&
                                (nextItems_CurrentItem.value < MAX_ITEM_LEVEL)){
                            // Join the item from ltdPos with the item on ltdBasePos
                            nextItems_CurrentBaseItem.value++;
                            nextItems_CurrentItem.value = 0;
                            // Set move vector on currentItem
                            currentItems[transLtdRot.getY()][transLtdRot.getX()].moveX = transLtdBaseRot.getX() - transLtdRot.getX();
                            currentItems[transLtdRot.getY()][transLtdRot.getX()].moveY = transLtdBaseRot.getY() - transLtdRot.getY();
                            // Increase currentScore
                            if(regardScore){
                                nextScore += (int)Math.round(Math.pow(2, nextItems_CurrentBaseItem.value - 1));
                            }
                            break; // Move ltdBasePos forward (only one join per field is possible)
                        }
                        else{
                            break; // Move ltdBasePos forward (ltdPos was not empty but join was not possible)
                        }

                    }
                }
            }
        }

        return comparePlaygrounds();
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public State getState() {
        return state;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public int getHighScore() {
        return highScore.value;
    }

    public void setHighScoreRef(IntRef highScore) {
        this.highScore = highScore;
    }

    public void setHighestItemUnveiledRef(IntRef highestItemUnveiled) {
        this.highestItemUnveiled = highestItemUnveiled;
    }

    private void setCurrentScore(int scoreArg){
        if(scoreArg != currentScore){
            int oldScore = currentScore;
            currentScore = scoreArg;
            highScore.value = (currentScore > highScore.value)? currentScore : highScore.value;
            pcs.firePropertyChange(SCORE_CHANGED_EV_ID, oldScore, currentScore);
        }

    }

    private void setState(State state) {
        State oldState = this.state;
        this.state = state;
        pcs.firePropertyChange(STATE_CHANGED_EV_ID, oldState, state);
    }

    /**
     * Tests if GameOver has been reached (if the playground can not be moved in any direction)
     * @return true if game is over
     */
    private boolean checkGameOver(){

        boolean isGameOver = true;
        Direction directions[] = {Direction.RIGHT, Direction.LEFT, Direction.UP, Direction.DOWN};

        // Check if its possible to move in any direction
        for(Direction dir: directions){
            if(moveItems(dir, false) == true){
                isGameOver = false;
                revert();
                break;
            }
        }

        return isGameOver;
    }

    /**
     * Reverts a started movement of items
     */
    private void revert(){

        for(int y = 0; y < currentItems.length; y++){
            for(int x = 0; x < currentItems[y].length; x++){
                currentItems[y][x].moveX = 0;
                currentItems[y][x].moveY = 0;
            }
        }
        initPlaygroundItems(nextItems);
    }

    /**
     * Copies nextItems to currentItems and deletes nextItems (commits the next playfield state)
     */
    private void commit(){
        // Copy the next items to the current items
        copyPlaygroundItems(nextItems, currentItems);

        highestItemUnveiled.value = Math.max(highestItemUnveiled.value, getHighestPlaygroundItem(currentItems));

        // Add the new Item to the current items
        if(nextNewItem.value > 0){
            currentItems[nextNewItemPosY][nextNewItemPosX].setAllValues(nextNewItem);
        }

        // Init the nextItems and the nextNewItem
        initPlaygroundItems(nextItems);
        nextNewItem.initAllValues(0);
    }

    /**
     * Deletes all nextItems and the nextNewItem.
     * Initializes the "move" attributes from the currentItems
     */
    private void rollback(){
        // Init the nextItems and the nextNewItem
        initPlaygroundItems(nextItems);
        nextNewItem.initAllValues(0);

        initPlaygroundItemsMoveAttribute(currentItems);
    }

    /**
     * Compares the Current with the next playground
     * @return true if at least one item is different
     */
    private boolean comparePlaygrounds(){
        for(int y = 0; y < currentItems.length; y++){
            for(int x = 0; x < currentItems[y].length; x++){
                if(currentItems[y][x].value != nextItems[y][x].value){
                    return true;
                }
            }
        }
        return false;
    }

    private void copyPlaygroundItems(PlaygroundItem srcItems[][], PlaygroundItem destItems[][]){
        for(int y = 0; y < srcItems.length; y++){
            for(int x = 0; x < srcItems[y].length; x++){
                destItems[y][x].setAllValues(srcItems[y][x]);
            }
        }
    }

    private int getHighestPlaygroundItem(PlaygroundItem itemsArg[][]){
        int highestPgItem = 0;
        for(int y = 0; y < itemsArg.length; y++){
            for(int x = 0; x < itemsArg[y].length; x++){
                highestPgItem = Math.max(itemsArg[y][x].value, highestPgItem);
            }
        }
        return highestPgItem;
    }

    /**
     * Sets a new random next item using member nextNewItem (sets its position and value from 0 to 1 or 2)
     * This operation searches a random empty field on the nextItems playfield.
     * @return true if a new item could be set
     */
    protected boolean setRandomNextItem() {

        if(countEmptyfields(nextItems) == 0){
            return false;
        }

        int xPos;
        int yPos;
        do{
            xPos = rndGen.nextInt(sizeX);
            yPos = rndGen.nextInt(sizeY);
        } while (nextItems[yPos][xPos].value > 0);

        nextNewItemPosX = xPos;
        nextNewItemPosY = yPos;
        nextNewItem.value = (rndGen.nextInt(100) > 90)? 2 : 1;
        //nextNewItem.value = (rndGen.nextInt(100) > 90)? 14 : 14; //todo remove
        return true;
    }

    /**
     * Counts the empty fileds (the fileds with value == 0) in a items field
     * @param itemsArg 2D array of items
     * @return empty items count
     */
    private int countEmptyfields(PlaygroundItem itemsArg[][]){

        int retVal = 0;

        for(int y = 0; y < itemsArg.length; y++){
            for(int x = 0; x < itemsArg[y].length; x++){
                if(itemsArg[y][x].value == 0){
                    retVal++;
                }
            }
        }
        return retVal;
    }

    private void initPlaygroundItemsMoveAttribute(PlaygroundItem[][] itemsArg) {

        for(int y = 0; y < itemsArg.length; y++){
            for(int x = 0; x < itemsArg[y].length; x++){
                itemsArg[y][x].moveX = 0;
                itemsArg[y][x].moveY = 0;
            }
        }
    }


    /**
     * Initializes a field of PlaygroundItems (creates new objects if required)
     * @param itemsArg items to delete or instantiate
     */
    protected void initPlaygroundItems(PlaygroundItem[][] itemsArg) {

        for(int y = 0; y < itemsArg.length; y++){
            for(int x = 0; x < itemsArg[y].length; x++){
                if(itemsArg[y][x] == null){
                    itemsArg[y][x] = new PlaygroundItem(0);
                }
                else{
                    itemsArg[y][x].initAllValues(0);
                }

            }
        }
    }

}
