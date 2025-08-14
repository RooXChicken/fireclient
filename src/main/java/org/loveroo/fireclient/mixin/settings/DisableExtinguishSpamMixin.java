package org.loveroo.fireclient.mixin.settings;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class DisableExtinguishSpamMixin {

    @Unique
    private long lastExtinguish = 0;

    @Inject(method = "playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZJ)V", at = @At("HEAD"), cancellable = true)
    private void stopSpam(double x, double y, double z, SoundEvent event, SoundCategory category, float volume, float pitch, boolean useDistance, long seed, CallbackInfo info) {
        if(event != SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE || FireClientside.getSetting(FireClientOption.EXTINGUISH_FIX) == 0) {
            return;
        }

        var cancel = false;

        var client = MinecraftClient.getInstance();
        if(client.player == null) {
            return;
        }

        var difference = client.player.clientWorld.getTime() - lastExtinguish;

        if(difference < 10) {
            cancel = true;
        }

        lastExtinguish = client.player.clientWorld.getTime();

        if(cancel) {
            info.cancel();
        }
    }
}
