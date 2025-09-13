package org.loveroo.fireclient.mixin.modules.mutesounds;

import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.SoundsModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundEvent;

@Mixin(ClientWorld.class)
public abstract class MuteSoundsMixin {

    @ModifyVariable(method = "playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZJ)V", at = @At("HEAD"), ordinal = 0)
    private float decreaseVolume(float volume, @Local(ordinal = 0, argsOnly = true) SoundEvent event) {
        var muteSounds = (SoundsModule) FireClientside.getModule("sounds");
        if(muteSounds == null || !muteSounds.getData().isEnabled()) {
            return volume;
        }

        var volumeMult = muteSounds.getVolume(event);
        return (float)(volume * volumeMult);
    }
}
