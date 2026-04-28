package org.loveroo.fireclient.mixin.modules.mutesounds;

import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.SoundsModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundEvent;

@Mixin(ClientLevel.class)
public abstract class MuteSoundsMixin {

    @ModifyVariable(method = "playSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZJ)V", at = @At("HEAD"), argsOnly = true, name = "volume")
    private float decreaseVolume(float volume, @Local(argsOnly = true, name = "sound") SoundEvent sound) {
        var muteSounds = (SoundsModule) FireClientside.getModule("sounds");
        if(muteSounds == null || !muteSounds.getData().isEnabled()) {
            return volume;
        }

        var volumeMult = muteSounds.getVolume(sound);
        return (float)(volume * volumeMult);
    }
}
