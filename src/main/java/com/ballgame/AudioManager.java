package com.ballgame;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class AudioManager {
    private final String AUDIO_PATH = "src/main/java/com/ballgame/assets/audio";

    public final Clip introMusic;
    public final Clip gameMusic;
    public final Clip loseMusic;
    public final Clip fireballContactEffect; 

    public AudioManager() {
        // Music From https://downloads.khinsider.com/
        this.introMusic = getMediaPlayer("intro.wav");
        this.gameMusic = getMediaPlayer("game.wav");
        this.loseMusic = getMediaPlayer("lose.wav");
        this.fireballContactEffect= getMediaPlayer("fireball-contact.wav");
    }

    /**
     * @param path path of audio file located in {@link AudioManager#AUDIO_PATH}
     * @return Mediaplayer JavaFX Object
     */
    private Clip getMediaPlayer(String path) {
        try {
            String absolutePath = Paths.get(AUDIO_PATH, path).toString();
            InputStream audioStream = new FileInputStream(absolutePath);
            InputStream bufferedIn = new BufferedInputStream(audioStream);

            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(bufferedIn));

            return clip; 
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
