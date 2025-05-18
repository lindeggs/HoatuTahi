package ch.linst.hoatutahi.view.actors.selectables;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import ch.linst.hoatutahi.components.MyAssetManager;
import ch.linst.hoatutahi.components.singleSelectionSupport.Selectable;
import ch.linst.hoatutahi.components.singleSelectionSupport.SelectedListener;

public class ItemSetActor extends BaseSelectableActor {


    public final Selectable activateable = new Selectable() {

        @Override
        public void addSelectedListener(SelectedListener listener) {
            itemActivatedListener = listener;
        }

        @Override
        public void select() {
            selected = true;
            refreshView();
        }

        @Override
        public void deSelect() {
            selected = false;
            refreshView();
        }
    };

    private SelectedListener itemActivatedListener;

    private final Image itemSetImg;
    private final Image itemSetMaskImg;

    private boolean selected = false; // Selected means the itemSet has been selected (in order to see its items). It may still be locked and not active

    public ItemSetActor(MyAssetManager assetManagerArg,
                        TextureRegionDrawable itemSetTex) {
        //addListener(this);

        itemSetImg = new Image(itemSetTex);
        itemSetMaskImg = new Image(assetManagerArg.getAtlas(MyAssetManager.ITEM_SET_SELECTOR_ATLAS).findRegion("ItemSetMask"));

        addActor(itemSetImg);
        addActor(itemSetMaskImg);

        setWidth(itemSetImg.getWidth());
        setHeight(itemSetImg.getHeight());

        refreshView();
    }

    private void refreshView(){
        itemSetImg.setVisible(true);
        itemSetMaskImg.setVisible(!selected);
    }

    /**
     * Do not call this operation directly. Its called by the attached SingleSelectionHandler class
     */
    @Override
    public void select() {
        selected = true;

        // Check if the item set can also be activated (this is the case if it's unlocked)
        if(itemActivatedListener != null) itemActivatedListener.handleSelected(activateable);

        refreshView();
    }

    /**
     * Do not call this operation directly. Its called by the attached SingleSelectionHandler class
     */
    @Override
    public void deSelect() {
        selected = false;
        refreshView();
    }
}
