package org.loveroo.fireclient.mixin.modules.flightspeed;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.FlightSpeedModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
abstract class ModifyFlightMoveSpeedMixin {

    @Redirect(method = "getFlyingSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Abilities;getFlyingSpeed()F"))
    private float modifySpeed(Abilities abilities) {
        var client = Minecraft.getInstance();
        if(client.player == null || client.player.gameMode() != GameType.CREATIVE) {
            return abilities.getFlyingSpeed();
        }

        var flight = (FlightSpeedModule) FireClientside.getModule("flight_speed");
        if(flight == null || !flight.getData().isEnabled()) {
            return abilities.getFlyingSpeed();
        }

        return flight.getSpeed();
    }
}

@Mixin(LocalPlayer.class)
abstract class ModifyFlightVerticalSpeedMixin {

    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Abilities;getFlyingSpeed()F"))
    private float modifySpeed(Abilities abilities, Operation<Float> original) {
        var client = Minecraft.getInstance();
        if(client.player == null || client.player.gameMode() != GameType.CREATIVE) {
            return original.call(abilities);
        }

        var flight = (FlightSpeedModule) FireClientside.getModule("flight_speed");
        if(flight == null || !flight.getData().isEnabled()) {
            return original.call(abilities);
        }

        return flight.getSpeed();
    }
}

