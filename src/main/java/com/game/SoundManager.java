package com.game;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    private Map<String, Clip> soundClips;
    private boolean muteSounds;

    public SoundManager(boolean muteSounds) {
        this.soundClips = new HashMap<>();
        this.muteSounds = muteSounds;
        loadAllSounds();
    }

    public void loadAllSounds() {
        String[] soundNames = { "BombComing.wav", "BombDetonating.wav", "ChickenSound.wav", "ChickenSquashed.wav" };
        for (String soundName : soundNames) {
            loadSound(soundName);
        }
    }

    private void loadSound(String soundFileName) {
        try {
            URL soundURL = getClass().getResource("/sounds/" + soundFileName);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            soundClips.put(soundFileName, clip);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void playSound(String soundFileName) {
        if (muteSounds) {
            return;
        }
        Clip clip = soundClips.get(soundFileName);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0); // rewind to the beginning
            clip.start();
        }
    }

    public void setMute(boolean mute) {
        this.muteSounds = mute;
    }

    public Map<String, Clip> getSoundClips() {
        return soundClips;
    }

    public boolean getMuteSounds() {
        return muteSounds;
    }
}
