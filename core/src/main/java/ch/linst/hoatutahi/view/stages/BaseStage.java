package ch.linst.hoatutahi.view.stages;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.jetbrains.annotations.NotNull;

import ch.linst.hoatutahi.HoatuTahi;
import ch.linst.hoatutahi.components.MyAssetManager;

public abstract class BaseStage extends Stage {

    protected final HoatuTahi hoatuTahi;
    protected final MyAssetManager assetManager;


    protected BaseStage(@NotNull Viewport viewportArg, @NotNull HoatuTahi hoatuTahiArg){
        super(viewportArg);
        hoatuTahi = hoatuTahiArg;
        assetManager = hoatuTahiArg.getAssetManager();
    }

}
