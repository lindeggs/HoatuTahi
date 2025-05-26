package ch.linst.hoatutahi.components.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.TimeUtils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import ch.linst.hoatutahi.view.actors.ScoreViewActor;

public class AudioProvider implements PropertyChangeListener{

    public enum Song{
        HomeStageMusic,
        GameStageMusic,
        GameOver
    }

    private static final float DELAY_INCREASE_FACTOR = 1.2f; // Increase factor of the delay after each song

    // Lock timeout, until the score update sound will be played again (this is to prevent jitter)
    private static final int SCORE_UPDATE_SOUND_LOCK_TIME = 150;
    private long scoreUpdateSoundLockTimeThreshold = 0;

    private final AudioProviderSettings audioPSettings;

    private final Music homeStageMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/2019-00029-1655_lucid-dreams.mp3"));
    private final Music[] gameStageMusic = {
            Gdx.audio.newMusic(Gdx.files.internal("audio/2016-00099-1051_carefully.mp3")),
            Gdx.audio.newMusic(Gdx.files.internal("audio/2018-00116-1522_long-run.mp3")),
            Gdx.audio.newMusic(Gdx.files.internal("audio/2015-00239-265_beautiful.mp3")),
            Gdx.audio.newMusic(Gdx.files.internal("audio/2019-00029-1655_lucid-dreams.mp3"))
    };
    private final Music gameOverSpoken = Gdx.audio.newMusic(Gdx.files.internal("audio/GameOver.mp3"));
    private final Music gameOverMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/2018-00173-1579_have-fun.mp3"));

    private final Sound scoreUpdateSound = Gdx.audio.newSound(Gdx.files.internal("audio/Loop_2T_100ms.wav"));

    private int gameStageMusicIndex = 0;
    private Music currentMusic = homeStageMusic;

    private int minDelayBetweenSongs = 20;
    private int maxDelayBetweenSongs = 60;
    private final Random randomDelayGen = new Random(System.nanoTime());

    private final ScheduledExecutorService gameStageMusicExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> gameStageMusicScheduledFuture = null;


    /**
     * Constructor
     */
    public AudioProvider(AudioProviderSettings audioProviderSettingsArg) {
        audioPSettings = audioProviderSettingsArg;
        homeStageMusic.setLooping(true);

        final Music.OnCompletionListener gameStageAudioCompletionListener = new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                gameStageMusicIndex = (gameStageMusicIndex + 1) % gameStageMusic.length;

                int delayTime = randomDelayGen.nextInt(maxDelayBetweenSongs - minDelayBetweenSongs) + minDelayBetweenSongs;

                minDelayBetweenSongs = (int)((float)minDelayBetweenSongs * DELAY_INCREASE_FACTOR);
                maxDelayBetweenSongs = (int)((float)maxDelayBetweenSongs * DELAY_INCREASE_FACTOR);

                gameStageMusicScheduledFuture = gameStageMusicExecutorService.schedule(new Runnable() {
                    @Override
                    public void run() {
                        doPlayMusic(gameStageMusic[gameStageMusicIndex]);
                    }
                }, delayTime, TimeUnit.SECONDS);
            }
        };

        for (Music music:gameStageMusic){
            music.setOnCompletionListener(gameStageAudioCompletionListener);
        }

        gameOverSpoken.setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                doPlayMusic(gameOverMusic);
            }
        });
    }

    public void playMusic(Song songArg){

        if(gameStageMusicScheduledFuture != null) gameStageMusicScheduledFuture.cancel(false);

        switch(songArg){
            case HomeStageMusic:
                doPlayMusic(homeStageMusic);
                break;
            case GameStageMusic:
                doPlayMusic(gameStageMusic[gameStageMusicIndex]);
                break;
            case GameOver:
                doPlayMusic(gameOverSpoken);
                break;
            default:
                throw new IllegalArgumentException("Unknown song!");
        }
    }

    public boolean isMusicEnabled() {
        return audioPSettings.musicEnabled;
    }

    public void setMusicEnabled(boolean musicEnabled) {
        this.audioPSettings.musicEnabled = musicEnabled;
        doPlayMusic(currentMusic);

    }

    public boolean isSoundEnabled() {
        return audioPSettings.soundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.audioPSettings.soundEnabled = soundEnabled;
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {

        if(propertyChangeEvent.getPropertyName().equals(ScoreViewActor.class.getSimpleName())){
            if(audioPSettings.soundEnabled){
                long currentTime = TimeUtils.millis();
                if(currentTime >= scoreUpdateSoundLockTimeThreshold){
                    scoreUpdateSound.play(0.3f);
                    scoreUpdateSoundLockTimeThreshold = currentTime + SCORE_UPDATE_SOUND_LOCK_TIME;
                }
            }
        }
    }

    /**
     * Internal synchronized operation to play music
     * This operation is synchronized to avoid any race conditions (it may be called by a timed runnable)
     * @param musicArg The music to play
     */
    private synchronized void doPlayMusic(Music musicArg){
        if(currentMusic != musicArg){
            currentMusic.stop();
            currentMusic = musicArg;
        }

        if(audioPSettings.musicEnabled || ((musicArg == gameOverSpoken) && audioPSettings.soundEnabled)){
            currentMusic.play();
        }else {
            currentMusic.pause();
        }
    }
}
