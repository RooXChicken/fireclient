package org.loveroo.fireclient.mixin.modules.reachdisplay;

import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.ReachDisplayModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class GetReachMixin {

    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void getReach(PlayerEntity player, Entity target, CallbackInfo info) {
        var reachDisplay = (ReachDisplayModule) FireClientside.getModule("reach_display");
        if(reachDisplay == null || !reachDisplay.isHitOnly()) {
            return;
        }

        var client = MinecraftClient.getInstance();
        reachDisplay.calculateReach(client.crosshairTarget);
    }
    
}
