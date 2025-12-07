package com.comp2042.audio;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;

/*
  Encapsulates all game audio behaviour (music and sound effects) so that
  GuiController does not have to manage Media/AudioClip objects directly.
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

    // volume + toggles

    public double getMasterVolume() {
        return masterVolume;
    }

    public void setMasterVolume(double masterVolume) {
        this.masterVolume = masterVolume;
        applyVolumeToAll();
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public void toggleMusicEnabled() {
        this.musicEnabled = !this.musicEnabled;
        applyVolumeToAll();
    }

    public boolean isSfxEnabled() {
        return sfxEnabled;
    }

    public void toggleSfxEnabled() {
        this.sfxEnabled = !this.sfxEnabled;
    }

    private void applyVolumeToAll() {
        if (backgroundPlayer != null) {
            backgroundPlayer.setVolume(musicEnabled ? masterVolume : 0.0);
        }
        // Clips get their volume set on each play; no need to update them here.
    }

    // background music
    // Starts the background music if enabled and the game is not paused or over.

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

    public void stopBackgroundMusic() {
        if (backgroundPlayer != null) {
            backgroundPlayer.stop();
            backgroundPlayer.seek(Duration.ZERO);   // rewind to start
            backgroundPlayer.setVolume(0.0);
        }
    }

    // pause without resetting to the beginning
    public void pauseBackgroundMusic() {
        if (backgroundPlayer != null) {
            backgroundPlayer.pause();
        }
    }

    // resume from current position (used when unpausing)
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

    // SFX helpers
    public void playMoveSound() {
        if (!sfxEnabled || moveClip == null) return;
        moveClip.setVolume(masterVolume);
        moveClip.play();
    }

    public void playRotateSound() {
        if (!sfxEnabled || rotateClip == null) return;
        rotateClip.setVolume(masterVolume);
        rotateClip.play();
    }

    public void playSoftDropSound() {
        if (!sfxEnabled || softDropClip == null) return;
        softDropClip.setVolume(masterVolume);
        softDropClip.play();
    }

    public void playHardDropSound() {
        if (!sfxEnabled || hardDropClip == null) return;
        hardDropClip.setVolume(masterVolume);
        hardDropClip.play();
    }

    public void playLineClearSound() {
        if (!sfxEnabled || lineClearSound == null) return;
        lineClearSound.setVolume(masterVolume);
        lineClearSound.play();
    }

    public void playLevelUpSound() {
        if (!sfxEnabled || levelUpClip == null) return;
        levelUpClip.setVolume(masterVolume);
        levelUpClip.play();
    }

    public void playGameOverSound() {
        if (!sfxEnabled || gameOverClip == null) return;
        gameOverClip.setVolume(masterVolume);
        gameOverClip.play();
    }
}
