package ch.linst.hoatutahi.view.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.jetbrains.annotations.NotNull;

import ch.linst.hoatutahi.HoatuTahi;
import ch.linst.hoatutahi.components.ItemSetContainer;
import ch.linst.hoatutahi.components.MyAssetManager;
import ch.linst.hoatutahi.components.audio.AudioProvider;
import ch.linst.hoatutahi.components.playgroundModel.Playground;
import ch.linst.hoatutahi.view.actors.DisposableActor;
import ch.linst.hoatutahi.view.actors.buttons.MyToggleButton;
import ch.linst.hoatutahi.view.actors.radioButtonSelectors.LevelSelectorActor;
import ch.linst.hoatutahi.view.actors.buttons.MyButton;
import ch.linst.hoatutahi.view.actors.animatedSelectors.PgSizeSelectorActor;
import ch.linst.hoatutahi.view.actors.ScoreViewActor;
import ch.linst.hoatutahi.view.actors.animatedSelectors.SelectorActor;

public class HomeStage extends BaseStage {

    private final PgSizeSelectorActor pgSizeSelectorActor;
    private final Button resumeGameBtnActor;
    private final ScoreViewActor highScoreActor;

    public HomeStage(@NotNull Viewport viewportArg, @NotNull HoatuTahi hoatuTahi){
        super(viewportArg, hoatuTahi);

        hoatuTahi.getAudioProvider().playMusic(AudioProvider.Song.HomeStageMusic);

        addActor(getNewRayActor());

        addActor(getNewGameTitleActor());

        pgSizeSelectorActor = getNewPgSizeSelectorActor();
        resumeGameBtnActor = getNewResumeGameBtnActor();
        updateResumeGameBtnEnabledState();

        highScoreActor = getNewScoreViewActor();
        updateHighScoreViewActor();

        Table table = new Table();
        table.align(Align.center|Align.top);
        table.setWidth(HoatuTahi.VIRT_SCREEN_WIDTH);
        table.setPosition(0f, 1750f);

        table.add(getNewLevelSelectorActor()).padBottom(100f);
        table.row();
        table.add(getNewHighScoreLabelActor()).padBottom(20f);
        table.row();
        table.add(highScoreActor).padBottom(60f);
        table.row();
        table.add(pgSizeSelectorActor).padBottom(120f);
        table.row();
        table.add(getNewNewGameBtnActor()).padBottom(80f);
        table.row();
        table.add(resumeGameBtnActor);
        addActor(table);

        addActor(getNewSettingsBtnActor());
        addActor(getMusicOnOffBtnActor());
        addActor(getSoundOnOffBtnActor());


        addActor(getNewAppVersionActor());
    }


    @Override
    public void dispose() {
        super.dispose();
        //noinspection GDXJavaUnsafeIterator
        for (Actor actor : getActors()) {
            if(actor instanceof DisposableActor){
                ((DisposableActor)actor).dispose();
            }
        }
    }

    @NotNull
    private PgSizeSelectorActor getNewPgSizeSelectorActor(){
        PgSizeSelectorActor act = new PgSizeSelectorActor(hoatuTahi);
        act.setSelectedIndex(hoatuTahi.getGameFactory().getPlaygroundSizeSelectorIndex());
        act.setPosition((HoatuTahi.VIRT_SCREEN_WIDTH - act.getWidth()) / 2, 800f);

        act.addPropertyChangeListener(propertyChangeEvent -> {
            if(propertyChangeEvent.getPropertyName().equals(SelectorActor.SELECTION_CHANGED_EV_ID)){
                updateResumeGameBtnEnabledState();
                updateHighScoreViewActor();
            }
        });

        return act;
    }

    /**
     * Enables or disables the resumeGameBtnActor (depending on whether the playground exists)
     */
    private void updateResumeGameBtnEnabledState(){
        Integer[] pgSizeXY = pgSizeSelectorActor.getPgSizeXY();
        int currentGameLevel = hoatuTahi.getGameFactory().getGameLevel();
        boolean pgStored = hoatuTahi.getGameFactory().isPlaygroundStored(pgSizeXY[0], pgSizeXY[1], currentGameLevel);
        resumeGameBtnActor.setDisabled(!pgStored);
        resumeGameBtnActor.setTouchable((pgStored)? Touchable.enabled : Touchable.disabled);
    }

    @NotNull
    private Actor getNewGameTitleActor(){

        float rotateThreshold = 2f;
        float rotateDuration = 7f;

        Image img = new Image(assetManager.getTex(MyAssetManager.HOATU_TAHI_TEX));
        img.setTouchable(Touchable.disabled);
        img.setX((HoatuTahi.VIRT_SCREEN_WIDTH - img.getPrefWidth()) / 2);
        img.setY(HoatuTahi.VIRT_SCREEN_HEIGHT - 400);
        img.setOrigin(img.getWidth() / 2, img.getHeight() / 2);

        img.setRotation(-rotateThreshold);

        RepeatAction repA1 = getGameTitleRotationRepAction(rotateThreshold, rotateDuration);
        RepeatAction repA2 = getGameTitleMoveRepAction();
        img.addAction(new ParallelAction(repA1, repA2));
        return img;
    }

    @NotNull
    private RepeatAction getGameTitleMoveRepAction() {
        float amountY = 10;

        MoveByAction mbA1 = new MoveByAction();
        mbA1.setAmountY(amountY);
        mbA1.setDuration(4);
        MoveByAction mbA2 = new MoveByAction();
        mbA2.setAmountY(-amountY);
        mbA2.setDuration(4);
        SequenceAction sqa = new SequenceAction(mbA1, mbA2);
        RepeatAction repA2 = new RepeatAction();
        repA2.setAction(sqa);
        repA2.setCount(RepeatAction.FOREVER);
        return repA2;
    }

    @NotNull
    private RepeatAction getGameTitleRotationRepAction(float rotateThreshold, float rotateDuration) {
        RotateToAction rotA1 = new RotateToAction();
        rotA1.setRotation(rotateThreshold);
        rotA1.setDuration(rotateDuration);
        RotateToAction rotA2 = new RotateToAction();
        rotA2.setRotation(-rotateThreshold);
        rotA2.setDuration(rotateDuration);
        SequenceAction sqa = new SequenceAction(rotA1, rotA2);
        RepeatAction repA = new RepeatAction();
        repA.setAction(sqa);
        repA.setCount(RepeatAction.FOREVER);
        return repA;
    }

    @NotNull
    private Actor getNewRayActor(){
        float rotateDuration = 50f;
        float imgSizeXY = 1.8f * HoatuTahi.VIRT_SCREEN_HEIGHT;

        Image img = new Image(assetManager.getTex(MyAssetManager.RAY_TEX));
        img.setTouchable(Touchable.disabled);
        img.setSize(imgSizeXY, imgSizeXY);

        img.setX(HoatuTahi.VIRT_SCREEN_WIDTH - (img.getWidth() / 2) - 95);
        img.setY(HoatuTahi.VIRT_SCREEN_HEIGHT - (img.getHeight()/ 2) - 120);
        img.setOrigin(img.getWidth() / 2, img.getHeight() / 2);
        img.setColor(1, 1, 1, 0.2f);

        // Create Action for ray rotation and alpha
        RepeatAction repA1 = getRayRotationRepAction(rotateDuration);
        RepeatAction repA2 = getRayAlphaRepAction();
        img.addAction(new ParallelAction(repA1, repA2));
        return img;
    }

    @NotNull
    private RepeatAction getRayAlphaRepAction() {
        AlphaAction alA1 = new AlphaAction();
        alA1.setAlpha(1);
        alA1.setDuration(2);

        AlphaAction alA2 = new AlphaAction();
        alA2.setAlpha(0.2f);
        alA2.setDuration(2);

        SequenceAction seA = new SequenceAction(Actions.delay(10f), alA1, Actions.delay(0.5f), alA2);
        RepeatAction repA2 = new RepeatAction();
        repA2.setAction(seA);
        repA2.setCount(RepeatAction.FOREVER);
        return repA2;
    }

    @NotNull
    private RepeatAction getRayRotationRepAction(float rotateDuration) {
        RotateByAction rotA = new RotateByAction();
        rotA.setAmount(360f);
        rotA.setDuration(rotateDuration);
        RepeatAction repA1 = new RepeatAction();
        repA1.setAction(rotA);
        repA1.setCount(RepeatAction.FOREVER);
        return repA1;
    }

    @NotNull
    private Actor getNewSettingsBtnActor(){
        TextureRegion tex1 = assetManager.getAtlas(MyAssetManager.BUTTON_ATLAS).findRegion("Settings");
        TextureRegion tex2 = assetManager.getAtlas(MyAssetManager.BUTTON_ATLAS).findRegion("SettingsActive");
        MyButton btn = new MyButton(tex1, tex2, null, null);
        btn.setPosition(40f, HoatuTahi.VIRT_SCREEN_HEIGHT - btn.getHeight() - 20f);

        btn.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hoatuTahi.setStage(SettingsStage.class, null);
            }
        });
        return btn;
    }

    @NotNull
    private Actor getMusicOnOffBtnActor() {
        TextureRegion trMusicOn = assetManager.getAtlas(MyAssetManager.BUTTON_ATLAS).findRegion("MusicOn");
        TextureRegion trMusicOnActive = assetManager.getAtlas(MyAssetManager.BUTTON_ATLAS).findRegion("MusicOnActive");
        TextureRegion trMusicOff = assetManager.getAtlas(MyAssetManager.BUTTON_ATLAS).findRegion("MusicOff");
        TextureRegion trMusicOffActive = assetManager.getAtlas(MyAssetManager.BUTTON_ATLAS).findRegion("MusicOffActive");
        boolean checked = hoatuTahi.getAudioProvider().isMusicEnabled();
        MyToggleButton btn = new MyToggleButton(trMusicOff, trMusicOffActive, trMusicOn, trMusicOnActive, checked);
        btn.setPosition(240f, HoatuTahi.VIRT_SCREEN_HEIGHT - btn.getHeight() - 20f);

        btn.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hoatuTahi.getAudioProvider().setMusicEnabled(btn.isChecked());
            }
        });

        return btn;
    }

    @NotNull
    private Actor getSoundOnOffBtnActor() {
        TextureRegion trNoiseOn = assetManager.getAtlas(MyAssetManager.BUTTON_ATLAS).findRegion("NoiseOn");
        TextureRegion trNoiseOnActive = assetManager.getAtlas(MyAssetManager.BUTTON_ATLAS).findRegion("NoiseOnActive");
        TextureRegion trNoiseOff = assetManager.getAtlas(MyAssetManager.BUTTON_ATLAS).findRegion("NoiseOff");
        TextureRegion trNoiseOffActive = assetManager.getAtlas(MyAssetManager.BUTTON_ATLAS).findRegion("NoiseOffActive");
        boolean checked = hoatuTahi.getAudioProvider().isSoundEnabled();
        MyToggleButton btn = new MyToggleButton(trNoiseOff, trNoiseOffActive, trNoiseOn, trNoiseOnActive, checked);
        btn.setPosition(440f, HoatuTahi.VIRT_SCREEN_HEIGHT - btn.getHeight() - 20f);

        btn.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hoatuTahi.getAudioProvider().setSoundEnabled(btn.isChecked());
            }
        });

        return btn;
    }


    /**
     * Creates the new game button actor and returns it
     */
    @NotNull
    private Actor getNewNewGameBtnActor() {
        TextureRegion tex1 = assetManager.getAtlas(MyAssetManager.BUTTON_ATLAS).findRegion("BtnNewGame");
        TextureRegion tex2 = assetManager.getAtlas(MyAssetManager.BUTTON_ATLAS).findRegion("BtnNewGameAct");
        final MyButton btn = new MyButton(tex1, tex2, null, null);

        btn.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Store the current selection of the pgSizeSelectorActor to the GameFactory
                hoatuTahi.getGameFactory().setPlaygroundSizeSelectorIndex(pgSizeSelectorActor.getSelectedIndex());

                // Start new game
                Integer[] pgSizeXY = pgSizeSelectorActor.getPgSizeXY();
                Playground playground = hoatuTahi.getGameFactory().getNewPlayground(pgSizeXY[0], pgSizeXY[1]);

                // Remove the specific playgrounds view information
                // (this causes the current settings to be applied to a new PlaygroundView)
                int selectedLevel = hoatuTahi.getGameFactory().getGameLevel();
                hoatuTahi.getGameViewFactory().removePlaygroundViewSettings(playground, selectedLevel);

                hoatuTahi.setStage(GameStage.class, playground);
            }
        });

        return btn;
    }

    /**
     * Creates the resume game button actor and adds it to the stage
     */
    @NotNull
    private Button getNewResumeGameBtnActor() {
        TextureRegion tex1 = assetManager.getAtlas(MyAssetManager.BUTTON_ATLAS).findRegion("BtnResumeGame");
        TextureRegion tex2 = assetManager.getAtlas(MyAssetManager.BUTTON_ATLAS).findRegion("BtnResumeGameAct");
        TextureRegion tex3 = assetManager.getAtlas(MyAssetManager.BUTTON_ATLAS).findRegion("BtnResumeGameDis");
        final MyButton btn = new MyButton(tex1, tex2, null, tex3);

        btn.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Store the current selection of the pgSizeSelectorActor to the GameFactory
                hoatuTahi.getGameFactory().setPlaygroundSizeSelectorIndex(pgSizeSelectorActor.getSelectedIndex());

                // Start new game
                Integer[] pgSizeXY = pgSizeSelectorActor.getPgSizeXY();
                int currentGameLevel = hoatuTahi.getGameFactory().getGameLevel();
                Playground playground = hoatuTahi.getGameFactory().getPlayground(pgSizeXY[0], pgSizeXY[1], currentGameLevel);
                hoatuTahi.setStage(GameStage.class, playground);
            }
        });

        return btn;
    }

    @NotNull
    private Actor getNewAppVersionActor(){

        String appInfoString = hoatuTahi.getPlatformService().getBuildConfigBuildType().toUpperCase() + "  V" +
                hoatuTahi.getPlatformService().getAppVerison();



        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = new BitmapFont(Gdx.files.internal("bitmapFonts/DejaVuSansLight_40.fnt"));
        labelStyle.fontColor = Color.BLACK;
        Label label = new Label(appInfoString,labelStyle);

        label.setX(HoatuTahi.VIRT_SCREEN_WIDTH - label.getWidth() - 10);
        label.setY(HoatuTahi.VIRT_SCREEN_HEIGHT - label.getHeight() - 5);

        return label;
    }

    @NotNull
    private LevelSelectorActor getNewLevelSelectorActor(){

        int selectedLevel = hoatuTahi.getGameFactory().getGameLevel();

        int selectedItemSet = hoatuTahi.getGameViewFactory().getItemSetSelected();
        ItemSetContainer itemSetContainer = assetManager.getItemSetContainer(selectedItemSet);

        LevelSelectorActor act = new LevelSelectorActor(selectedLevel, itemSetContainer, assetManager);

        act.addSelectionChangedListener(propertyChangeEvent -> {
            if((propertyChangeEvent.getNewValue() != null) && (propertyChangeEvent.getNewValue() instanceof Integer)){
                int selectedLevel1 = (Integer) propertyChangeEvent.getNewValue();
                hoatuTahi.getGameFactory().setGameLevel(selectedLevel1);
                pgSizeSelectorActor.refreshVisibleGameLevel();
                updateResumeGameBtnEnabledState();
                updateHighScoreViewActor();
            }
        });

        return act;
    }

    @NotNull
    private ScoreViewActor getNewScoreViewActor() {
        ScoreViewActor actor = new ScoreViewActor(assetManager);
        actor.setScale(0.7f);
        return  actor;
    }

    private void updateHighScoreViewActor() {
        Integer[] pgSizeXY = pgSizeSelectorActor.getPgSizeXY();
        int highScore = hoatuTahi.getGameFactory().getHighScore(pgSizeXY[0], pgSizeXY[1]);
        highScoreActor.setScore(highScore);
    }


    @NotNull
    private Actor getNewHighScoreLabelActor(){
        float scale = 1.2f;
        Image img = new Image(assetManager.getTex(MyAssetManager.HIGH_SCORE_LABEL_TEX));
        Group group = new Group();
        group.addActor(img);
        group.setSize(scale * img.getWidth(), scale * img.getHeight());
        group.setScale(scale);
        return group;
    }
}
