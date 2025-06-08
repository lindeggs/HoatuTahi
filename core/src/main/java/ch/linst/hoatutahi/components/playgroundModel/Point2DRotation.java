package ch.linst.hoatutahi.components.playgroundModel;

/**
 * This class provides functionality to rotate a point in a 2D field
 */
public class Point2DRotation {

    private int xSize = 0;
    private int ySize = 0;
    private int xPos  = 0;
    private int yPos  = 0;

    private Direction direction = Direction.LEFT;

    /**
     * Constructor (private)
     * This constructor is needed for JSON deserialization
     */
    @SuppressWarnings("unused")
    private Point2DRotation() {
    }

    /**
     * Constructor
     * @param xSizeArg Size of 2D array in X direction
     * @param ySizeArg Size of 2D array in Y direction
     */
    public Point2DRotation(int xSizeArg, int ySizeArg){
        xSize = xSizeArg;
        ySize = ySizeArg;
    }

    /**
     * Sets the X and the Y position attribute
     * @param xPosArg X position
     * @param yPosArg Y position
     */
    public void setPoint2D(int xPosArg, int yPosArg){
        xPos = xPosArg;
        yPos = yPosArg;
    }

    /**
     * Returns the X position rotated to the current direction
     * @return rotated X postion
     */
    public int getX() {

        int retVal = xPos;

        switch(direction){
            case LEFT:
                // No action
                break;
            case RIGHT:
                retVal = (xSize - 1) - xPos;
                break;
            case UP:
                retVal = yPos;
                break;
            case DOWN:
                retVal = (ySize - 1) - yPos;
                break;
            default:
                break;
        }
        return retVal;
    }

    /**
     * Returns the Y position rotated to the current direction
     * @return rotated Y postion
     */
    public int getY() {

        int retVal = yPos;

        switch(direction){
            case LEFT:
                // No action
                break;
            case RIGHT:
                retVal = (ySize - 1) - yPos;
                break;
            case UP:
                retVal = xPos;
                break;
            case DOWN:
                retVal = (xSize - 1) - xPos;
                break;
            default:
                break;
        }
        return retVal;
    }

    /**
     * Sets the direction to rotate to
     * @param direction Direction to rotate (up, down, left or right)
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * Sets the size of the 2D array in X direction
     * @param xSize Size in X direction
     */
    public void setxSize(int xSize) {
        this.xSize = xSize;
    }

    /**
     * Sets the size of the 2D array in Y direction
     * @param ySize Size in Y direction
     */
    public void setySize(int ySize) {
        this.ySize = ySize;
    }
}
