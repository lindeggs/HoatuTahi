package ch.linst.hoatutahi.view.actors.radioButtonSelectors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.ArrayList;

import ch.linst.hoatutahi.HoatuTahi;
import ch.linst.hoatutahi.components.ItemSetContainer;
import ch.linst.hoatutahi.components.MyAssetManager;
import ch.linst.hoatutahi.view.actors.selectables.GlassGlobeActor;

public class LevelSelectorActor extends BaseSelectorActor {

    private static final String LVL1_SEL_ACT_NAME = "lvl1SelectorActor";
    private static final String LVL2_SEL_ACT_NAME = "lvl2SelectorActor";
    private static final String LVL3_SEL_ACT_NAME = "lvl3SelectorActor";

    private static final int   SELECTOR_WIDTH  = HoatuTahi.VIRT_SCREEN_WIDTH;
    private static final int   SELECTOR_HEIGHT = 350;
    private static final float SELECTOR_HORIZONTAL_SPACE = 150f;


    public LevelSelectorActor(int levelSelected, ItemSetContainer itemSetContainerArg, MyAssetManager assetManagerArg) {
        super(assetManagerArg, SELECTOR_WIDTH, SELECTOR_HEIGHT, SELECTOR_HORIZONTAL_SPACE);

        addActor(getNewLvlSelector(LVL1_SEL_ACT_NAME, itemSetContainerArg, true, 1));
        addActor(getNewLvlSelector(LVL2_SEL_ACT_NAME, itemSetContainerArg, true, 2));
        addActor(getNewLvlSelector(LVL3_SEL_ACT_NAME, itemSetContainerArg, false, 3));

        singleSelectionHandler.addSelectable(findActor(LVL1_SEL_ACT_NAME));
        singleSelectionHandler.addSelectable(findActor(LVL2_SEL_ACT_NAME));
        singleSelectionHandler.addSelectable(findActor(LVL3_SEL_ACT_NAME));
        singleSelectionHandler.setCurrentSelection(levelSelected);
    }

    private GlassGlobeActor getNewLvlSelector(String actorName, ItemSetContainer itemSetContainerArg, boolean switchOn, int level) {
        GlassGlobeActor act = new GlassGlobeActor(itemSetContainerArg, assetManager);
        act.setName(actorName);
        act.setScale(SELECTOR_HEIGHT / act.getHeight());
        act.setHeight(act.getHeight() * act.getScaleY());
        act.setWidth(act.getWidth() * act.getScaleX());
        if(switchOn) act.switchOn();
        if(level == 1){
            act.showItem(1);
        } else if(level == 2){
            act.showItem(0);
        }
        return act;
    }

}
