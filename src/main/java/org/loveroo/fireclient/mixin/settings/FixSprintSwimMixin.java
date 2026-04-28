package org.loveroo.fireclient.mixin.settings;

import java.util.Set;

import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;

@Mixin(MultiPlayerGameMode.class)
abstract class FixSprintSwimMixin {
    
    @Unique
    private static final Set<String> minemenClubBrands = Set.of(
        "XeBungee", "ClubSpigot", "ClubPaper"
    );

    @Inject(method = "attack", at = @At("TAIL"))
    private void resetSprint(Player player, Entity target, CallbackInfo info) {
        var client = Minecraft.getInstance();
        if(FireClientside.getSetting(FireClientOption.FIX_SPRINT_SWIM) == 0 || !client.player.isUnderWater()) {
            return;
        }

        if(onMinemen()) {
            return;
        }

        client.player.setSprinting(false);
        var sprintInvoker = (SprintPacketAccessor)(LocalPlayer)client.player;
        sprintInvoker.sendSprintingPacketInvoker();
    }

    @Unique
    private boolean onMinemen() {
        var brand = RooHelper.getServerBrand();
        var isMMCBrand = minemenClubBrands.stream().anyMatch((mmcBrand) -> mmcBrand.equalsIgnoreCase(brand));

        return (isMMCBrand || RooHelper.getIp().toLowerCase().endsWith("minemen.club"));
    }
}

@Mixin(LocalPlayer.class)
abstract interface SprintPacketAccessor {

    @Invoker("sendIsSprintingIfNeeded")
    void sendSprintingPacketInvoker();
}