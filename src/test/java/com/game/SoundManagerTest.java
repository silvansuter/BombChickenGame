package com.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.sound.sampled.Clip;
import java.util.Map;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SoundManagerTest {

    private SoundManager soundManager;
    private Clip mockClip;

    @BeforeEach
    void setUp() throws Exception {
        soundManager = new SoundManager(false);
        mockClip = mock(Clip.class);

        // Use reflection to access the private soundClips field and set it to mockMap
        Field soundClipsField = SoundManager.class.getDeclaredField("soundClips");
        soundClipsField.setAccessible(true);

        Map<String, Clip> mockMap = mock(Map.class);
        when(mockMap.get(anyString())).thenReturn(mockClip);
        soundClipsField.set(soundManager, mockMap); // Set the field to the mockMap
}

    @Test
    void loadAllSounds_ShouldNotBeEmpty() {
        soundManager.loadAllSounds();
        assertFalse(soundManager.getSoundClips().isEmpty());
    }

    @Test
    void playSound_WhenNotMuted_ShouldPlaySound() {
        String soundFileName = "BombComing.wav";
        soundManager.playSound(soundFileName);
        verify(mockClip, times(1)).start();
    }

    @Test
    void playSound_WhenMuted_ShouldNotPlaySound() {
        soundManager.setMute(true);
        String soundFileName = "BombComing.wav";
        soundManager.playSound(soundFileName);
        verify(mockClip, never()).start();
    }

    @Test
    void setMute_ShouldUpdateMuteSounds() {
        soundManager.setMute(true);
        assertTrue(soundManager.getMuteSounds());

        soundManager.setMute(false);
        assertFalse(soundManager.getMuteSounds());
    }
}
