package org.loveroo.fireclient.mixin.settings;

import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

@Mixin(ClientPlayerInteractionManager.class)
abstract class FixSprintSwimMixin {
    
    @Inject(method = "attackEntity", at = @At("TAIL"))
    private void resetSprint(PlayerEntity player, Entity target, CallbackInfo info) {
        var client = MinecraftClient.getInstance();
        if(FireClientside.getSetting(FireClientOption.FIX_SPRINT_SWIM) == 0 || !client.player.isSubmergedInWater()) {
            return;
        }

        client.player.setSprinting(false);
        var sprintInvoker = (SprintPacketAccessor)(ClientPlayerEntity)client.player;
        sprintInvoker.sendSprintingPacketInvoker();
    }
}

@Mixin(ClientPlayerEntity.class)
abstract interface SprintPacketAccessor {

    @Invoker("sendSprintingPacket")
    void sendSprintingPacketInvoker();
}