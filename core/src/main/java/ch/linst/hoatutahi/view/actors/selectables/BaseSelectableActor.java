package ch.linst.hoatutahi.view.actors.selectables;

import ch.linst.hoatutahi.components.singleSelectionSupport.Selectable;
import ch.linst.hoatutahi.components.singleSelectionSupport.SelectedListener;
import ch.linst.hoatutahi.view.actors.buttons.MyClickableGroup;

public abstract class BaseSelectableActor extends MyClickableGroup implements Selectable {

    private SelectedListener selectedListener = null;


    @Override
    protected void onTouchUpEvent() {
        if(selectedListener != null) selectedListener.handleSelected(this);
    }

    @Override
    public void addSelectedListener(SelectedListener listener) {
        selectedListener = listener;
    }
}
