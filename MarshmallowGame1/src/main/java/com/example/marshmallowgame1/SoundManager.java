package com.example.marshmallowgame1;

import javafx.scene.media.AudioClip;

/**
 * Vastaa pelin äänistä.
 */
public class SoundManager {

    private AudioClip fireSound;
    private boolean soundOn = true;

    public SoundManager() {
        try {
            var url = getClass().getResource("/aani/copyright-free-fire-crackling-427409.wav");
            if (url != null) {
                fireSound = new AudioClip(url.toExternalForm());
                fireSound.setCycleCount(AudioClip.INDEFINITE);
                fireSound.setVolume(0.4);
            } else {
                System.out.println("Äänitiedostoa ei löytynyt");
            }
        } catch (Exception e) {
            System.out.println("Äänen lataus epäonnistui");
        }
    }

    public void playFire() {
        if (!soundOn) return;

        if (fireSound != null) {
            fireSound.play();
        }
    }

    public void stopFire() {
        if (fireSound != null) {
            fireSound.stop();
        }
    }

    public void toggleSound() {
        soundOn = !soundOn;

        if (!soundOn) {
            stopFire();
        }
    }

    public boolean isSoundOn() {
        return soundOn;
    }
}