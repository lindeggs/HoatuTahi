package ch.linst.hoatutahi.components.singleSelectionSupport;

/**
 * Represents the interface of a selectable object
 */
public interface Selectable {

    /**
     * Provides option to register an event listener of type ISelectedListener
     * @param listener Event listener
     */
    void addSelectedListener(SelectedListener listener);

    /**
     * Selects the object
     */
    void select();

    /**
     * Deselects the object
     */
    void deSelect();
}
