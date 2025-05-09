package ch.linst.hoatutahi.view.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import java.util.ArrayList;


import ch.linst.hoatutahi.components.ItemSetContainer;
import ch.linst.hoatutahi.components.MyAssetManager;
import ch.linst.hoatutahi.components.playgroundModel.Playground;
import ch.linst.hoatutahi.components.playgroundModel.PlaygroundItem;

public class PlaygroundActor extends Group {

    Playground playground;
    int gameLevel;

    private MyAssetManager assetManager;
    private ItemSetContainer itemSetContainer;

    private ArrayList<TextureRegionDrawable> itemTexDrawables;

    private Image backgroundImages[][];
    private Image itemImages[][];
    private Image nextItemImage;

    private ParticleEffectActor nextItemEffect;

    private boolean backgroundImagesInitialized = false;

    private Vector2 vector_getImgViewPosXY = new Vector2();


    public PlaygroundActor(Playground playgroundArg, ItemSetContainer itemSetContainerArg, int gameLevelArg, MyAssetManager assetManagerArg) {
        super();

        setTouchable(Touchable.disabled);

        gameLevel = gameLevelArg;
        assetManager = assetManagerArg;

        itemSetContainer = itemSetContainerArg;
        playground = playgroundArg;
        itemTexDrawables = getNewItemTexDrawables();

        backgroundImages = getNewBackgroundImages();
        addActor2dArray(backgroundImages);

        itemImages = getNewItemImages();
        addActor2dArray(itemImages);

        nextItemImage = getNewNextItemImage();
        addActor(nextItemImage);

        nextItemEffect = getNewNextItemEffect();
        addActor(nextItemEffect);

        // Default the size and aspect ratio of the actor
        // (the size can be overwritten any time later on)
        setSize(400, 400 / playgroundArg.getSizeX() * playgroundArg.getSizeY());
    }


    public void updateGroup(){
        updateGroup(0f, false);
    }


    /**
     * Updates all actors (images) of the whole group. This includes the backgroundImages and the itemImages
     * Update backgroundImages means: Initialization of size and position (only one single time)
     * Update itemImages means: Update visibility, texture, size and position
     */
    public void updateGroup(float sliderDeflection, boolean showNextNewPgItem){

        // Init the background images (this is required only once)
        /*
        if(!backgroundImagesInitialized){
            initBackgroundImages();
            backgroundImagesInitialized = true;
        }
        */

        updateCurrentPlaygroundItems(sliderDeflection);
        updateNextItemImage(showNextNewPgItem);
        updateNextItemEffect(showNextNewPgItem);
    }

    public void setPlayground(Playground playground) {
        this.playground = playground;
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        initBackgroundImages();
        updateGroup();
        initAllItemsSize();
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        initBackgroundImages();
        updateGroup();
        initAllItemsSize();
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        initBackgroundImages();
        updateGroup();
        initAllItemsSize();
    }

    private void updateCurrentPlaygroundItems(float sliderDeflection) {
        PlaygroundItem pgModelItem[][] = playground.getCurrentItems();
        for(int y = 0; y < pgModelItem.length; y++){
            for(int x = 0; x < pgModelItem[y].length; x++){
                    PlaygroundItem item = pgModelItem[y][x];
                    Image itemView = itemImages[y][x];

                // Update Item images
                if(item.value > 0){
                    itemView.setVisible(true);
                    itemView.setDrawable(itemTexDrawables.get(item.value - 1));
                    itemView.setScaling(Scaling.stretch);
                    itemView.setAlign(Align.center);
                    itemView.setSize(itemView.getPrefWidth(), itemView.getPrefHeight());

                    Vector2 imgPos = getImgViewPosXY(x, y, item.moveX, item.moveY, itemView, sliderDeflection);
                    itemView.setPosition(imgPos.x, imgPos.y);
                    itemView.setOrigin(itemView.getWidth() / 2, itemView.getHeight() / 2);

                }
                else{
                    itemView.setVisible(false);
                }
            }
        }
    }

    private void updateNextItemImage(boolean showNextNewPgItem) {
        if((gameLevel == 0) && (showNextNewPgItem) && (playground.getNextNewItem().value > 0)){
            PlaygroundItem item = playground.getNextNewItem();
            nextItemImage.setVisible(true);
            nextItemImage.setDrawable(itemTexDrawables.get(item.value - 1));
            nextItemImage.setScaling(Scaling.stretch);
            nextItemImage.setAlign(Align.center);
            nextItemImage.setSize(nextItemImage.getPrefWidth(), nextItemImage.getPrefHeight());
            Vector2 nextItemPos = getImgViewPosXY(playground.getNextNewItemPosX(), playground.getNextNewItemPosY(), 0, 0, nextItemImage, 0f);
            nextItemImage.setPosition(nextItemPos.x, nextItemPos.y);
            nextItemImage.setOrigin(nextItemImage.getWidth() / 2, nextItemImage.getHeight() / 2);
        }
        else{
            nextItemImage.setVisible(false);
        }
    }

    private void updateNextItemEffect(boolean showNextNewPgItem){
        if((gameLevel <= 1) && (showNextNewPgItem) && (playground.getNextNewItem().value > 0)){
            Vector2 effectPos = getImgViewPosXY(playground.getNextNewItemPosX(), playground.getNextNewItemPosY(), 0, 0, nextItemEffect, 0f);
            nextItemEffect.restartAtNewPosition(effectPos.x, effectPos.y);
        }
        else{
            nextItemEffect.allowCompletion();
        }
    }

    /**
     * Initializes the size and position of the background images.
     * This operation must be called after the PlaygroundActor size has been set
     */
    private void initBackgroundImages(){
        PlaygroundItem pgModelItem[][] = playground.getCurrentItems();
        for(int y = 0; y < pgModelItem.length; y++){
            for(int x = 0; x < pgModelItem[y].length; x++){
                // Initialize background images (is needed only once)
                backgroundImages[y][x].setWidth(getWidth() / playground.getSizeX());
                backgroundImages[y][x].setHeight(getHeight() / playground.getSizeY());
                Vector2 imgPos = getImgViewPosXY(x, y, 0, 0, backgroundImages[y][x], 0f);
                backgroundImages[y][x].setPosition(imgPos.x, imgPos.y);
                backgroundImages[y][x].setOrigin(backgroundImages[y][x].getWidth() / 2f, backgroundImages[y][x].getHeight() / 2f);
                backgroundImages[y][x].setScale(0.98f);
            }
        }
    }

    /**
     * Initializes the size of all playground items (but not the background images
     */
    private void initAllItemsSize(){
        float itemScale = itemSetContainer.playgroundBaseScale * getWidth() / playground.getSizeX() / 180;

        for(int y = 0; y < itemImages.length; y++){
            for(int x = 0; x < itemImages[y].length; x++){
                itemImages[y][x].setScale(itemScale);
            }
        }
        nextItemImage.setScale(itemScale);
    }

    private Vector2 getImgViewPosXY(int xIndex, int yIndex, int xMove, int yMove, Actor img, float sliderDeflection){

        Vector2 vect = vector_getImgViewPosXY;
        float xRaster = getWidth() / playground.getSizeX();
        float yRaster = getHeight() / playground.getSizeY();

        // Calculate the image position (without slider deflection correction)
        vect.x = ((float)xIndex + 0.5f) * xRaster;
        vect.x -= img.getWidth() / 2;
        vect.y = getHeight() - ((float)yIndex + 0.5f) * yRaster;
        vect.y -= img.getHeight() / 2;

        // Calculate slider deflection correction
        vect.x += xMove * xRaster * sliderDeflection;
        vect.y -= yMove * yRaster * sliderDeflection;

        return vect;
    }


    private ArrayList<TextureRegionDrawable> getNewItemTexDrawables(){

        ArrayList<TextureRegionDrawable> texDrawables = new ArrayList<TextureRegionDrawable>();
        for(int i = 1; i <= 16; i++){
            texDrawables.add(new TextureRegionDrawable(itemSetContainer.itemTexAtlas.findRegion("Item", i)));
        }
        return texDrawables;
    }


    private Image getNewNextItemImage(){
        Image img = new Image();
        img.setTouchable(Touchable.disabled);
        img.setVisible(false);
        img.setColor(new Color(1f, 1f, 1f, 0.3f));
        return img;
    }


    private Image[][] getNewItemImages(){

        Image images[][] = new Image[playground.getSizeY()][playground.getSizeX()];

        for(int y = 0; y < images.length; y++){
            for(int x = 0; x < images[y].length; x++){
                images[y][x] = new Image();
                images[y][x].setTouchable(Touchable.disabled);
                images[y][x].setVisible(false);
            }
        }

        return images;
    }


    private Image[][] getNewBackgroundImages() {

        Image images[][] = new Image[playground.getSizeY()][playground.getSizeX()];

        for(int y = 0; y < images.length; y++){
            for(int x = 0; x < images[y].length; x++){
                images[y][x] = new Image(assetManager.getTex(MyAssetManager.PG_TILE_TEX));
                images[y][x].setTouchable(Touchable.disabled);
            }
        }
        return images;
    }

    private void addActor2dArray(Actor actors[][]){
        for(int y = 0; y < actors.length; y++){
            for(int x = 0; x < actors[y].length; x++){
                addActor(actors[y][x]);
            }
        }
    }

    private ParticleEffectActor getNewNextItemEffect(){

        ParticleEffect effect = new ParticleEffect();
        effect.load(Gdx.files.internal("particleEffects/GlassGlobeParticles.p"), Gdx.files.internal("particleEffects"));
        ParticleEffectActor peActor = new ParticleEffectActor(effect);
        peActor.setTouchable(Touchable.disabled);

        peActor.setScale(1.2f * 5f / playground.getSizeX());
        peActor.setAlpha(0.5f);
        peActor.allowCompletion();

        return peActor;
    }
}
