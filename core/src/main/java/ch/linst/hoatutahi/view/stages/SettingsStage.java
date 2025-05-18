package ch.linst.hoatutahi.view.stages;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ch.linst.hoatutahi.HoatuTahi;
import ch.linst.hoatutahi.components.ItemSetContainer;
import ch.linst.hoatutahi.components.MyAssetManager;
import ch.linst.hoatutahi.view.actors.ItemSetPresentationActor;
import ch.linst.hoatutahi.view.actors.radioButtonSelectors.ItemSetSelectorActor;

public class SettingsStage extends BaseStage {

    public SettingsStage(Viewport viewportArg, HoatuTahi hoatuTahiArg) {
        super(viewportArg, hoatuTahiArg);

        ItemSetSelectorActor itemSetSelectorActor = getNewItemSetSelectorActor();

        addActor(getNewExitBtnActor());
        addActor(getNewItemSetPresentationActor(0, itemSetSelectorActor));
        addActor(getNewItemSetPresentationActor(1, itemSetSelectorActor));
        addActor(itemSetSelectorActor);

        setVisibleItemSetActor(hoatuTahi.getGameViewFactory().getItemSetSelected());

    }

    private Actor getNewExitBtnActor() {
        TextureRegionDrawable trd1 = new TextureRegionDrawable(assetManager.getAtlas(MyAssetManager.BUTTON_ATLAS).findRegion("Exit"));
        TextureRegionDrawable trd2 = new TextureRegionDrawable(assetManager.getAtlas(MyAssetManager.BUTTON_ATLAS).findRegion("ExitActive"));
        Button btn = new Button(trd1, trd2);

        btn.setPosition(40f, hoatuTahi.getScreenHeight() - btn.getHeight() - 20f);

        btn.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hoatuTahi.setStage(HomeStage.class, null);
            }
        });

        return btn;
    }


    private Actor getNewItemSetPresentationActor(int itemSetIndex, ItemSetSelectorActor itemSetSelectorActorArg){
        int highestVisibleItem = hoatuTahi.getGameFactory().getHighestItemUnveiled(itemSetIndex);
        String actName = MyAssetManager.ITEM_SET_ATLAS[itemSetIndex];
        ItemSetContainer itemSetContainer = assetManager.getItemSetContainer(itemSetIndex);

        Vector2 itemsStartPosition = itemSetSelectorActorArg.getAbsoluteItemSetCenterPos(itemSetIndex + 1); // TODO: Missmatch of index start should be resolved

        //Actor act = new ItemSetPresentationActor_OLD(itemSetContainer, highestVisibleItem);
        Actor act = new ItemSetPresentationActor(itemSetContainer, highestVisibleItem, hoatuTahi.getScreenSize(), itemsStartPosition);
        act.setName(actName);
        act.setPosition(0, 0);

        return act;
    }

    private void setVisibleItemSetActor(int itemSetIndex){
        for(int i = 0; i < MyAssetManager.ITEM_SET_COUNT; i++){
            Actor act = getRoot().findActor(MyAssetManager.ITEM_SET_ATLAS[i]);
            ((ItemSetPresentationActor)act).showItems((itemSetIndex == i)? true: false);
        }
    }

    private ItemSetSelectorActor getNewItemSetSelectorActor(){
        int selectedItemSet = hoatuTahi.getGameViewFactory().getItemSetSelected();

        ItemSetSelectorActor act = new ItemSetSelectorActor(selectedItemSet, assetManager);
        act.setPosition(0, hoatuTahi.getScreenHeight() - act.getHeight() - 20f);

        // Register handler which is called if the itemSet selection as changed
        act.addSelectionChangedListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                if((propertyChangeEvent.getNewValue() != null) && (propertyChangeEvent.getNewValue() instanceof Integer)){
                    int selectedItemSet = (Integer) propertyChangeEvent.getNewValue();
                    setVisibleItemSetActor(selectedItemSet);
                    hoatuTahi.getGameViewFactory().setItemSetSelected(selectedItemSet);
                }
            }
        });

        return act;
    }

}
