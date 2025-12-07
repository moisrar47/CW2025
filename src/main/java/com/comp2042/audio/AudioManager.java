package com.comp2042.audio;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;

/**
 * Encapsulates all game audio behaviour (background music and sound effects)
 * so that the GUI layer does not have to manage {@link javafx.scene.media.MediaPlayer}
 * or {@link javafx.scene.media.AudioClip} instances directly.
 */
public class AudioManager {

    private MediaPlayer backgroundPlayer;
    private AudioClip moveClip;
    private AudioClip rotateClip;
    private AudioClip softDropClip;
    private AudioClip hardDropClip;
    private AudioClip gameOverClip;
    private AudioClip lineClearSound;
    private AudioClip levelUpClip;

    private double masterVolume = 0.5;
    private boolean musicEnabled = true; // default ON
    private boolean sfxEnabled = true;


    /**
     * Creates a new {@code AudioManager} and loads all background music
     * and sound-effect resources.
     * <p>
     * If any audio resource fails to load, the game will continue running
     * without crashing, but that sound will simply not play.
     */
    public AudioManager() {
        initAudio();
    }

    private void initAudio() {
        try {
            moveClip       = loadClip("/sounds/move.wav");
            rotateClip     = loadClip("/sounds/rotate.wav");
            softDropClip   = loadClip("/sounds/soft_drop.wav");
            hardDropClip   = loadClip("/sounds/hard_drop.wav");
            gameOverClip   = loadClip("/sounds/game_over.wav");
            lineClearSound = loadClip("/sounds/line_clear.wav");
            levelUpClip    = loadClip("/sounds/level_up.wav");

            URL bgmUrl = getClass().getResource("/sounds/bgm.mp3");
            if (bgmUrl != null) {
                backgroundPlayer = new MediaPlayer(new Media(bgmUrl.toExternalForm()));
                backgroundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                backgroundPlayer.setVolume(masterVolume);
            }
        } catch (Exception e) {
            // fail silently in game, just logs to console
            e.printStackTrace();
        }
    }

    private AudioClip loadClip(String path) {
        URL url = getClass().getResource(path);
        if (url == null) {
            return null;
        }
        return new AudioClip(url.toExternalForm());
    }

    /**
     * Returns the current master volume used for both music and sound effects.
     *
     * @return the master volume (typically between 0.0 and 1.0)
     */
    public double getMasterVolume() {
        return masterVolume;
    }

    /**
     * Sets the master volume used for both music and sound effects and
     * reapplies the volume to all loaded audio players.
     *
     * @param masterVolume the new master volume (typically between 0.0 and 1.0)
     */
    public void setMasterVolume(double masterVolume) {
        this.masterVolume = masterVolume;
        applyVolumeToAll();
    }

    /**
     * Indicates whether background music is currently enabled.
     *
     * @return {@code true} if music is enabled; {@code false} if it is muted
     */
    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    /**
     * Toggles the global music enabled flag and reapplies volume settings.
     * When music is disabled, background music is effectively muted.
     */
    public void toggleMusicEnabled() {
        this.musicEnabled = !this.musicEnabled;
        applyVolumeToAll();
    }

    /**
     * Indicates whether sound effects are currently enabled.
     *
     * @return {@code true} if sound effects are enabled; {@code false} if they are muted
     */
    public boolean isSfxEnabled() {
        return sfxEnabled;
    }

    /**
     * Toggles the global sound-effects enabled flag.
     * When disabled, calls to the SFX helper methods will not play any sounds.
     */
    public void toggleSfxEnabled() {
        this.sfxEnabled = !this.sfxEnabled;
    }

    private void applyVolumeToAll() {
        if (backgroundPlayer != null) {
            backgroundPlayer.setVolume(musicEnabled ? masterVolume : 0.0);
        }
        // Clips get their volume set on each play; no need to update them here.
    }

    /**
     * Starts the looping background music if music is enabled and a track is loaded.
     * <p>
     * Music will not start if the game is currently paused or in a game-over state.
     *
     * @param paused   {@code true} if the game is currently paused
     * @param gameOver {@code true} if the game is currently in a game-over state
     */
    public void startBackgroundMusic(boolean paused, boolean gameOver) {
        if (!musicEnabled || backgroundPlayer == null) {
            return;
        }
        if (paused || gameOver) {
            return;
        }

        backgroundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        backgroundPlayer.setVolume(masterVolume);
        backgroundPlayer.play();
    }

    /**
     * Stops the background music and rewinds it to the beginning.
     * Used when leaving the game or resetting the session.
     */
    public void stopBackgroundMusic() {
        if (backgroundPlayer != null) {
            backgroundPlayer.stop();
            backgroundPlayer.seek(Duration.ZERO);   // rewind to start
            backgroundPlayer.setVolume(0.0);
        }
    }

    /**
     * Pauses the background music without resetting its playback position.
     * Used when the in-game pause menu is opened.
     */
    public void pauseBackgroundMusic() {
        if (backgroundPlayer != null) {
            backgroundPlayer.pause();
        }
    }

    /**
     * Resumes the background music from its current position if music is enabled,
     * a background track is loaded, and the game is not in a game-over state.
     *
     * @param gameOver {@code true} if the game is currently over; music will not resume in this case
     */
    public void resumeBackgroundMusic(boolean gameOver) {
        if (!musicEnabled || backgroundPlayer == null) {
            return;
        }
        if (gameOver) {
            return;
        }
        backgroundPlayer.setVolume(masterVolume);
        backgroundPlayer.play();
    }

    /**
     * Plays the sound effect used when the active brick is moved left or right.
     */
    public void playMoveSound() {
        if (!sfxEnabled || moveClip == null) return;
        moveClip.setVolume(masterVolume);
        moveClip.play();
    }

    /**
     * Plays the sound effect used when the active brick is rotated.
     */
    public void playRotateSound() {
        if (!sfxEnabled || rotateClip == null) return;
        rotateClip.setVolume(masterVolume);
        rotateClip.play();
    }

    /**
     * Plays the sound effect used when the brick is soft-dropped.
     */
    public void playSoftDropSound() {
        if (!sfxEnabled || softDropClip == null) return;
        softDropClip.setVolume(masterVolume);
        softDropClip.play();
    }

    /**
     * Plays the sound effect used when the brick is hard-dropped to the bottom.
     */
    public void playHardDropSound() {
        if (!sfxEnabled || hardDropClip == null) return;
        hardDropClip.setVolume(masterVolume);
        hardDropClip.play();
    }

    /**
     * Plays the sound effect used when one or more lines are cleared.
     */
    public void playLineClearSound() {
        if (!sfxEnabled || lineClearSound == null) return;
        lineClearSound.setVolume(masterVolume);
        lineClearSound.play();
    }

    /**
     * Plays the sound effect used when the player advances to the next level.
     */
    public void playLevelUpSound() {
        if (!sfxEnabled || levelUpClip == null) return;
        levelUpClip.setVolume(masterVolume);
        levelUpClip.play();
    }

    /**
     * Plays the sound effect used when the game ends in a game-over.
     */
    public void playGameOverSound() {
        if (!sfxEnabled || gameOverClip == null) return;
        gameOverClip.setVolume(masterVolume);
        gameOverClip.play();
    }
}
