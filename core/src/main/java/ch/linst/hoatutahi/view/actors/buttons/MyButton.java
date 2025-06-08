package ch.linst.hoatutahi.view.actors.buttons;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MyButton extends Button {

    public MyButton(TextureRegion up, TextureRegion down, TextureRegion checked, TextureRegion disabled) {
        super();

        ButtonStyle buttonStyle = new ButtonStyle();
        buttonStyle.up = new TextureRegionDrawable(up);
        buttonStyle.down = new TextureRegionDrawable(down);
        buttonStyle.checked = new TextureRegionDrawable((checked != null)? checked : down);
        buttonStyle.disabled = new TextureRegionDrawable((disabled != null)? disabled : up);

        setStyle(buttonStyle);

        setSize(getPrefWidth(), getPrefHeight());

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggle(); // This transients from down to up state again (since buttons to toggle)
            }
        });
    }

    public MyButton(){
        super();
    }

    @Override
    public void setScale(float scaleXY) {
        super.setScale(scaleXY);
        setSize(getPrefWidth() * scaleXY, getPrefHeight() * scaleXY);
    }
}
