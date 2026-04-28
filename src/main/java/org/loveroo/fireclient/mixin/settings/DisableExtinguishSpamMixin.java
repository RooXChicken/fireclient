package org.loveroo.fireclient.mixin.settings;

import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

@Mixin(ClientLevel.class)
public abstract class DisableExtinguishSpamMixin {

    @Unique
    private long lastExtinguish = 0;

    @Inject(method = "playSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZJ)V", at = @At("HEAD"), cancellable = true)
    private void stopSpam(double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay, long seed, CallbackInfo info) {
        if(sound != SoundEvents.GENERIC_EXTINGUISH_FIRE || FireClientside.getSetting(FireClientOption.EXTINGUISH_FIX) == 0) {
            return;
        }

        var cancel = false;

        var client = Minecraft.getInstance();
        if(client.player == null) {
            return;
        }

        var time = client.player.level().getGameTime();
        var difference = time - lastExtinguish;

        if(difference < 10) {
            cancel = true;
        }

        lastExtinguish = time;

        if(cancel) {
            info.cancel();
        }
    }
}
