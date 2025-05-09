package ch.linst.hoatutahi.view.actors.animatedSelectors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import ch.linst.hoatutahi.HoatuTahi;
import ch.linst.hoatutahi.components.MyAssetManager;

public abstract class SelectorActor extends Group {

    private enum Direction{
        LEFT,
        RIGHT
    }

    public static final String SELECTION_CHANGED_EV_ID = "SelectionChanged";

    protected MyAssetManager assetManager;

    private static final int SELECTOR_WIDTH  = HoatuTahi.VIRT_SCREEN_WIDTH - 200;
    private static final int SELECTOR_HEIGHT = 400;

    private static final float LEFT_OUTSIDE_POS  = - 10 - ((HoatuTahi.VIRT_SCREEN_WIDTH - SELECTOR_WIDTH) / 2);
    private static final float RIGHT_OUTSIDE_POS = 10 + HoatuTahi.VIRT_SCREEN_WIDTH - ((HoatuTahi.VIRT_SCREEN_WIDTH - SELECTOR_WIDTH) / 2);

    private static final float SWITCH_DURATION = 0.15f;

    private static final String BTN_LEFT_NAME  = "btnLeft";
    private static final String BTN_RIGHT_NAME = "btnRight";

    private PropertyChangeSupport pcs;

    private ArrayList<Actor> selectorItems = new ArrayList<>();
    private Integer selectedIndex = 0;

    protected SelectorActor(MyAssetManager assetManagerArg) {
        super();
        assetManager = assetManagerArg;
        pcs = new PropertyChangeSupport(this);

        setWidth(SELECTOR_WIDTH);
        setHeight(SELECTOR_HEIGHT);

        addActor(getNewBtnLeft());
        addActor(getNewBtnRight());
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void addSelectorItem(Actor item){

        float aspectRatio = item.getHeight() / item.getWidth();

        item.setTouchable(Touchable.disabled);
        item.setHeight(getHeight());
        item.setWidth(item.getHeight() / aspectRatio);

        selectorItems.add(item);

        if(selectorItems.size() == 1){
            item.setPosition((getWidth() - item.getWidth()) / 2, getHeight() - item.getHeight());
        }
        else{
            item.setPosition(LEFT_OUTSIDE_POS - item.getWidth(), getHeight() - item.getHeight());
            this.findActor(BTN_RIGHT_NAME).setVisible(true);
        }

        addActorAt(0, item);
    }

    public Integer getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int indexArg){

        if((indexArg < 0) || (indexArg >= selectorItems.size()))
            throw new IllegalArgumentException("indexArg is out of bounds");

        Actor currentItem  = selectorItems.get(selectedIndex);
        Actor selectedItem = selectorItems.get(indexArg);
        selectedIndex = indexArg;

        currentItem.setX(LEFT_OUTSIDE_POS - currentItem.getWidth());
        selectedItem.setX((getWidth() - selectedItem.getWidth()) / 2);
        setButtonVisibility();
    }

    private void setButtonVisibility(){
        this.findActor(BTN_LEFT_NAME).setVisible(
                (selectedIndex > 0) ? true : false
        );
        this.findActor(BTN_RIGHT_NAME).setVisible(
                (selectedIndex < (selectorItems.size() - 1)) ? true : false
        );
    }

    private void goToNextIndex(){
        if(selectedIndex < (selectorItems.size() - 1)){
            Actor currentItem = selectorItems.get(selectedIndex);
            Actor nextItem    = selectorItems.get(selectedIndex + 1);

            moveItemOut(currentItem, Direction.LEFT);
            moveItemIn(nextItem, Direction.LEFT);

            selectedIndex++;
            setButtonVisibility();
            pcs.firePropertyChange(SELECTION_CHANGED_EV_ID, (Integer)(selectedIndex - 1), selectedIndex);
        }
    }


    private void goToPreviousIndex(){
        if(selectedIndex > 0){
            Actor currentItem = selectorItems.get(selectedIndex);
            Actor prevItem    = selectorItems.get(selectedIndex -1);

            moveItemOut(currentItem, Direction.RIGHT);
            moveItemIn(prevItem, Direction.RIGHT);

            selectedIndex--;
            setButtonVisibility();
            pcs.firePropertyChange(SELECTION_CHANGED_EV_ID, (Integer)(selectedIndex + 1), selectedIndex);
        }
    }

    private Button getNewBtnRight() {
        Button btn = getNewButton("RightArrow", "RightArrowActive");
        float aspectRatio = btn.getHeight() / btn.getWidth();
        btn.setName(BTN_RIGHT_NAME);
        btn.setHeight(getHeight() / 1.2f);
        btn.setWidth(btn.getHeight() / aspectRatio);
        btn.setPosition(getWidth() - btn.getWidth(), (getHeight() / 2) - (btn.getHeight() / 2));
        btn.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goToNextIndex();
            }
        });
        return btn;
    }

    private Button getNewBtnLeft() {
        Button btn = getNewButton("LeftArrow", "LeftArrowActive");
        float aspectRatio = btn.getHeight() / btn.getWidth();
        btn.setName(BTN_LEFT_NAME);
        btn.setHeight(getHeight() / 1.2f);
        btn.setWidth(btn.getHeight() / aspectRatio);
        btn.setPosition(0, (getHeight() / 2) - (btn.getHeight() / 2));
        btn.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goToPreviousIndex();
            }
        });
        return btn;
    }

    private Button getNewButton(String atlasRegion1, String atlasRegion2){
        TextureRegion tex1 = assetManager.getAtlas(MyAssetManager.ARROW_ATLAS).findRegion(atlasRegion1);
        TextureRegion tex2 = assetManager.getAtlas(MyAssetManager.ARROW_ATLAS).findRegion(atlasRegion2);

        Button btn = new Button(
                new TextureRegionDrawable(tex1),
                new TextureRegionDrawable(new TextureRegion(tex2))
        );

        btn.setVisible(false); // Button is not yet visible because no selector icon has been added so far

        return btn;
    }

    private void moveItemOut(Actor item, Direction direction) {

        float endPosX = (direction == Direction.LEFT)? LEFT_OUTSIDE_POS - item.getWidth() : RIGHT_OUTSIDE_POS;

        MoveToAction mta = new MoveToAction();
        mta.setPosition(endPosX, item.getY());
        mta.setDuration(SWITCH_DURATION);
        item.addAction(mta);
    }

    private void moveItemIn(Actor item, Direction direction){

        float startPosX = (direction == Direction.LEFT)? RIGHT_OUTSIDE_POS : LEFT_OUTSIDE_POS - item.getWidth();
        float endPosX = (getWidth() - item.getWidth()) / 2;

        item.setX(startPosX);
        MoveToAction mta = new MoveToAction();
        mta.setPosition(endPosX, item.getY());
        mta.setDuration(SWITCH_DURATION);
        item.addAction(mta);
    }
}
