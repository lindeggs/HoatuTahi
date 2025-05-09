package ch.linst.hoatutahi.components.singleSelectionSupport;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class SingleSelectionHandler implements SelectedListener {

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private List<Selectable> selectables = new ArrayList<Selectable>();
    private int currentSelection = -1;


    public SingleSelectionHandler() {
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void addSelectable(Selectable selectable){
        selectables.add(selectable);
        selectable.addSelectedListener(this);
        if(currentSelection == -1){
            handleSelected(selectable);
        }
    }

    @Override
    public void handleSelected(Selectable subject) {

        for(int index = 0; index < selectables.size(); index++){
            Selectable sel = selectables.get(index);
            if(subject.hashCode() == sel.hashCode()){
                sel.select();
                if(currentSelection != index) {
                    int oldIndex = currentSelection;
                    currentSelection = index;
                    pcs.firePropertyChange("currentSelection", oldIndex, currentSelection);
                }
            }
            else{
                sel.deSelect();
            }
        }
    }

    public int getCurrentSelection() {
        return currentSelection;
    }

    public void setCurrentSelection(int newSelection) {
        if((newSelection < 0) || (newSelection >= selectables.size())){
            throw new IllegalArgumentException("No such element to select");
        }

        Selectable sel = selectables.get(newSelection);
        handleSelected(sel);
    }
}
