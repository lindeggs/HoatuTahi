package ch.linst.hoatutahi.view.actors.selectables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import java.util.Objects;

import ch.linst.hoatutahi.components.ItemSetContainer;
import ch.linst.hoatutahi.components.MyAssetManager;
import ch.linst.hoatutahi.view.actors.ParticleEffectActor;


public class GlassGlobeActor extends BaseSelectableActor {

    private static class GlobeImgInfo{
        public final String name;
        public final int posX;
        public final int posY;
        public final float alpha;

        public GlobeImgInfo(String name, int posX, int posY, float alpha) {
            this.name = name;
            this.posX = posX;
            this.posY = posY;
            this.alpha = alpha;
        }
    }

    private static final String GG_OFF      = "GlassGlobe_off";
    private static final String GG_ON       = "GlassGlobe_on2";
    private static final String GG_P        = "GlassGlobe_plus";
    private static final String GG_SEL      = "GlassGlobe_selected";
    private static final String GG_PARTICLE = "GlassGlobe_particles";

    private static final String GG_ITEM = "Item_";
    private static final float  GG_ITEM_ALPHA = 0.5f;
    private static final float  GG_PARTICLES_ALPHA = 0.75f;

    private static final GlobeImgInfo[] GLOBE_IMAGES_INFO = {
            new GlobeImgInfo(GG_SEL, (185 - 284) / 2, (253 - 350) / 2, 1f),
            new GlobeImgInfo(GG_OFF, 0, 0, 1f),
            new GlobeImgInfo(GG_ON, 0, 0, 1f),
            new GlobeImgInfo(GG_P, 185/2 - 77/2, 253 - 185/2 - 77/2, 0.7f)
    };

    private final MyAssetManager assetManager;

    public GlassGlobeActor(ItemSetContainer itemSetContainerArg, MyAssetManager assetManagerArg) {
        super();
        assetManager = assetManagerArg;
        setTouchable(Touchable.enabled);
        addListener(this);

        // Add all static images of the glass globe
        //noinspection GDXJavaUnsafeIterator
        for(Image img: createAllGlassGlobeStaticImages()) addActor(img);

        // Add all (dynamic) prediction images of the glass globe
        //noinspection GDXJavaUnsafeIterator
        for(Image img: createAllGlassGlobePredictionImages(itemSetContainerArg)) addActor(img);

        // Add the ParticleEffectActor containing the animated stars
        addActor(createGGParticles());

        // Set the bounds of the GlassGlobeActor
        setWidth(findActor(GG_OFF).getWidth());
        setHeight(findActor(GG_OFF).getHeight());

        switchOff();
    }

    public void switchOff(){
        hideAllActors();
        findActor(GG_OFF).setVisible(true);
    }

    public void switchOn(){
        hideAllActors();
        findActor(GG_ON).setVisible(true);
    }

    public void showItem(int i){
        hideAllActors();
        findActor(GG_ON).setVisible(true);
        Actor item = findActor(GG_ITEM + i);
        if(item != null){
            item.setVisible(true);
        }
        ((ParticleEffectActor)findActor(GG_PARTICLE)).start();
    }



    private void hideAllActors(){
        for(Actor act: getChildren()){
            if(!Objects.equals(act.getName(), GG_PARTICLE)){
                act.setVisible(false);
            }
            else{
                ((ParticleEffectActor)act).allowCompletion();
            }
        }
    }

    private Array<Image> createAllGlassGlobeStaticImages(){

        Array<Image> aImg = new Array<>();

        for(GlobeImgInfo imgInfo: GLOBE_IMAGES_INFO){
            Image img = new Image(new TextureRegionDrawable(assetManager.getAtlas(MyAssetManager.GLASS_GLOBE_ATLAS).findRegion(imgInfo.name)));
            img.setTouchable(Touchable.disabled);
            img.setVisible(false);
            img.setName(imgInfo.name);
            img.setX(imgInfo.posX);
            img.setY(imgInfo.posY);
            img.setColor(1f, 1f, 1f, imgInfo.alpha);
            aImg.add(img);
        }

        return aImg;
    }

    private ParticleEffectActor createGGParticles(){

        ParticleEffect effect = new ParticleEffect();
        effect.load(Gdx.files.internal("particleEffects/GlassGlobeParticles.p"), Gdx.files.internal("particleEffects"));
        ParticleEffectActor peActor = new ParticleEffectActor(effect);
        peActor.setTouchable(Touchable.disabled);
        //peActor.setVisible(false);
        peActor.setName(GG_PARTICLE);
        peActor.setScale(1.2f);
        peActor.setAlpha(GG_PARTICLES_ALPHA);
        Actor temp = findActor(GG_ON);
        peActor.setPosition(temp.getWidth()/2, temp.getHeight()- temp.getWidth()/2);
        peActor.allowCompletion();

        return peActor;
    }

    private Array<Image> createAllGlassGlobePredictionImages(ItemSetContainer itemSetContainerArg){

        Array<Image> aImg = new Array<>();
        for(int i = 1; i <= 3; i++){
            Image img = new Image(new TextureRegionDrawable(itemSetContainerArg.itemTexAtlas.findRegion("Item", i)));
            img.setTouchable(Touchable.disabled);
            img.setVisible(false);
            img.setName(GG_ITEM + (i));
            Actor temp = findActor(GG_ON);
            img.setX(temp.getWidth() / 2 - img.getPrefWidth() / 2);
            img.setY(temp.getHeight() - temp.getWidth() / 2 - img.getPrefHeight() / 2);
            img.setColor(1f, 1f, 1f, GG_ITEM_ALPHA);
            img.setOrigin(img.getPrefWidth() / 2, img.getPrefHeight() / 2);
            img.setScale(itemSetContainerArg.glasglobeItemsBaseScale);
            aImg.add(img);
        }

        return aImg;
    }

    @Override
    public void select() {
        findActor(GG_SEL).setVisible(true);
    }

    @Override
    public void deSelect() {
        findActor(GG_SEL).setVisible(false);
    }
}
