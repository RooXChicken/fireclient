package org.loveroo.fireclient.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.FlightModificationModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
abstract class ModifyFlightMoveSpeedMixin {

    @Redirect(method = "getOffGroundSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerAbilities;getFlySpeed()F"))
    private float modifySpeed(PlayerAbilities abilities) {
        var flight = (FlightModificationModule) FireClientside.getModule("flight_modification");
        if(flight == null || !flight.getData().isEnabled()) {
            return abilities.getFlySpeed();
        }

        return flight.getSpeed();
    }
}

@Mixin(ClientPlayerEntity.class)
abstract class ModifyFlightVerticalSpeedMixin {

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerAbilities;getFlySpeed()F"))
    private float modifySpeed(PlayerAbilities abilities) {
        var flight = (FlightModificationModule) FireClientside.getModule("flight_modification");
        if(flight == null || !flight.getData().isEnabled()) {
            return abilities.getFlySpeed();
        }

        return flight.getSpeed();
    }
}

