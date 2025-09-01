package org.loveroo.fireclient.mixin.settings;

import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.loveroo.fireclient.settings.SprintSwimFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(ClientPlayerInteractionManager.class)
abstract class FixSprintSwimMixin {
    
    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void resetSprint(PlayerEntity player, Entity target, CallbackInfo info) {
        if(FireClientside.getSetting(FireClientOption.FIX_SPRINT_SWIM) == 0) {
            return;
        }

        SprintSwimFix.preventSprint();
    }
}

@Mixin(LivingEntity.class)
abstract class PreventSprintSwinMixin {

    @Inject(method = "setSprinting", at = @At("HEAD"))
    private void preventSprint(boolean sprint, CallbackInfo info) {
        if(FireClientside.getSetting(FireClientOption.FIX_SPRINT_SWIM) == 0) {
            return;
        }

        if(SprintSwimFix.canSprint()) {
            return;
        }

        info.cancel();
    }
}