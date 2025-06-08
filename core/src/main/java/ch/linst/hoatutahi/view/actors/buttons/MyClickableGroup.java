package ch.linst.hoatutahi.view.actors.buttons;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;

/**
 * Group which implements an event listener and catches touchUp events
 * Annotation: Don't forget to set the size of instances deriving from this class
 */
public abstract class MyClickableGroup extends Group implements EventListener {

    protected MyClickableGroup(){
        addListener(this);
        setTouchable(Touchable.enabled);
    }

    @Override
    public boolean handle(Event event) {
        if(!(event instanceof InputEvent)) return false;

        InputEvent iEv = (InputEvent)event;
        //Gdx.app.debug("Hoatutahi", "event type: " + iEv.getType());

        // Return if the callback comes from a second of third touch object
        if((iEv.getPointer() != 0)) return false;

        if(iEv.isHandled()) return true;

        if(iEv.getType() == InputEvent.Type.touchDown)
        {
            // Below line is required to capture touchUp events after touchDown
            iEv.getStage().addTouchFocus(this, iEv.getListenerActor(), iEv.getTarget(), iEv.getPointer(), iEv.getButton());
        }
        else if(iEv.getType() == InputEvent.Type.touchUp)
        {
            onTouchUpEvent();
        }

        return true;
    }

    protected abstract void onTouchUpEvent();
}
