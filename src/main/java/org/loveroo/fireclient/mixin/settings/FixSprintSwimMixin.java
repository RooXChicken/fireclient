package org.loveroo.fireclient.mixin.settings;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class FixSprintSwimMixin {
    
    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void resetSprint(PlayerEntity player, Entity target, CallbackInfo info) {
        MinecraftClient.getInstance().send(() -> {
            MinecraftClient.getInstance().player.setSprinting(true);
        });
    }
}
