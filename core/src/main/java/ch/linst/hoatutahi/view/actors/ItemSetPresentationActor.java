package ch.linst.hoatutahi.view.actors;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Random;

import ch.linst.hoatutahi.components.ItemSetContainer;
import ch.linst.hoatutahi.view.actions.EndlessSpringMoveToAction;

public class ItemSetPresentationActor extends Group {

    private final Array<TextureAtlas.AtlasRegion> items;
    private final Array<TextureAtlas.AtlasRegion> hiddenItems;
    private final ArrayList<Image> itemImages;
    private final Vector2 itemsStartPos;
    private final Random rand = new Random();

    // This actor is intended to use up the whole screen space
    public ItemSetPresentationActor(ItemSetContainer itemSetContainerArg, int highestVisibleItemArg, Vector2 screenSizeArg, Vector2 itemsStartPosArg) {

        if((highestVisibleItemArg < 6) || (highestVisibleItemArg > 16)){
            throw new IllegalArgumentException("highestVisibleItemArg is out of bounds");
        }
        items = itemSetContainerArg.itemTexAtlas.findRegions("Item");
        hiddenItems = itemSetContainerArg.itemTexAtlas.findRegions("HItem");
        itemsStartPos = itemsStartPosArg;

        setSize(screenSizeArg.x, screenSizeArg.y);
        setTouchable(Touchable.disabled);

        itemImages = prepareAllItemImages(itemSetContainerArg.itemSetPresentationBaseScale, highestVisibleItemArg);
        for(Image img: itemImages) addActor(img);
    }

    public void showItems(boolean doShow){

        final int sideMargin = 200;
        final int bottomMargin = 400;

        for(int row = 0; row < 4; row++){
            for(int col = 0; col < 4; col++){
                Image img = itemImages.get((row * 4) + col);
                Action act = img.getActions().first();
                if(act instanceof EndlessSpringMoveToAction){
                    EndlessSpringMoveToAction springACtion = (EndlessSpringMoveToAction)act;
                    float xPos, yPos;
                    if(doShow){
                        float space = (getWidth() - 2 * sideMargin) / 3;
                        xPos = sideMargin + col * space;
                        yPos = bottomMargin + (3 - row) * space;
                        springACtion.setVelocity(rand.nextInt(20000) - 10000, rand.nextInt(20000) - 10000);
                    }
                    else{
                        xPos = itemsStartPos.x;
                        yPos = itemsStartPos.y;
                    }

                    springACtion.setEndPosition(xPos - img.getWidth() / 2, yPos - img.getHeight() / 2);
                }
            }
        }
    }

    private ArrayList<Image> prepareAllItemImages(float scaleArg, int highestVisibleItemArg){
        ArrayList<Image> images = new ArrayList<>();

        for(int i = 0; i < items.size; i++){
            Image img;
            if(i < highestVisibleItemArg){
                img = new Image(items.get(i));
            }
            else{
                img = new Image(hiddenItems.get(i - 6)); // The lowest six items are not covered in the HItem asset
            }
            images.add(img);

            img.setTouchable(Touchable.disabled);
            img.setX(itemsStartPos.x - img.getWidth() / 2);
            img.setY(itemsStartPos.y - img.getHeight() / 2);
            img.setOrigin(Align.center);
            img.setScale(scaleArg);

            img.addAction(new EndlessSpringMoveToAction(new Vector2(itemsStartPos.x - img.getWidth() / 2, itemsStartPos.y - img.getHeight() / 2)));
        }

        return images;
    }


}
