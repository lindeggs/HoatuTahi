package ch.linst.hoatutahi.view.actors.radioButtonSelectors;

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.utils.Align;

import java.beans.PropertyChangeListener;

import ch.linst.hoatutahi.components.MyAssetManager;
import ch.linst.hoatutahi.components.singleSelectionSupport.SingleSelectionHandler;

public abstract class BaseSelectorActor extends HorizontalGroup {

    protected MyAssetManager assetManager;
    protected SingleSelectionHandler singleSelectionHandler = new SingleSelectionHandler();

    protected BaseSelectorActor(MyAssetManager assetManagerArg, float widthArg, float heightArg, float hSpace) {
        this.assetManager = assetManagerArg;
        setWidth(widthArg);
        setHeight(heightArg);
        align(Align.center);
        space(hSpace);
    }

    public void addSelectionChangedListener(PropertyChangeListener listener){
        singleSelectionHandler.addPropertyChangeListener(listener);
    }
}
