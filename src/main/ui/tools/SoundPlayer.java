package ui.tools;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.nio.file.Paths;
import java.util.Random;

// this class controls the sounds played in the swing application; only need one to exist.
public class SoundPlayer extends JFXPanel {
    public static final double SOUND_EFFECTS_NORMAL_VOLUME = 1.0;
    public static final double VOICE_LINES_NORMAL_VOLUME = 0.5;
    private static final String SOUND_FOLDER_URI = Paths.get("./././data/sound/").toUri().toString();

    private final AudioClip audioClipFSharp3 = new AudioClip(SOUND_FOLDER_URI + "fSharp3.mp3");
    private final AudioClip audioClipFSharp4 = new AudioClip(SOUND_FOLDER_URI + "fSharp4.mp3");
    private final AudioClip audioClipExplosion = new AudioClip(SOUND_FOLDER_URI + "mineExplosion.mp3");
    private final AudioClip audioClipOnboard1 = new AudioClip(SOUND_FOLDER_URI + "onboard1.mp3");
    private final AudioClip audioClipOnboard2 = new AudioClip(SOUND_FOLDER_URI + "onboard2.mp3");
    private final AudioClip audioClipHitByDragon1 = new AudioClip(SOUND_FOLDER_URI + "hitByDragon1.mp3");
    private final AudioClip audioClipHitByDragon2 = new AudioClip(SOUND_FOLDER_URI + "hitByDragon2.mp3");
    private final AudioClip audioClipFlagDown = new AudioClip(SOUND_FOLDER_URI + "flagDown.mp3");
    private final AudioClip audioClipFlagUp = new AudioClip(SOUND_FOLDER_URI + "flagDown.mp3");
    private final AudioClip audioClipHitDragon = new AudioClip(SOUND_FOLDER_URI + "hitDragon.mp3");
    private final AudioClip audioClipMineKO = new AudioClip(SOUND_FOLDER_URI + "mineKO.mp3");
    private final AudioClip audioClipTimeKO = new AudioClip(SOUND_FOLDER_URI + "timeKO.mp3");
    private final AudioClip audioClipDragonKO = new AudioClip(SOUND_FOLDER_URI + "dragonKO.mp3");
    private final AudioClip audioClipSkill = new AudioClip(SOUND_FOLDER_URI + "skill.mp3");
    private final AudioClip audioClipSuccess = new AudioClip(SOUND_FOLDER_URI + "success.mp3");

    private final Media inGameBGM = new Media(SOUND_FOLDER_URI + "inGameBGMusicGurenge.mp3");

    private double soundEffectVolume = SOUND_EFFECTS_NORMAL_VOLUME;
    private double voiceLineVolume = VOICE_LINES_NORMAL_VOLUME;

    private MediaPlayer mediaPlayer;

    private static SoundPlayer thisInstance = null;

    // EFFECTS: sets up the JavaFX Toolkit when a SoundPlayer is created
    private SoundPlayer() {
        new JFXPanel();
        mediaPlayer = new MediaPlayer(inGameBGM);
        mediaPlayer.setAutoPlay(false);
    }

    // EFFECTS: creates a sound player if none exists so far; returns the current sound player
    public static SoundPlayer getSoundPlayer() {
        if (thisInstance == null) {
            thisInstance = new SoundPlayer();
        }
        return thisInstance;
    }


    // EFFECTS: plays an F#3 bloop
    public void playFSharp3() {
        audioClipFSharp3.play(soundEffectVolume);
    }

    // EFFECTS: plays an F#4 bloop
    public void playFSharp4() {
        audioClipFSharp4.play(soundEffectVolume);
    }

    // EFFECTS: plays an explosion sound
    public void playExplosion() {
        audioClipExplosion.play(soundEffectVolume);
    }

    // EFFECTS: plays a flag sound
    public void playFlagDown() {
        audioClipFlagDown.play(soundEffectVolume);
    }

//    // EFFECTS: plays a flag sound
//    public void playFlagUp() {
//        audioClipFlagUp.play(soundEffectVolume);
//    }

    // EFFECTS: plays a sound that signals that the dragon was hit by the mage
    public void playHitDragon() {
        audioClipHitDragon.play(Math.min(0.1, voiceLineVolume - 0.25));
    }

    // EFFECTS: plays a sound that signals that the mage detonated a mine
    public void playMineKO() {
        audioClipMineKO.play(voiceLineVolume);
    }

    // EFFECTS: plays a sound that signals that the mage ran out of time
    public void playTimeKO() {
        audioClipTimeKO.play(voiceLineVolume);
    }

    // EFFECTS: plays a sound that signals that the mage was knocked out by the dragon
    public void playDragonKO() {
        audioClipDragonKO.play(voiceLineVolume);
    }

    // EFFECTS: plays a sound that signals that the mage is using their skill
    public void playSkill() {
        audioClipSkill.play(voiceLineVolume);
    }

    // EFFECTS: plays a sound that signals that the mage is using their skill
    public void playSuccess() {
        audioClipSuccess.play(voiceLineVolume);
    }

    // EFFECTS: plays one of two onboard voice lines
    public void playOnboard() {
        Random random = new Random();
        if (random.nextInt(2) == 0) {
            audioClipOnboard1.play(voiceLineVolume);
        } else {
            audioClipOnboard2.play(voiceLineVolume);
        }
    }

    // EFFECTS: plays one of two hit by dragon voice lines
    public void playHitByDragon() {
        Random random = new Random();
        if (random.nextInt(2) == 0) {
            audioClipHitByDragon1.play(voiceLineVolume);
        } else {
            audioClipHitByDragon2.play(voiceLineVolume);
        }
    }

    // MODIFIES: this
    // EFFECTS: plays the given background music song
    public MediaPlayer playInGameBGM() {
        mediaPlayer.play();
        return mediaPlayer;
    }

    // EFFECTS: stops playing whatever sounds the media player passed in was playing
    public void stopBGM() {
        mediaPlayer.stop();
    }

    // MODIFIES: this
    // EFFECTS: turns sound effects on or off.
    public void haveSoundEffectsOrNot(boolean yesSound) {
        if (yesSound) {
            soundEffectVolume = SOUND_EFFECTS_NORMAL_VOLUME;
        } else {
            soundEffectVolume = 0.0;
        }
    }

    // MODIFIES: this
    // EFFECTS: turns voice lines on or off.
    public void haveVoiceLinesOrNot(boolean yesSound) {
        if (yesSound) {
            voiceLineVolume = VOICE_LINES_NORMAL_VOLUME;
        } else {
            voiceLineVolume = 0.0;
        }
    }

    // MODIFIES: this
    // EFFECTS: turns background music on or off.
    public void haveBackgroundMusicOrNot(boolean yesSound) {
        if (yesSound) {
            mediaPlayer.setMute(false);
        } else {
            mediaPlayer.setMute(true);
        }
    }
}
