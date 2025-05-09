package ch.linst.hoatutahi.view.actors;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

import ch.linst.hoatutahi.components.ItemSetContainer;

public class ItemSetPresentationActor_OLD extends Group {

    private static final float SET_WIDTH  = 1000f;
    private static final float SET_HEIGHT = 600f;
    private static final float ITEM_PADDING = 20;

    private int highestVisibleItem; // Starting with index 1, up to 16
    private float item_scale; // Scaling factor for the displayed items

    private Array<TextureAtlas.AtlasRegion> items;
    private Array<TextureAtlas.AtlasRegion> hiddenItems;

    public ItemSetPresentationActor_OLD(ItemSetContainer itemSetContainerArg, int highestVisibleItemArg) {

        if((highestVisibleItemArg < 6) || (highestVisibleItemArg > 16)){
            throw new IllegalArgumentException("highestVisibleItemArg is out of bounds");
        }
        highestVisibleItem = highestVisibleItemArg;
        items = itemSetContainerArg.itemTexAtlas.findRegions("Item");
        hiddenItems = itemSetContainerArg.itemTexAtlas.findRegions("HItem");

        item_scale = itemSetContainerArg.itemSetPresentationBaseScale;

        for(Image img: prepareAllItemImages()) addActor(img);
    }

    private ArrayList<Image> prepareAllItemImages(){
        ArrayList<Image> images = getNewImageArray();
        for(Image img: images){
            img.setTouchable(Touchable.disabled);
            img.setOrigin(Align.center);
            img.setScale(item_scale);
        }
        setItemsPosition(images);

        return images;
    }

    private ArrayList<Image> getNewImageArray(){
        ArrayList<Image> images = new ArrayList<>();

        for(int i = 0; i < items.size; i++){
            if(i < highestVisibleItem){
                images.add(new Image(items.get(i)));
            }
            else{
                images.add(new Image(hiddenItems.get(i - 6))); // The lowest six items are not covered in the HItem asset
            }
        }
        return images;
    }

    private void setItemsPosition(ArrayList<Image> images){
        float angle = 60; // The start angle
        float dAngle = 30;

        Vector2 posXY = calcPosXY(angle);

        images.get(0).setPosition(posXY.x, posXY.y, Align.center);

        for(int i = 1; i < images.size(); i++){
            float targetDistance = ITEM_PADDING + item_scale * (images.get(i-1).getWidth()/2 + images.get(i).getWidth()/2);

            Vector2 newPosXY;
            float distance;
            int loopLimit = 10;
            do{
                loopLimit--;
                newPosXY = calcPosXY(angle + dAngle);
                distance = newPosXY.dst(posXY);
                dAngle *= targetDistance / distance;
            }while ((Math.abs(targetDistance - distance) > 3) && (loopLimit > 0));
            angle += dAngle;
            posXY = calcPosXY(angle);
            images.get(i).setPosition(posXY.x, posXY.y, Align.center);
        }
    }

    private Vector2 calcPosXY(float angle){
        Vector2 posXY = new Vector2();

        float width = SET_WIDTH / 600 * (angle + 75);
        float height = SET_HEIGHT / 600 * (angle + 75);

        posXY.x = -width / 2f * (float)Math.cos(Math.toRadians(angle));
        posXY.x += SET_WIDTH / 2f;

        posXY.y = height / 2f * (float)Math.sin(Math.toRadians(angle));
        posXY.y += SET_HEIGHT / 2f;

        return posXY;
    }
}
