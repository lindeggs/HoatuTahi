package ch.linst.hoatutahi.view.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import ch.linst.hoatutahi.components.MyAssetManager;
import ch.linst.hoatutahi.components.billing.AppStoreItem;
import ch.linst.hoatutahi.components.billing.service.ProductDetails;
import ch.linst.hoatutahi.view.actors.buttons.MyClickableGroup;

public class PriceTagActor extends MyClickableGroup {

    MyAssetManager assetManager;
    AppStoreItem appStoreItem;

    public PriceTagActor(MyAssetManager assetManagerArg, TextureRegionDrawable unlockHintDrawable, AppStoreItem appStoreItemArg) {

        assetManager = assetManagerArg;
        appStoreItem = appStoreItemArg;

        Actor billingBackground = new Image(assetManagerArg.getAtlas(MyAssetManager.ITEM_SET_SELECTOR_ATLAS).
                findRegion("BillingBackground"));

        setSize(billingBackground.getWidth(), billingBackground.getHeight());

        addActor(billingBackground);
        addActor(getNewUnlockHintActor(unlockHintDrawable));
        addActor(getNewPriceInfoActor(appStoreItemArg.getProductDetails()));

        setSize(billingBackground.getWidth(), billingBackground.getHeight());
    }

    private Actor getNewPriceInfoActor(ProductDetails productDetails){

        Group priceInfo = new Group();

        Actor trolley = new Image(assetManager.getAtlas(MyAssetManager.ITEM_SET_SELECTOR_ATLAS).
                findRegion("Trolley"));
        Actor currencyLabel = getNewLabelActor(productDetails.getCurrency());
        Actor priceLabel = getNewLabelActor(productDetails.getPrice());


        float xOffset = 40f;
        float xMargin = 50f;
        float yMargin = 0f;

        currencyLabel.setPosition(trolley.getWidth() + xMargin, trolley.getHeight() / 2 + yMargin / 2);
        priceLabel.setPosition(trolley.getWidth() + xMargin, trolley.getHeight() / 2 - currencyLabel.getHeight() - yMargin / 2);

        priceInfo.addActor(trolley);
        priceInfo.addActor(currencyLabel);
        priceInfo.addActor(priceLabel);

        priceInfo.setWidth(currencyLabel.getX() + currencyLabel.getWidth());
        priceInfo.setHeight(Math.max(trolley.getY() + trolley.getHeight(), currencyLabel.getY() + currencyLabel.getHeight()) -
                Math.min(trolley.getY(), priceLabel.getY()));

        priceInfo.setPosition(xOffset, (getHeight() - priceInfo.getHeight()) / 2 - 25f);

        priceInfo.addAction(getAlphaRepAction(true));
        return priceInfo;
    }

    private Actor getNewLabelActor(String text){
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = new BitmapFont(Gdx.files.internal("bitmapFonts/Dyuthi_56.fnt"));
        labelStyle.fontColor = Color.WHITE;
        Label label = new Label(text,labelStyle);
        return label;
    }





    private Actor getNewUnlockHintActor(TextureRegionDrawable drawable){
        Image img = new Image(drawable);
        img.setPosition((getWidth() - img.getWidth()) / 2, (getHeight() - img.getHeight()) / 2 - 30f);
        img.setColor(1f, 1f, 1f, 0f);
        img.addAction(getAlphaRepAction(false));
        return img;
    }

    private RepeatAction getAlphaRepAction(boolean initiallyVisible) {
        AlphaAction alA1 = new AlphaAction();
        alA1.setAlpha((initiallyVisible)? 0f: 1f);
        alA1.setDuration(0.5f);

        AlphaAction alA2 = new AlphaAction();
        alA2.setAlpha((initiallyVisible)? 1f: 0f);
        alA2.setDuration(0.5f);

        SequenceAction seA = new SequenceAction(Actions.delay(2f), alA1, Actions.delay(2f), alA2);

        return Actions.forever(seA);
    }


    @Override
    protected void onTouchUpEvent() {
        Gdx.app.debug("HoatuTahi", "Initiate purchase");
        appStoreItem.launchBillingWorkflow();
    }
}
