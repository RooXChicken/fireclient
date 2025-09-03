package org.loveroo.fireclient.mixin.modules.tps;

import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.TPSModule;
import org.loveroo.fireclient.screen.config.FireClientSettingsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

@Mixin(ClientPlayNetworkHandler.class)
public class UpdateTpsMixin {

    // @Unique
    // private long oldSystemTime = -1;

    // @Unique
    // private long oldTime = -1;

    @Inject(method = "onWorldTimeUpdate", at = @At("TAIL"))
    private void calculateTps(WorldTimeUpdateS2CPacket packet, CallbackInfo info) {
        var tps = (TPSModule) FireClientside.getModule("tps_display");
        if(tps == null) {
            return;
        }

        // if(oldSystemTime == -1) {
        //     oldSystemTime = System.currentTimeMillis();
        //     oldTime = packet.time();

        //     return;
        // }

        // var newSystemTime = System.currentTimeMillis();
        // var newTime = packet.time();

        // var timeDiff = (newTime - oldTime) / 20.0;
        // var systemTimeDiff = 1000.0 / Math.max(1, newSystemTime - oldSystemTime);

        // oldSystemTime = newSystemTime;
        // oldTime = newTime;

        // tps.setTps((systemTimeDiff * 20.0) * timeDiff);
        tps.setTps();
    }
}
