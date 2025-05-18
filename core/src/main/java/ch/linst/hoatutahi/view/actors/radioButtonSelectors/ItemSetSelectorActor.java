package ch.linst.hoatutahi.view.actors.radioButtonSelectors;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import ch.linst.hoatutahi.HoatuTahi;
import ch.linst.hoatutahi.components.MyAssetManager;
import ch.linst.hoatutahi.view.actors.selectables.ItemSetActor;

public class ItemSetSelectorActor extends BaseSelectorActor {

    private static final int   SELECTOR_WIDTH  = HoatuTahi.VIRT_SCREEN_WIDTH;
    private static final int   SELECTOR_HEIGHT = 384;
    private static final float SELECTOR_HORIZONTAL_SPACE = 50f;

    public ItemSetSelectorActor(int itemSetSelected, MyAssetManager assetManager) {
        super(assetManager, SELECTOR_WIDTH, SELECTOR_HEIGHT, SELECTOR_HORIZONTAL_SPACE);

        ItemSetActor itemSet1 = getNewItemSetActor("ItemSet1");
        ItemSetActor itemSet2 = getNewItemSetActor("ItemSet2");

        addActor(itemSet1);
        addActor(itemSet2);

        singleSelectionHandler.addSelectable(itemSet1);
        singleSelectionHandler.addSelectable(itemSet2);
        singleSelectionHandler.setCurrentSelection(itemSetSelected);
    }

    public Vector2 getAbsoluteItemSetCenterPos(int itemSetIndexArg){
        validate(); // This causes the HorizontalGroup (parent class) to layout its children
        Actor act = this.findActor("ItemSet" + itemSetIndexArg);
        if(act == null) throw new NullPointerException("No item set with index " + itemSetIndexArg + " found!");
        return new Vector2(getX() + act.getX() + act.getWidth() / 2, getY() + act.getY() + act.getHeight() / 2);
    }

    private ItemSetActor getNewItemSetActor(String itemName){
        TextureAtlas atlas = assetManager.getAtlas(MyAssetManager.ITEM_SET_SELECTOR_ATLAS);

        TextureRegionDrawable activeItemTexReg = new TextureRegionDrawable(atlas.findRegion(itemName));
        ItemSetActor act = new ItemSetActor(assetManager, activeItemTexReg);

        act.setName(itemName);
        act.setScale(SELECTOR_HEIGHT / act.getHeight());
        act.setHeight(act.getHeight() * act.getScaleY());
        act.setWidth(act.getWidth() * act.getScaleX());

        return act;
    }
}
