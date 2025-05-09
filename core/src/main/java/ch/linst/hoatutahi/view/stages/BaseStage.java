package ch.linst.hoatutahi.view.stages;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

import ch.linst.hoatutahi.HoatuTahi;
import ch.linst.hoatutahi.components.MyAssetManager;

public abstract class BaseStage extends Stage {

    protected HoatuTahi hoatuTahi;
    protected MyAssetManager assetManager;


    protected BaseStage(Viewport viewportArg, HoatuTahi hoatuTahiArg){
        super(viewportArg);
        hoatuTahi = hoatuTahiArg;
        assetManager = hoatuTahiArg.getAssetManager();
    }

}
