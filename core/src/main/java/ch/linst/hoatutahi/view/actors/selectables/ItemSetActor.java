package ch.linst.hoatutahi.view.actors.selectables;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ch.linst.hoatutahi.components.MyAssetManager;
import ch.linst.hoatutahi.components.billing.AppStoreItem;
import ch.linst.hoatutahi.components.singleSelectionSupport.Selectable;
import ch.linst.hoatutahi.components.singleSelectionSupport.SelectedListener;
import ch.linst.hoatutahi.view.actors.PriceTagActor;

public class ItemSetActor extends BaseSelectableActor {


    public final Selectable activateable = new Selectable() {

        @Override
        public void addSelectedListener(SelectedListener listener) {
            itemActivatedListener = listener;
        }

        @Override
        public void select() {
            activate();
        }

        @Override
        public void deSelect() {
            deactivate();
        }
    };

    private SelectedListener itemActivatedListener;

    private Image itemSetImg;
    private Image itemSetLockedImg;
    private Image itemSetActiveCheckmarkImg;
    private Image itemSetLockedPadlockImg;
    private Image itemSetMaskImg;
    private Image batchMaskImg;

    private Actor priceTag;

    private boolean selected = false; // Selected means the itemSet has been selected (in order to see its items). It may still be locked and not active
    private boolean active = false; // Active means the itemSet is activated to be used.

    private AppStoreItem appStoreItem;

    public ItemSetActor(MyAssetManager assetManagerArg,
                        TextureRegionDrawable itemSetTex,
                        TextureRegionDrawable itemSetLockedTex,
                        AppStoreItem appStoreItemArg,
                        TextureRegionDrawable unlockHintDrawable) {
        //addListener(this);

        appStoreItem = appStoreItemArg;



        itemSetImg = new Image(itemSetTex);
        itemSetLockedImg = new Image(itemSetLockedTex);
        itemSetMaskImg = new Image(assetManagerArg.getAtlas(MyAssetManager.ITEM_SET_SELECTOR_ATLAS).findRegion("ItemSetMask"));
        itemSetActiveCheckmarkImg = getNewItemSetActiveCheckmarkImg(assetManagerArg);
        itemSetLockedPadlockImg = getNewItemSetLockedPadlockImg(assetManagerArg);
        batchMaskImg = getNewBatchMaskImg(assetManagerArg);
        priceTag = getNewPriceTagActor(assetManagerArg, unlockHintDrawable);

        addActor(itemSetImg);
        addActor(itemSetLockedImg);
        addActor(itemSetMaskImg);
        addActor(itemSetActiveCheckmarkImg);
        addActor(itemSetLockedPadlockImg);
        addActor(batchMaskImg);
        addActor(priceTag);

        setWidth(itemSetImg.getWidth());
        setHeight(itemSetImg.getHeight());

        refreshView();

        appStoreItem.setPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                if(propertyChangeEvent.getPropertyName().equals(AppStoreItem.PURCHASE_STATE_CHANGED_EV_ID)){
                    // Check if the item set can also be activated (this is the case if it's unlocked)
                    if(appStoreItem.isUnlocked() && itemActivatedListener != null) itemActivatedListener.handleSelected(activateable);
                }
            }
        });

    }

    private void refreshView(){
        itemSetImg.setVisible(false);
        itemSetLockedImg.setVisible(false);
        itemSetActiveCheckmarkImg.setVisible(false);
        itemSetLockedPadlockImg.setVisible(false);
        itemSetMaskImg.setVisible(false);
        batchMaskImg.setVisible(false);
        priceTag.setVisible(false);

        // Determine the visibility of the itemSetImg, itemSetLockedImg, itemSetActiveCheckmarkImg and itemSetLockedPadlockImg
        if(appStoreItem.isUnlocked()){
            itemSetImg.setVisible(true);
            if(active) itemSetActiveCheckmarkImg.setVisible(true);
        }
        else{
            itemSetLockedImg.setVisible(true);
            itemSetLockedPadlockImg.setVisible(true);
            if(selected) priceTag.setVisible(true);
        }

        // Determine when to overlay the not selected semi transparent mask
        if(!selected)
        {
            itemSetMaskImg.setVisible(true);
            if(active || !appStoreItem.isUnlocked()) batchMaskImg.setVisible(true);
        }
    }

    /**
     * Do not call this operation directly. Its called by the activateable Selectable objects (private class)
     */
    private void activate(){
        active = true;
        refreshView();
    }

    /**
     * Do not call this operation directly. Its called by the activateable Selectable objects (private class)
     */
    private void deactivate(){
        active = false;
        refreshView();
    }

    /**
     * Do not call this operation directly. Its called by the attached SingleSelectionHandler class
     */
    @Override
    public void select() {
        selected = true;

        // Check if the item set can also be activated (this is the case if it's unlocked)
        if(appStoreItem.isUnlocked() && itemActivatedListener != null) itemActivatedListener.handleSelected(activateable);

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

    private Image getNewItemSetActiveCheckmarkImg(MyAssetManager assetManagerArg){
        Image img = new Image(assetManagerArg.getAtlas(MyAssetManager.ITEM_SET_SELECTOR_ATLAS).findRegion("Checkmark"));
        img.setX(itemSetImg.getWidth() - img.getWidth());
        img.setY(itemSetImg.getHeight() - 60f);
        return img;
    }

    private Image getNewItemSetLockedPadlockImg(MyAssetManager assetManagerArg){
        Image img = new Image(assetManagerArg.getAtlas(MyAssetManager.ITEM_SET_SELECTOR_ATLAS).findRegion("PadLock"));
        img.setX(itemSetImg.getWidth() - img.getWidth());
        img.setY(itemSetImg.getHeight() - 60f);
        return img;
    }

    private Image getNewBatchMaskImg(MyAssetManager assetManagerArg){
        Image img = new Image(assetManagerArg.getAtlas(MyAssetManager.ITEM_SET_SELECTOR_ATLAS).findRegion("BatchMask"));
        img.setX(itemSetImg.getWidth() - img.getWidth());
        img.setY(itemSetImg.getHeight() - 60f);
        return img;
    }

    private Actor getNewPriceTagActor(MyAssetManager assetManagerArg, TextureRegionDrawable unlockHintDrawableArg){
        Actor act = new PriceTagActor(assetManagerArg, unlockHintDrawableArg, appStoreItem);
        act.setY(-act.getHeight() + 60f);
        return act;
    }

}
