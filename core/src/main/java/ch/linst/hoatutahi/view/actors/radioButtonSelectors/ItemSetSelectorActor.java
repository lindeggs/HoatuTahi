package ch.linst.hoatutahi.view.actors.radioButtonSelectors;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.beans.PropertyChangeListener;

import ch.linst.hoatutahi.HoatuTahi;
import ch.linst.hoatutahi.components.MyAssetManager;
import ch.linst.hoatutahi.components.billing.AppStoreItem;
import ch.linst.hoatutahi.components.billing.BillingManager;
import ch.linst.hoatutahi.components.billing.service.ProductId;
import ch.linst.hoatutahi.components.singleSelectionSupport.SingleSelectionHandler;
import ch.linst.hoatutahi.view.actors.selectables.ItemSetActor;

public class ItemSetSelectorActor extends BaseSelectorActor {

    private static final int   SELECTOR_WIDTH  = HoatuTahi.VIRT_SCREEN_WIDTH;
    private static final int   SELECTOR_HEIGHT = 384;
    private static final float SELECTOR_HORIZONTAL_SPACE = 50f;

    private SingleSelectionHandler itemsetActivationHandler = new SingleSelectionHandler();

    public ItemSetSelectorActor(int itemSetSelected, MyAssetManager assetManager, BillingManager billingManager) {
        super(assetManager, SELECTOR_WIDTH, SELECTOR_HEIGHT, SELECTOR_HORIZONTAL_SPACE);

        ItemSetActor itemSet1 = getNewItemSetActor("ItemSet1", billingManager.getAppStoreItem(ProductId.PLAYGROUND_ITEMSET_0_BALLS));
        ItemSetActor itemSet2 = getNewItemSetActor("ItemSet2", billingManager.getAppStoreItem(ProductId.PLAYGROUND_ITEMSET_1_NUMBERS_2048));

        addActor(itemSet1);
        addActor(itemSet2);

        singleSelectionHandler.addSelectable(itemSet1);
        singleSelectionHandler.addSelectable(itemSet2);
        singleSelectionHandler.setCurrentSelection(itemSetSelected);

        itemsetActivationHandler.addSelectable(itemSet1.activateable);
        itemsetActivationHandler.addSelectable(itemSet2.activateable);
        itemsetActivationHandler.setCurrentSelection(itemSetSelected); // When creating the ItemSetSelectorActor, the selected itemSet is always also the activated one
    }

    public Vector2 getAbsoluteItemSetCenterPos(int itemSetIndexArg){
        validate(); // This causes the HorizontalGroup (parent class) to layout its children
        Actor act = this.findActor("ItemSet" + itemSetIndexArg);
        if(act == null) throw new NullPointerException("No item set with index " + itemSetIndexArg + " found!");
        return new Vector2(getX() + act.getX() + act.getWidth() / 2, getY() + act.getY() + act.getHeight() / 2);
    }

    private ItemSetActor getNewItemSetActor(String itemName, AppStoreItem appStoreItem){
        TextureAtlas atlas = assetManager.getAtlas(MyAssetManager.ITEM_SET_SELECTOR_ATLAS);

        TextureRegionDrawable activeItemTexReg = new TextureRegionDrawable(atlas.findRegion(itemName));
        TextureRegionDrawable lockedItemTexReg = (itemName != "ItemSet1")?
                new TextureRegionDrawable(atlas.findRegion(itemName + "Locked")): activeItemTexReg;

        ItemSetActor act = new ItemSetActor(assetManager, activeItemTexReg, lockedItemTexReg, appStoreItem,
                (itemName != "ItemSet1")? new TextureRegionDrawable(atlas.findRegion(itemName + "UnlockHint")) : null);

        act.setName(itemName);
        act.setScale(SELECTOR_HEIGHT / act.getHeight());
        act.setHeight(act.getHeight() * act.getScaleY());
        act.setWidth(act.getWidth() * act.getScaleX());

        return act;
    }

    public void addActivationChangedListener(PropertyChangeListener listener){
        itemsetActivationHandler.addPropertyChangeListener(listener);
    }

}
