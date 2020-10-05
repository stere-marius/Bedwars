package ro.marius.bedwars.team.upgrade.enemy.trapinformation;

import org.bukkit.Sound;

public class SoundInformation {

    private final Sound sound;
    private final float pitch;
    private final float volume;

    public SoundInformation(Sound sound, float pitch, float volume) {
        this.sound = sound;
        this.pitch = pitch;
        this.volume = volume;
    }


    public Sound getSound() {
        return this.sound;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getVolume() {
        return this.volume;
    }

}
