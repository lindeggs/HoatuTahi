package ch.linst.hoatutahi.view.stages;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.Viewport;

import ch.linst.hoatutahi.HoatuTahi;

public class SplashStage extends Stage {

    private HoatuTahi hoatuTahi;

    public SplashStage(Viewport viewport, HoatuTahi hoatuTahiArg) {
        super(viewport);
        hoatuTahi = hoatuTahiArg;
        hoatuTahi.getAssetManager().loadAllOpenGlAssets();

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                hoatuTahi.getAssetManager().finishLoading();
                hoatuTahi.setStage(HomeStage.class, hoatuTahi);
            }
        }, 0.2f);
    }
}
