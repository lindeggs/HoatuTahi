package ch.linst.hoatutahi.view.actors;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.ArrayList;

public class ScoreDigitActor extends Image {

    private final ArrayList<TextureRegionDrawable> digitsTexDrawables;

    public ScoreDigitActor(ArrayList<TextureRegionDrawable> digitsTexDrawablesArg, float widthArg, float heightArg) {
        super(digitsTexDrawablesArg.get(10));

        setTouchable(Touchable.disabled);
        digitsTexDrawables = digitsTexDrawablesArg;

        setSize(widthArg, heightArg);
    }

    public void setDigitValue(int valueArg){

        if((valueArg >= 0) && (valueArg <= 9)){
            setDrawable(digitsTexDrawables.get(valueArg));
        }
        else{
            setDrawable(digitsTexDrawables.get(10));
        }



    }
}
