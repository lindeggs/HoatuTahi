package ch.linst.hoatutahi.view.actors.buttons;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MyToggleButton extends MyButton {

    private Button.ButtonStyle btnStyle1;
    private Button.ButtonStyle btnStyle2;

    public MyToggleButton(TextureRegion up1, TextureRegion down1, TextureRegion up2, TextureRegion down2, boolean checked) {
        btnStyle1 = new Button.ButtonStyle();
        btnStyle1.up = new TextureRegionDrawable(up1);
        btnStyle1.down = new TextureRegionDrawable(down1);
        btnStyle2 = new Button.ButtonStyle();
        btnStyle2.up = new TextureRegionDrawable(up2);
        btnStyle2.down = new TextureRegionDrawable(down2);
        setChecked(checked);
        setStyle((isChecked()) ? btnStyle2 : btnStyle1);
        setSize(getPrefWidth(), getPrefHeight());

        addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setStyle((isChecked()) ? btnStyle2 : btnStyle1);
                setSize(getPrefWidth(), getPrefHeight());
            }
        });
    }
}
