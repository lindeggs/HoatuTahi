package ch.linst.hoatutahi.components;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class ItemSetContainer {
    public final TextureAtlas itemTexAtlas;
    public final float playgroundBaseScale;
    public final float itemSetPresentationBaseScale;
    public final float glasglobeItemsBaseScale;

    public ItemSetContainer(TextureAtlas itemTexAtlasArg, float playgroundBaseScaleArg, float itemSetPresentationBaseScaleArg, float glasglobeItemsBaseScaleArg){
        itemTexAtlas = itemTexAtlasArg;
        playgroundBaseScale = playgroundBaseScaleArg;
        itemSetPresentationBaseScale = itemSetPresentationBaseScaleArg;
        glasglobeItemsBaseScale = glasglobeItemsBaseScaleArg;
    }
}
