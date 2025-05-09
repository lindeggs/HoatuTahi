package ch.linst.hoatutahi.view.actors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;

public abstract class DisposableActor extends Actor implements Disposable {

    public void dispose() {
        // Override this method if the subclass Actor needs to dispose resources
    }
}
