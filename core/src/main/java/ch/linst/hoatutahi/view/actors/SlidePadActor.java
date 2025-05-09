package ch.linst.hoatutahi.view.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import ch.linst.hoatutahi.components.MyAssetManager;


/**
 * Implements the logic of the slide pad.
 * The slide pad is used in the game to move the items smoothly in the desired direction
 */
public class SlidePadActor extends Group implements EventListener {

    public enum SpaEvent{
        DIRECTION_THRESHOLD_EXCEEDED,
        DEFLECTION_CHANGED,
        ABOVE_DEFLECTION_THRESHOLD,
        BELOW_DEFLECTION_THRESHOLD,
        TOUCH_UP_ABOVE_DEFLECTION_THRESHOLD,
        TOUCH_UP_BELOW_DEFLECTION_THRESHOLD,
        TOUCH_UP_BELOW_DIRECTION_THRESHOLD
    }

    public enum SpaSlideDirection {
        UNKNOWN,
        SLIDE_RIGHT,
        SLIDE_LEFT,
        SLIDE_UP,
        SLIDE_DOWN
    }

    private enum SpaState {
        READY,
        FINISH_SLIDE
    }

    private static final float padSize = 384f;
    private static final float setDirectionThreshold = 30f;
    private static final float sliderDeflectionThreshold = 0.98f;
    private static final long  swipeThresholdMs = 250; // Max delay time between touch down and touch up for triggering a swipe action

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private MyAssetManager assetManager;

    private Image sliderImg;
    private Image backgroundImg;

    private SpaSlideDirection spaSlideDirection = SpaSlideDirection.UNKNOWN;
    private SpaState spaState = SpaState.READY;

    private Vector2 initialPos = new Vector2(); // The initial pos of the touch down event
    private Vector2 farestPos = new Vector2();
    private Vector2 tempPos = new Vector2(); // Used for calculations between local- and stage coordinates

    private float sliderDeflection = 0f;

    private long initialTime = 0; // The initial time of the touch down event

    private AlphaAction bkgImgAlphaAction = new AlphaAction();
    private AlphaAction sliderImgAlphaAction = new AlphaAction();
    private FloatAction finishSlideAction = new FloatAction();

    public SlidePadActor(MyAssetManager assetManagerArg) {
        assetManager = assetManagerArg;

        backgroundImg = getNewBackgroundImg();
        addActor(backgroundImg);

        sliderImg = getNewSliderImg();
        addActor(sliderImg);

        addListener(this);

        bkgImgAlphaAction.setAlpha(1f);
        bkgImgAlphaAction.setDuration(1f);
        sliderImgAlphaAction.setAlpha(1f);
        sliderImgAlphaAction.setDuration(1f);
    }


    @Override
    public boolean handle(Event event) {

        if(!(event instanceof InputEvent)){
            return false;
        }

        InputEvent iEv = (InputEvent)event;

        // Return if the callback comes from a second of third touch object
        if((iEv.getPointer() != 0)){
            return false;
        }

        tempPos.set(iEv.getStageX(), iEv.getStageY());
        parentToLocalCoordinates(tempPos);




        // Handle event
        if(spaState == SpaState.READY){
            if(spaSlideDirection == SpaSlideDirection.UNKNOWN){
                if(iEv.getType() == InputEvent.Type.touchDown)    onTouchDown(tempPos.x, tempPos.y);
                if(iEv.getType() == InputEvent.Type.touchDragged) onTouchDraggedAtReady(tempPos.x, tempPos.y);
            }
            else if(spaSlideDirection == SpaSlideDirection.SLIDE_RIGHT){
                if(iEv.getType() == InputEvent.Type.touchDragged) onTouchDraggedAtSlideRight(tempPos.x);
            }
            else if(spaSlideDirection == SpaSlideDirection.SLIDE_LEFT){
                if(iEv.getType() == InputEvent.Type.touchDragged) onTouchDraggedAtSlideLeft(tempPos.x);
            }
            else if(spaSlideDirection == SpaSlideDirection.SLIDE_UP){
                if(iEv.getType() == InputEvent.Type.touchDragged) onTouchDraggedAtSlideUp(tempPos.y);
            }
            else if(spaSlideDirection == SpaSlideDirection.SLIDE_DOWN){
                if(iEv.getType() == InputEvent.Type.touchDragged) onTouchDraggedAtSlideDown(tempPos.y);
            }

            if(iEv.getType() == InputEvent.Type.touchUp) onTouchUp(tempPos.x, tempPos.y);
        }

        return true;
    }


    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }


    public float getSliderDeflection() {
        return sliderDeflection;
    }

    public boolean isSliderAboveDeflectionThreshold(){
        return sliderDeflection >= sliderDeflectionThreshold;
    }

    private void onTouchDown(float x, float y){

        initialTime = System.currentTimeMillis();

        sliderDeflection = 0f;

        initialPos.set(x, y);
        farestPos.set(initialPos);

        sliderImg.setX(x - sliderImg.getWidth() / 2);
        sliderImg.setY(y - sliderImg.getHeight() / 2);

        // Slowly make slider image visible
        sliderImgAlphaAction.restart();
        sliderImg.setColor(1f, 1f, 1f, 0f);
        sliderImg.addAction(sliderImgAlphaAction);
        sliderImg.setVisible(true);

        backgroundImg.setX(x - backgroundImg.getHeight() / 2);
        backgroundImg.setY(y - backgroundImg.getHeight() / 2);

    }

    private void onTouchDraggedAtReady(float x, float y){

        float deltaX = Math.abs(x - initialPos.x);
        float deltaY = Math.abs(y - initialPos.y);

        // Set new position of the slider
        if(deltaX >= deltaY){
            sliderImg.setX(x - sliderImg.getWidth() / 2);
            sliderImg.setY(initialPos.y - sliderImg.getHeight() / 2);
        }
        else{
            sliderImg.setX(initialPos.x - sliderImg.getWidth() / 2);
            sliderImg.setY(y - sliderImg.getHeight() / 2);
        }


        // Check if the threshold to decide the drag direction has been exceeded
        if(Math.max(deltaX, deltaY) > setDirectionThreshold){
            //System.out.println("hit: " + Math.max(deltaX, deltaY));
            if(deltaX >= deltaY){
                setSpaSlideDirection(((x - initialPos.x) > 0f)? SpaSlideDirection.SLIDE_RIGHT : SpaSlideDirection.SLIDE_LEFT);
            }
            else{ // deltaX < deltaY
                setSpaSlideDirection(((y - initialPos.y) > 0f)? SpaSlideDirection.SLIDE_UP : SpaSlideDirection.SLIDE_DOWN);
            }

            switch (spaSlideDirection){
                case SLIDE_RIGHT:
                    farestPos.x = initialPos.x + padSize;
                    backgroundImg.setRotation(0f);
                    break;
                case SLIDE_LEFT:
                    farestPos.x = initialPos.x - padSize;
                    backgroundImg.setRotation(180f);
                    break;
                case SLIDE_UP:
                    farestPos.y = initialPos.y + padSize;
                    backgroundImg.setRotation(90f);
                    break;
                case SLIDE_DOWN:
                    farestPos.y = initialPos.y - padSize;
                    backgroundImg.setRotation(270f);
                    break;
            }

            // Slowly make background image visible
            bkgImgAlphaAction.restart();
            backgroundImg.setColor(1f, 1f, 1f, 0f);
            backgroundImg.addAction(bkgImgAlphaAction);
            backgroundImg.setVisible(true);
        }
    }

    private void onTouchDraggedAtSlideRight(float x){
        x = Math.min(x, farestPos.x);
        x = Math.max(x, initialPos.x);
        sliderImg.setX(x - sliderImg.getWidth() / 2);

        setSliderDeflection(1f / (farestPos.x - initialPos.x) * (x - initialPos.x));
    }

    private void onTouchDraggedAtSlideLeft(float x){
        x = Math.max(x, farestPos.x);
        x = Math.min(x, initialPos.x);
        sliderImg.setX(x - sliderImg.getWidth() / 2);

        setSliderDeflection(1f - (1f / (initialPos.x - farestPos.x) * (x - farestPos.x)));
    }

    private void onTouchDraggedAtSlideUp(float y){
        y = Math.min(y, farestPos.y);
        y = Math.max(y, initialPos.y);
        sliderImg.setY(y - sliderImg.getHeight() / 2);

        setSliderDeflection(1f / (farestPos.y - initialPos.y) * (y - initialPos.y));
    }

    private void onTouchDraggedAtSlideDown(float y){
        y = Math.max(y, farestPos.y);
        y = Math.min(y, initialPos.y);
        sliderImg.setY(y - sliderImg.getHeight() / 2);

        setSliderDeflection(1f - (1f / (initialPos.y - farestPos.y) * (y - farestPos.y)));
    }

    private void onTouchUp(float x, float y){
        sliderImg.setVisible(false);
        backgroundImg.setVisible(false);

        if(spaSlideDirection == SpaSlideDirection.UNKNOWN){

            pcs.firePropertyChange("spaEvent", SpaEvent.TOUCH_UP_BELOW_DIRECTION_THRESHOLD, spaSlideDirection);
        }
        else{
            spaState = SpaState.FINISH_SLIDE;
            finishSlideAction.restart();
            finishSlideAction.setStart(sliderDeflection);

            if((sliderDeflection >= sliderDeflectionThreshold) ||
                    ((System.currentTimeMillis() - initialTime) < swipeThresholdMs)){

                finishSlideAction.setEnd(1f);
                finishSlideAction.setDuration(0.2f * (1f - sliderDeflection));

            }
            else{
                finishSlideAction.setEnd(0f);
                finishSlideAction.setDuration(0.2f * (sliderDeflection));
            }

            this.addAction(finishSlideAction);
        }
    }

    private Image getNewSliderImg() {
        Image img = new Image(assetManager.getTex(MyAssetManager.SLIDE_PAD_SLIDER_TEX));
        img.setWidth(padSize);
        img.setHeight(padSize);
        img.setVisible(false);
        img.setTouchable(Touchable.disabled);
        return img;
    }

    private Image getNewBackgroundImg(){
        Image img = new Image(assetManager.getTex(MyAssetManager.SLIDE_PAD_BG_TEX));
        float aspectRatio = img.getHeight() / img.getWidth();
        img.setWidth(padSize / aspectRatio);
        img.setHeight(padSize);
        img.setOrigin(padSize / 2, padSize / 2);
        img.setVisible(false);
        img.setTouchable(Touchable.disabled);

        return img;
    }

    private void setSpaSlideDirection(SpaSlideDirection stateArg){
        //System.out.println("spaSlideDirection: " + stateArg);
        SpaSlideDirection oldState = spaSlideDirection;
        spaSlideDirection = stateArg;

        if((oldState == SpaSlideDirection.UNKNOWN) && spaSlideDirection != SpaSlideDirection.UNKNOWN){
            pcs.firePropertyChange("spaEvent", SpaEvent.DIRECTION_THRESHOLD_EXCEEDED, spaSlideDirection);
        }
    }

    private void setSliderDeflection(float deflectionArg){
        //System.out.println("sliderDeflection: " + deflectionArg);
        float oldDeflection = sliderDeflection;
        sliderDeflection = deflectionArg;

        if(sliderDeflection != oldDeflection){
            pcs.firePropertyChange("spaEvent", SpaEvent.DEFLECTION_CHANGED, spaSlideDirection);
        }

        if((sliderDeflection >= sliderDeflectionThreshold) && (oldDeflection < sliderDeflectionThreshold)){
            pcs.firePropertyChange("spaEvent", SpaEvent.ABOVE_DEFLECTION_THRESHOLD, spaSlideDirection);
        }
        else if((sliderDeflection < sliderDeflectionThreshold) && (oldDeflection >= sliderDeflectionThreshold)){
            pcs.firePropertyChange("spaEvent", SpaEvent.BELOW_DEFLECTION_THRESHOLD, spaSlideDirection);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if(spaState == SpaState.FINISH_SLIDE){
            setSliderDeflection(finishSlideAction.getValue());
            if(finishSlideAction.getValue() == finishSlideAction.getEnd()){

                if((sliderDeflection >= sliderDeflectionThreshold) ||
                        ((System.currentTimeMillis() - initialTime) < swipeThresholdMs)){
                    pcs.firePropertyChange("spaEvent", SpaEvent.TOUCH_UP_ABOVE_DEFLECTION_THRESHOLD, spaSlideDirection);
                }
                else{
                    pcs.firePropertyChange("spaEvent", SpaEvent.TOUCH_UP_BELOW_DEFLECTION_THRESHOLD, spaSlideDirection);
                }
                setSpaSlideDirection(SpaSlideDirection.UNKNOWN);
                spaState = SpaState.READY;
            }
        }
    }
}
