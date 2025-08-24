package org.loveroo.fireclient.mixin.settings;

import net.minecraft.client.particle.ParticleManager;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleS2CPacket.class)
public abstract class LimitParticleCountMixin {

    @Shadow @Final
    private int count;

    @Unique
    private final int cap = 16384/2;

    @Inject(method = "getCount", at = @At("HEAD"), cancellable = true)
    private void capCount(CallbackInfoReturnable<Integer> info) {
        if(FireClientside.getSetting(FireClientOption.CAP_PARTICLE_COUNT) == 0) {
            return;
        }

        info.setReturnValue(Math.min(count, cap));
    }
}
