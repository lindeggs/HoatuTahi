package ch.linst.hoatutahi;

import com.badlogic.gdx.Application;
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

import ch.linst.hoatutahi.components.MyAssetManager;
import ch.linst.hoatutahi.components.audio.AudioProvider;
import ch.linst.hoatutahi.components.PlatformService;
import ch.linst.hoatutahi.components.gameBackup.BackupHandler;
import ch.linst.hoatutahi.components.gameBackup.GameFactory;
import ch.linst.hoatutahi.components.gameBackup.GameViewFactory;
import ch.linst.hoatutahi.components.playgroundModel.Playground;
import ch.linst.hoatutahi.view.stages.GameStage;
import ch.linst.hoatutahi.view.stages.HomeStage;
import ch.linst.hoatutahi.view.stages.SettingsStage;
import ch.linst.hoatutahi.view.stages.SplashStage;

public class HoatuTahi extends ApplicationAdapter {


    public static final int VIRT_SCREEN_HEIGHT = 2560;
    public static final int VIRT_SCREEN_WIDTH  = 1440;

    private final PlatformService platformService;

    private final Color clearColor = new Color(0x54C2C0FF);

	private Camera camera;
	private Viewport viewport;
	private Stage stage;

	private BackupHandler backupHandler;


    public HoatuTahi(PlatformService platformServiceArg) {
        super();
        platformService = platformServiceArg;
    }


    public void setStage(Class<?> stageClassArg, Object arg) {

        if(stage != null){
            stage.dispose();
        }

        if(SplashStage.class.isAssignableFrom(stageClassArg)){
            stage = new SplashStage(viewport, this);
        }
        else if(HomeStage.class.isAssignableFrom(stageClassArg)){
            stage = new HomeStage(viewport, this);
        }
        else if((GameStage.class.isAssignableFrom(stageClassArg)) && (arg instanceof Playground)){
            stage = new GameStage(viewport, this, (Playground)arg);
        }
        else if(SettingsStage.class.isAssignableFrom(stageClassArg)){
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

    public GameFactory getGameFactory() {
        return backupHandler.getGameFactory();
    }

    public GameViewFactory getGameViewFactory(){
        return backupHandler.getGameViewFactory();
    }

    public Vector2 getScreenSize(){
        return new Vector2(VIRT_SCREEN_WIDTH, VIRT_SCREEN_HEIGHT);
    }

    public MyAssetManager getAssetManager() {
        return backupHandler.getAssetManager();
    }

    @Override
    public void create () {
        if(platformService.getBuildConfigBuildType().equals("debug")){
            //noinspection GDXJavaLogLevel
            Gdx.app.setLogLevel(Application.LOG_DEBUG);
        }
        else{
            Gdx.app.setLogLevel(Application.LOG_ERROR);
        }

        camera = new OrthographicCamera();
        camera.position.set(VIRT_SCREEN_WIDTH / 2f, VIRT_SCREEN_HEIGHT / 2f, 0f);

        viewport = new FitViewport(VIRT_SCREEN_WIDTH, VIRT_SCREEN_HEIGHT, camera);
        viewport.apply();

        backupHandler = new BackupHandler();

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
        camera.position.set(VIRT_SCREEN_WIDTH / 2f, VIRT_SCREEN_HEIGHT / 2f, 0f);
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
