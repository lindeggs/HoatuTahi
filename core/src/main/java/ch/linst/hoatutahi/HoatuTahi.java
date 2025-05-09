package ch.linst.hoatutahi;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import ch.linst.hoatutahi.components.FakeAdService;
import ch.linst.hoatutahi.components.MyAssetManager;
import ch.linst.hoatutahi.components.audio.AudioProvider;
import ch.linst.hoatutahi.components.PlatformService;
import ch.linst.hoatutahi.components.billing.BillingManager;
import ch.linst.hoatutahi.components.billing.service.BillingService;
import ch.linst.hoatutahi.components.gameBackup.BackupHandler;
import ch.linst.hoatutahi.components.gameBackup.GameFactory;
import ch.linst.hoatutahi.components.gameBackup.GameViewFactory;
import ch.linst.hoatutahi.components.playgroundModel.Playground;
import ch.linst.hoatutahi.view.stages.GameStage;
import ch.linst.hoatutahi.view.stages.HomeStage;
import ch.linst.hoatutahi.view.stages.SettingsStage;
import ch.linst.hoatutahi.view.stages.SplashStage;

//TODO Feature list

/**
 * Done:
 * - Game playable
 * - Move Items smoothly using SlidePadActor
 * - Show next item during item movement and change position of next item when sliding back and forth
 * - Set alpha to next displayed item
 * - Adapt size of items in relation to the playfield size
 * - Define game icon
 * - Change "Back" button to "Exit" and move position
 * - Implement score count
 * - provide option to set playground size
 * - Persist game state
 * - Implement and persist high score
 * - Add versioning information to backup file
 * - Show app version on home screen
 * - Label the field "high score"
 * - Issue: SlidePadActor has problems with multitouch (playground item temporarily disappear)
 * - Provide option to restart game (restart or resume)
 * - Issue: High Score is not always updated (it would be better to inject the highscore object into the pg obj)
 * - Handle Android OS back button
 * - Beautify home screen
 * - Implement sounds
 * - Provide switch to enable or disable the sound
 * - Make sound on off selection persistent
 * - Improve slide pad actor to support swipe
 * - Improve slide pad actor to smoothly finish an input (smoothly move the items to the end or start pos)
 * - Implement adds
 * - Multiple Levels: Add GlasGlobe to GameStage
 * - Multiple Levels: Change the way the next ball is viewed
 * - Multiple Levels: Add level selection to HomeStage (only the view)
 * - Multiple Levels: Add persistency of level selection
 * - Multiple Levels: Implement prediction for all levels
 * - Multiple Levels: Implement individual high score per difficulty level
 * - Multiple Levels: Show high score on HomeStage
 * - Implement score view which "tickers" the score value
 * - Implement sound for score update (like money tinkle)
 * - Add licenced music and play with gaps
 * - Exchange Buttons with nicer texture
 * - Show build type on home stage
 * - Add new game button to GameStage
 * - Implement chance to spawn higher level balls
 * - Block combining the biggest balls
 * - Avoid game score overflow
 * - Updated assets to reduce data size
 * - Switched to android SDK version 29
 * - Implemented adaption of score update speed
 * - Implement settings screen
 * - Add music and sound on off option on settings screen
 * - Add second item set (textures not yet final)
 * - Show real playfields in the main menu
 * - Prepare in app purchase mechanism for Android
 * - Implement in app purchase to get second item set
 * - Play Game over words and music even if music is switched off
 * - Redesign itemset selector spritesheet
 * - Update Settings stage: Remove sound- and noise switch
 * - Update Settings stage: Move up itemset selector buttons
 * - Update Settings stage: Update layouting of items
 * - Update Settings stage: Animate layouting of items
 * - Update Settings stage: Provide items preview
 * - Correct product id of second item set (its not fidget spinner anymore)
 * - Advance to Android latest API level (at least 30)
 * - Enhance sound when counting points (avoid jitter)
 * - Privacy policy in german language
 * - Check App preview assets (https://support.google.com/googleplay/android-developer/answer/9866151#zippy=)
 * - Upgrade to latest google play billing (this is mandatory till 01.11.2021)
 * - Release App !!!
 *
 * Next tasks:
 *
 * - Implement "you sure" popup when starting a new game (deleting an ongoing one)
 * - Implement signature for game status (security)
 * - Implement more sounds (e.g. click sound)
 *
 * - Create multilayer ray
 * - Create more animations (e.g. when screens are switched)
 * - Implement full screen ads (removed when the first item is bought)
 * - Implement interaction with single items on settings stage (easter egg)
 * - Implement special animation when a new higher level item appears
 *
 * Ideas:
 * - Add time constraint (play against time)
 * - Try to be game over as fast as possible
 *
 */


public class HoatuTahi extends ApplicationAdapter {


    public static final int VIRT_SCREEN_HEIGHT = 2560;
    public static final int VIRT_SCREEN_WIDTH  = 1440;

    private PlatformService platformService;
    private FakeAdService adService;
    private BillingManager billingManager;

    private Color clearColor = new Color(0x54C2C0FF);

	private Camera camera;
	private Viewport viewport;
	private Stage stage;

	private BackupHandler backupHandler;


    public HoatuTahi(PlatformService platformServiceArg, FakeAdService adServiceArg, BillingService billingServiceArg) {
        super();
        platformService = platformServiceArg;
        adService = adServiceArg;
        billingManager = new BillingManager(billingServiceArg);
    }


    public void setStage(Class stageClassArg, Object arg) {

        if(stage != null){
            stage.dispose();
        }

        if(stageClassArg == SplashStage.class){
            stage = new SplashStage(viewport, this);
        }
        else if(stageClassArg == HomeStage.class){
            stage = new HomeStage(viewport, this);
        }
        else if((stageClassArg == GameStage.class) && (arg instanceof Playground)){
            stage = new GameStage(viewport, this, (Playground)arg);
        }
        else if(stageClassArg == SettingsStage.class){
            stage = new SettingsStage(viewport, this);
        }
        else{
            throw new IllegalArgumentException("Unknown stage class");
        }

        Gdx.input.setInputProcessor(stage);
    }

    public AudioProvider getAudioProvider() {
        return backupHandler.getAudioProvider();
    }

    public PlatformService getPlatformService() {
        return platformService;
    }

    public FakeAdService getAdService() {
        return adService;
    }

    public GameFactory getGameFactory() {
        return backupHandler.getGameFactory();
    }

    public GameViewFactory getGameViewFactory(){
        return backupHandler.getGameViewFactory();
    }

    public BillingManager getBillingManager() {
        return billingManager;
    }

    public int getScreenWidth() {
        return VIRT_SCREEN_WIDTH;
    }

    public int getScreenHeight() {
        int retVal = VIRT_SCREEN_HEIGHT;
        if(adService.isVisible()){
            retVal -= adService.getHeight();
        }
        return retVal;
    }

    public Vector2 getScreenSize(){
        return new Vector2(getScreenWidth(), getScreenHeight());
    }

    public MyAssetManager getAssetManager() {
        return backupHandler.getAssetManager();
    }

    @Override
    public void create () {
        if(platformService.getBuildConfigBuildType().equals("debug")){
            Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);
        }
        else{
            Gdx.app.setLogLevel(Gdx.app.LOG_INFO);
        }

        camera = new OrthographicCamera();
        camera.position.set(VIRT_SCREEN_WIDTH / 2, VIRT_SCREEN_HEIGHT / 2, 0);

        viewport = new FitViewport(VIRT_SCREEN_WIDTH, VIRT_SCREEN_HEIGHT, camera);
        viewport.apply();

        backupHandler = new BackupHandler();
        billingManager.connectToBillingService();

        setStage(SplashStage.class, null);
    }

    @Override
    public void pause() {
        super.pause();
        backupHandler.doBackup();
    }

    @Override
    public void resize(int width, int height){
	    viewport.update(width, height);
        camera.position.set(VIRT_SCREEN_WIDTH / 2, VIRT_SCREEN_HEIGHT / 2, 0);
    }

	@Override
	public void render () {

        Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void dispose () {
		stage.dispose();
	}


}
