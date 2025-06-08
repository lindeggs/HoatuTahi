package ch.linst.hoatutahi.view.actors;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import ch.linst.hoatutahi.components.MyAssetManager;

public class ScoreViewActor extends Group {

    private static final int NUM_DIGITS = 6;
    private static final float DIGIT_ASPECT_RATIO = 72f / 128f;
    private static final float SCORE_VIEW_WIDTH = 600f;
    private static final float MARGIN_BETWEEN_DIGITS = 20f;
    private static final float SCORE_DIGIT_WIDTH = (SCORE_VIEW_WIDTH - MARGIN_BETWEEN_DIGITS * (NUM_DIGITS - 1)) / NUM_DIGITS;
    private static final float SCORE_VIEW_HEIGHT = SCORE_DIGIT_WIDTH / DIGIT_ASPECT_RATIO;
    private static final float SCORE_UPDATE_THRESHOLD = 0.1f; // Defines the speed in which the score is updated towards targetScore

    private final PropertyChangeSupport pcs;

    private final MyAssetManager assetManager;

    private float scoreUpdateDeltaTime = 0f;
    private int score = 0;
    private int targetScore = 0;

    private final ScoreDigitActor[] digits = new ScoreDigitActor[NUM_DIGITS];

    public ScoreViewActor(MyAssetManager assetManagerArg) {
        super();
        assetManager = assetManagerArg;
        setTouchable(Touchable.disabled);
        setWidth(SCORE_VIEW_WIDTH);
        setHeight(SCORE_VIEW_HEIGHT);
        pcs = new PropertyChangeSupport(this);

        for(int i = 0; i < NUM_DIGITS; i++){
            digits[i] = new ScoreDigitActor(getNewDigitsTexDrawables(), SCORE_DIGIT_WIDTH, SCORE_VIEW_HEIGHT);
            digits[i].setPosition(getWidth() - (i + 1) * (SCORE_DIGIT_WIDTH + MARGIN_BETWEEN_DIGITS) + MARGIN_BETWEEN_DIGITS, 0f);
            addActor(digits[i]);
        }
        setScore(0);
    }

    public ScoreViewActor(MyAssetManager assetManagerArg, PropertyChangeListener listener){
        this(assetManagerArg);
        this.pcs.addPropertyChangeListener(listener);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(targetScore > score){
            scoreUpdateDeltaTime += delta;
            if(scoreUpdateDeltaTime >= SCORE_UPDATE_THRESHOLD){
                scoreUpdateDeltaTime -= SCORE_UPDATE_THRESHOLD;
                updateScore();
            }
        }
    }

    @Override
    public void setScale(float scaleXY) {
        super.setScale(scaleXY);
        setWidth(scaleXY * SCORE_VIEW_WIDTH);
        setHeight(scaleXY * SCORE_VIEW_HEIGHT);
    }


    public void setScore(int newScoreArg){
        int newScore = limitScoreRange(newScoreArg);
        this.score = newScore;
        this.targetScore = newScore;
        setDigits();
    }

    /**
     * Sets only the target score (but not the score).
     * Score will be updated towards the target score step by step
     * @param trgScoreArg the target score
     */
    public void setTargetScore(int trgScoreArg){

        int trgScore = limitScoreRange(trgScoreArg);

        // Update the score immediately if the target score is smaller than the current score,
        // or if the score has already catch-ed up with targetScore
        // (do not update immediately if the score is still catching up)
        boolean doUpdateScoreImmediately = (trgScore < score) || (targetScore == score);

        this.targetScore = trgScore;
        if(doUpdateScoreImmediately){
            updateScore();
        }

    }

    private int limitScoreRange(int scoreArg){
        return Math.min(Math.max(0, scoreArg), (int)Math.pow(10, NUM_DIGITS) - 1);
    }

    /**
     * Updates the score one tick towards the target score if necessary
     * Notifies property change listeners
     */
    private void updateScore(){
        if(targetScore > score){
            int oldScore = score;
            score += 1 + (targetScore - score) / 10;
            pcs.firePropertyChange(getClass().getSimpleName(), oldScore, score);
            setDigits();
        }
    }

    /**
     * Sets every digit according to the current score value
     */
    private void setDigits() {
        boolean allNull = true;
        for(int i = NUM_DIGITS - 1; i >= 0; i--){
            int digitVal = (int)((score / Math.pow(10, i)) % 10);
            if((digitVal == 0) && allNull && (i > 0)){
                digitVal = -1;
            }
            else{
                allNull = false;
            }
            digits[i].setDigitValue(digitVal);
        }
    }

    private ArrayList<TextureRegionDrawable> getNewDigitsTexDrawables(){

        ArrayList<TextureRegionDrawable> texDrawables = new ArrayList<>();

        // Add digits 0..9 to the ArrayList
        for(int i = 0; i <= 9; i++){
            texDrawables.add(new TextureRegionDrawable(assetManager.getAtlas(MyAssetManager.SCORE_DIGITS_ATLAS).findRegion("Digit", i)));
        }

        // Add empty digit at last (position 10)
        texDrawables.add(new TextureRegionDrawable(assetManager.getAtlas(MyAssetManager.SCORE_DIGITS_ATLAS).findRegion("Digit_off")));

        return texDrawables;
    }
}
