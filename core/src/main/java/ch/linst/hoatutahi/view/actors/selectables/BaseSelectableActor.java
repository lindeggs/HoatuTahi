package ch.linst.hoatutahi.view.actors.selectables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import ch.linst.hoatutahi.components.singleSelectionSupport.Selectable;
import ch.linst.hoatutahi.components.singleSelectionSupport.SelectedListener;
import ch.linst.hoatutahi.view.actors.buttons.MyClickableGroup;

public abstract class BaseSelectableActor extends MyClickableGroup implements Selectable {

    private SelectedListener selectedListener = null;


    @Override
    protected void onTouchUpEvent() {
        Gdx.app.debug("HoatuTahi-core", "BaseSelectableActor clicked");
        if(selectedListener != null) selectedListener.handleSelected(this);
    }


    @Override
    public void addSelectedListener(SelectedListener listener) {
        selectedListener = listener;
    }
}
