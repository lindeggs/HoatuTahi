package ch.linst.hoatutahi.view.stages;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import ch.linst.hoatutahi.HoatuTahi;
import ch.linst.hoatutahi.components.MyAssetManager;
import ch.linst.hoatutahi.components.audio.AudioProvider;
import ch.linst.hoatutahi.components.playgroundModel.Playground;
import ch.linst.hoatutahi.view.actors.selectables.GlassGlobeActor;
import ch.linst.hoatutahi.view.actors.buttons.MyButton;
import ch.linst.hoatutahi.view.actors.PlaygroundActor;
import ch.linst.hoatutahi.view.actors.ScoreViewActor;
import ch.linst.hoatutahi.view.actors.SlidePadActor;

public class GameStage extends BaseStage {

    private Playground playground;
    private PlaygroundActor playgroundActor;
    private ScoreViewActor scoreViewActor;
    private ScoreViewActor highScoreViewActor;
    private GlassGlobeActor glassGlobeActor;
    private SlidePadActor slidePadActor;
    private Actor newGameBtnActor;
    private Actor gameOverActor;

    public GameStage(Viewport viewportArg, HoatuTahi hoatuTahi, Playground playgroundArg){
        super(viewportArg, hoatuTahi);

        hoatuTahi.getAudioProvider().playMusic(AudioProvider.Song.GameStageMusic);

        // Create new playground model
        playground = playgroundArg;
        setupPlayground();

        // Create and register all actors
        playgroundActor = getNewGamePlaygroundActor();
        addActor(playgroundActor);

        scoreViewActor = getNewScoreViewActor();
        scoreViewActor.setScore(playground.getCurrentScore());
        addActor(scoreViewActor);

        highScoreViewActor = getNewHighScoreViewActor();
        highScoreViewActor.setScore(playground.getHighScore());
        addActor(highScoreViewActor);

        addActor(getNewHighScoreLabelActor());

        glassGlobeActor = getNewGlassGlobeActor();
        addActor(glassGlobeActor);

        slidePadActor = getNewSlidePadActor();
        addActor(slidePadActor);

        addActor(getNewExitBtnActor());

        newGameBtnActor = getNewNewGameBtnActor();
        addActor(newGameBtnActor);

        gameOverActor = getNewGameOverActor();
        gameOverActor.setVisible(false);
        addActor(gameOverActor);

    }

    @Override
    public boolean keyDown(int keyCode) {
        // Check if the OS back button has been pressed
        if(keyCode == Input.Keys.BACK){
            hoatuTahi.setStage(HomeStage.class, null);
        }
        return false;
    }

    /**
     * Sets the playground up with the needed listeners in order to start or resume a game
     */
    private void setupPlayground() {
        playground.removeAllPropertyChangeListeners(); // This is required to not double register the listener to an existing playground
        playground.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                if(propertyChangeEvent.getPropertyName().equals(Playground.STATE_CHANGED_EV_ID)){
                    if((propertyChangeEvent.getNewValue() != null) && (propertyChangeEvent.getNewValue() instanceof Playground.State)){
                        Playground.State newState = (Playground.State)propertyChangeEvent.getNewValue();
                        //Gdx.app.log("debug", "New state = " + newState.toString());
                        if (newState == Playground.State.GAME_OVER){
                            onGameOver();
                        }
                    }
                }
                else if(propertyChangeEvent.getPropertyName().equals(Playground.SCORE_CHANGED_EV_ID)){
                    if((propertyChangeEvent.getNewValue() != null) && (propertyChangeEvent.getNewValue() instanceof Integer)){
                        scoreViewActor.setTargetScore((Integer)propertyChangeEvent.getNewValue());
                        highScoreViewActor.setTargetScore(playground.getHighScore());
                    }
                }
            }
        });
        playground.event(Playground.Event.START_GAME);
    }


    private void onGameOver(){

        slidePadActor.setVisible(false);
        slidePadActor.setTouchable(Touchable.disabled);
        glassGlobeActor.setVisible(false);

        newGameBtnActor.setVisible(true);
        gameOverActor.setVisible(true);

        // Remove playground from GameFactory
        hoatuTahi.getGameFactory().removePlayground(playground);

        hoatuTahi.getAudioProvider().playMusic(AudioProvider.Song.GameOver);
    }

    private ScoreViewActor getNewScoreViewActor() {
        ScoreViewActor actor = new ScoreViewActor(assetManager, hoatuTahi.getAudioProvider());
        actor.setPosition((hoatuTahi.getScreenWidth() - actor.getWidth()) / 2 - 100,
                hoatuTahi.getScreenHeight() - actor.getHeight() - 40);
        return  actor;
    }

    private ScoreViewActor getNewHighScoreViewActor() {
        ScoreViewActor actor = new ScoreViewActor(assetManager);
        float scale = 0.5f;
        actor.setScale(scale);
        actor.setX(playgroundActor.getX() + playgroundActor.getWidth()  - actor.getWidth() - 20);
        actor.setY(hoatuTahi.getScreenHeight() - actor.getHeight() - 120);
        return  actor;
    }

    private Actor getNewHighScoreLabelActor(){
        Image img = new Image(assetManager.getTex(MyAssetManager.HIGH_SCORE_LABEL_TEX));
        img.setX(highScoreViewActor.getX() - 30);
        img.setY(highScoreViewActor.getY() + highScoreViewActor.getHeight() * highScoreViewActor.getScaleY() + 60);
        return img;
    }

    private GlassGlobeActor getNewGlassGlobeActor(){
        float scale = 2f;
        int gameLevel = hoatuTahi.getGameFactory().getGameLevel();

        GlassGlobeActor actor = hoatuTahi.getGameViewFactory().getGlassGlobeActor(playground, gameLevel);
        actor.setScale(scale);
        actor.setX(hoatuTahi.getScreenWidth() * 0.95f - actor.getWidth() * scale);
        actor.setY(playgroundActor.getY() - actor.getHeight() * scale - 30);
        if(gameLevel <= 1){
            actor.switchOn();
        }
        else{
            actor.setVisible(false);
        }

        return actor;
    }

    private PlaygroundActor getNewGamePlaygroundActor(){

        int gameLevel = hoatuTahi.getGameFactory().getGameLevel();
        PlaygroundActor actor = hoatuTahi.getGameViewFactory().getPlaygroundActor(playground, gameLevel);

        actor.setWidth(hoatuTahi.getScreenWidth() * 0.98f);
        actor.setHeight(actor.getWidth() / playground.getSizeX() * playground.getSizeY());
        actor.setX((hoatuTahi.getScreenWidth() - actor.getWidth()) / 2);
        actor.setY(hoatuTahi.getScreenHeight() - actor.getHeight() - 250);
        actor.setOrigin(actor.getWidth() / 2f, actor.getHeight() / 2f);
        //actor.setScale(0.9f);
        actor.updateGroup();
        return actor;
    }


    private SlidePadActor getNewSlidePadActor(){
        final SlidePadActor slidePadActor = new SlidePadActor(assetManager);
        slidePadActor.setWidth(hoatuTahi.getScreenWidth());
        slidePadActor.setHeight(hoatuTahi.getScreenHeight());
        slidePadActor.setX(0f);
        slidePadActor.setY(0f);

        slidePadActor.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                // Check if the "OldValue" exists and if it is an SpaEvent
                if(((propertyChangeEvent.getOldValue() != null) && (propertyChangeEvent.getOldValue() instanceof SlidePadActor.SpaEvent)) &&
                        ((propertyChangeEvent.getNewValue() != null) && (propertyChangeEvent.getNewValue() instanceof SlidePadActor.SpaSlideDirection))){
                    SlidePadActor.SpaEvent spaEvent = (SlidePadActor.SpaEvent)propertyChangeEvent.getOldValue();
                    SlidePadActor.SpaSlideDirection spaState = (SlidePadActor.SpaSlideDirection)propertyChangeEvent.getNewValue();
                    onSlidePadActorPropertyChange(spaEvent, spaState);
                    //System.out.println("spaEvent: " + spaEvent.toString() + "    spaState: " + spaState.toString());
                }
            }

        });
        return slidePadActor;
    }

    private void onSlidePadActorPropertyChange(SlidePadActor.SpaEvent spaEvent, SlidePadActor.SpaSlideDirection spaState){

        int gameLevel = hoatuTahi.getGameFactory().getGameLevel();

        if(spaEvent == SlidePadActor.SpaEvent.DEFLECTION_CHANGED){
            //System.out.println("Deflection: " + slidePadActor.getSliderDeflection());
        }
        else if(spaEvent == SlidePadActor.SpaEvent.DIRECTION_THRESHOLD_EXCEEDED){
            switch (spaState){
                case SLIDE_RIGHT:
                    playground.event(Playground.Event.MOVE_RIGHT);
                    break;
                case SLIDE_LEFT:
                    playground.event(Playground.Event.MOVE_LEFT);
                    break;
                case SLIDE_UP:
                    playground.event(Playground.Event.MOVE_UP);
                    break;
                case SLIDE_DOWN:
                    playground.event(Playground.Event.MOVE_DOWN);
                    break;
            }
        }
        else if(spaEvent == SlidePadActor.SpaEvent.ABOVE_DEFLECTION_THRESHOLD){
            if(gameLevel == 0){
                glassGlobeActor.showItem(playground.getNextNewItem().value);
            }
            else if(gameLevel == 1){
                glassGlobeActor.showItem(0);
            }
        }
        else if(spaEvent == SlidePadActor.SpaEvent.BELOW_DEFLECTION_THRESHOLD){
            playground.event(Playground.Event.UPDATE_NEW_ITEM);
            if(gameLevel <= 1){
                glassGlobeActor.switchOn();
            }
        }
        else if(spaEvent == SlidePadActor.SpaEvent.TOUCH_UP_ABOVE_DEFLECTION_THRESHOLD){
            playground.event(Playground.Event.COMMIT);
            if(gameLevel <= 1){
                glassGlobeActor.switchOn();
            }
        }
        else if(spaEvent == SlidePadActor.SpaEvent.TOUCH_UP_BELOW_DEFLECTION_THRESHOLD){
            playground.event(Playground.Event.ROLLBACK);
        }
        playgroundActor.updateGroup(slidePadActor.getSliderDeflection(), slidePadActor.isSliderAboveDeflectionThreshold());
    }


    /**
     * Creates the new game button actor and returns it
     */
    private Actor getNewNewGameBtnActor() {
        TextureRegion tex1 = assetManager.getAtlas(MyAssetManager.BUTTON_ATLAS).findRegion("BtnNewGame");
        TextureRegion tex2 = assetManager.getAtlas(MyAssetManager.BUTTON_ATLAS).findRegion("BtnNewGameAct");

        final MyButton btn = new MyButton(tex1, tex2, null, null);
        btn.setX((hoatuTahi.getScreenWidth() - btn.getWidth())/2);
        btn.setY(300);
        btn.setVisible(false);

        btn.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                btn.setVisible(false);
                gameOverActor.setVisible(false);

                if(hoatuTahi.getGameFactory().getGameLevel() <= 1){
                    glassGlobeActor.setVisible(true);
                }
                slidePadActor.setVisible(true);
                slidePadActor.setTouchable(Touchable.enabled);

                // Start new game
                // The previous playground has been destroyed in the geme factory at game over.
                // Therefore it is important to get a new playground instance from the factory
                playground = hoatuTahi.getGameFactory().getNewPlayground(playground.getSizeX(), playground.getSizeY());
                setupPlayground();
                scoreViewActor.setScore(playground.getCurrentScore());

                playgroundActor.setPlayground(playground);
                playgroundActor.updateGroup();

                hoatuTahi.getAudioProvider().playMusic(AudioProvider.Song.GameStageMusic);
            }
        });

        return btn;
    }

    /**
     * Creates the back button actor and adds it to the stage
     */
    private Actor getNewExitBtnActor() {

        TextureRegionDrawable trd1 = new TextureRegionDrawable(assetManager.getAtlas(MyAssetManager.BUTTON_ATLAS).findRegion("Exit"));
        TextureRegionDrawable trd2 = new TextureRegionDrawable(assetManager.getAtlas(MyAssetManager.BUTTON_ATLAS).findRegion("ExitActive"));
        Button backBtn = new Button(trd1, trd2);

        backBtn.setX(playgroundActor.getX());
        backBtn.setY(hoatuTahi.getScreenHeight() - backBtn.getHeight() - 30);

        backBtn.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hoatuTahi.setStage(HomeStage.class, null);
            }
        });

        return backBtn;
    }

    private Actor getNewGameOverActor(){

        float rotateThreshold = 1f;
        float rotateDuration = 4f;
        float moveAmountY = 400f;
        float moveDuration = 20f;

        Image img = new Image(assetManager.getTex(MyAssetManager.GAME_OVER_TEX));
        img.setTouchable(Touchable.disabled);

        float aspectRatio = img.getPrefHeight() / img.getPrefWidth();

        img.setWidth(HoatuTahi.VIRT_SCREEN_WIDTH * 0.9f);
        img.setHeight(img.getWidth() * aspectRatio);

        img.setX((hoatuTahi.getScreenWidth() / 2) - (img.getWidth() / 2));
        img.setY(playgroundActor.getY() + (playgroundActor.getHeight() / 2) - (img.getHeight() / 2) - (moveAmountY / 2));

        img.setOrigin(img.getWidth() / 2, img.getHeight() / 2);
        img.setRotation(-rotateThreshold);

        RepeatAction repA1 = getGameOverRotationRepAction(rotateThreshold, rotateDuration);
        RepeatAction repA2 = getGameOverMoveRepAction(moveAmountY, moveDuration);
        img.addAction(new ParallelAction(repA1, repA2));
        return img;
    }

    private RepeatAction getGameOverRotationRepAction(float rotateThreshold, float rotateDuration) {
        RotateToAction rotA1 = new RotateToAction();
        rotA1.setRotation(rotateThreshold);
        rotA1.setDuration(rotateDuration);
        RotateToAction rotA2 = new RotateToAction();
        rotA2.setRotation(-rotateThreshold);
        rotA2.setDuration(rotateDuration);
        SequenceAction sqa = new SequenceAction(rotA1, rotA2);
        RepeatAction repA = new RepeatAction();
        repA.setAction(sqa);
        repA.setCount(RepeatAction.FOREVER);
        return repA;
    }

    private RepeatAction getGameOverMoveRepAction(float moveAmountY, float moveDuration) {

        MoveByAction mbA1 = new MoveByAction();
        mbA1.setAmountY(moveAmountY);
        mbA1.setDuration(moveDuration);
        MoveByAction mbA2 = new MoveByAction();
        mbA2.setAmountY(-moveAmountY);
        mbA2.setDuration(moveDuration);
        SequenceAction sqa = new SequenceAction(mbA1, mbA2);
        RepeatAction repA2 = new RepeatAction();
        repA2.setAction(sqa);
        repA2.setCount(RepeatAction.FOREVER);
        return repA2;
    }

}
