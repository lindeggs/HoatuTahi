package ch.linst.hoatutahi.components;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class MyAssetManager extends AssetManager {

    // Texture atlas definitions
    public static final String BUTTON_ATLAS               = "spriteSheets/Buttons.atlas";
    public static final String GLASS_GLOBE_ATLAS          = "spriteSheets/GlassGlobe.atlas";
    public static final String SCORE_DIGITS_ATLAS         = "spriteSheets/ScoreDigits.atlas";
    public static final String ARROW_ATLAS                = "spriteSheets/HorizontalArrows.atlas";
    public static final String ITEM_SET_SELECTOR_ATLAS    = "spriteSheets/ItemSetSelector.atlas";

    public static final String ITEM_SET_ATLAS[]           = {"spriteSheets/ItemSet1.atlas", "spriteSheets/ItemSet2.atlas"};
    public static final int ITEM_SET_COUNT = ITEM_SET_ATLAS.length;

    // Texture definitions
    public static final String SLIDE_PAD_SLIDER_TEX       = "slidePad/SlidePadSlider.png";
    public static final String SLIDE_PAD_BG_TEX           = "slidePad/SlidePadBackground.png";
    public static final String PG_TILE_TEX                = "ItemBkgTile_green.png";
    public static final String HIGH_SCORE_LABEL_TEX       = "HighScore.png";
    public static final String GAME_OVER_TEX              = "GameOver.png";
    public static final String HOATU_TAHI_TEX             = "HoatuTahi.png";
    public static final String RAY_TEX                    = "Ray.png";

    // Item set scaling definitions                                      balls set, 2048 set
    private static final float PLAYGROUND_VIEW_BASE_SCALE[]           = {1.15f    , 0.93f};
    private static final float ITEMSET_PRESENTATION_VIEW_BASE_SCALE[] = {1.8f     , 1.5f};
    private static final float GLASGLOBE_ITEMS_BASE_SCALE[]           = {1.4f     , 0.84f};

    public MyAssetManager() {
        super();
    }

    public void loadAllOpenGlAssets(){
        loadAllTexAtlasAssets();
        loadAllTexAssets();
    }

    public ItemSetContainer getItemSetContainer(int itemSetNr){
        return new ItemSetContainer(
                getAtlas(ITEM_SET_ATLAS[itemSetNr]),
                PLAYGROUND_VIEW_BASE_SCALE[itemSetNr],
                ITEMSET_PRESENTATION_VIEW_BASE_SCALE[itemSetNr],
                GLASGLOBE_ITEMS_BASE_SCALE[itemSetNr]);
    }

    public Texture getTex(String fileArg){
        Texture retVal = null;
        if(isLoaded(fileArg, Texture.class)){
            retVal = get(fileArg, Texture.class);
        }
        else{
            throw new IllegalArgumentException(String.format("Didn't find texture \"%s\"", fileArg));
        }
        return retVal;
    }

    public TextureAtlas getAtlas(String fileArg){
        TextureAtlas retVal = null;
        if(isLoaded(fileArg, TextureAtlas.class)){
            retVal = get(fileArg, TextureAtlas.class);
        }
        else{
            throw new IllegalArgumentException(String.format("Didn't find texture atlas \"%s\"", fileArg));
        }
        return retVal;
    }

    private void loadAllTexAtlasAssets(){
        load(BUTTON_ATLAS, TextureAtlas.class);
        load(GLASS_GLOBE_ATLAS, TextureAtlas.class);
        load(SCORE_DIGITS_ATLAS, TextureAtlas.class);
        load(ARROW_ATLAS, TextureAtlas.class);
        load(ITEM_SET_SELECTOR_ATLAS, TextureAtlas.class);

        for (String atlas:ITEM_SET_ATLAS) {
            load(atlas, TextureAtlas.class);
        }
    }

    private void loadAllTexAssets(){
        load(SLIDE_PAD_SLIDER_TEX, Texture.class);
        load(SLIDE_PAD_BG_TEX, Texture.class);
        load(PG_TILE_TEX, Texture.class);
        load(HIGH_SCORE_LABEL_TEX, Texture.class);
        load(GAME_OVER_TEX, Texture.class);
        load(HOATU_TAHI_TEX, Texture.class);
        load(RAY_TEX, Texture.class);
    }
}
