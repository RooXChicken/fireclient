package org.loveroo.fireclient.mixin.modules.reachdisplay;

import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.ReachDisplayModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

@Mixin(MultiPlayerGameMode.class)
public abstract class GetReachMixin {

    @Inject(method = "attack", at = @At("HEAD"))
    private void getReach(Player player, Entity entity, CallbackInfo info) {
        var reachDisplay = (ReachDisplayModule) FireClientside.getModule("reach_display");
        if(reachDisplay == null || !reachDisplay.isHitOnly()) {
            return;
        }

        var client = Minecraft.getInstance();
        reachDisplay.calculateReach(client.hitResult);
    }
    
}
