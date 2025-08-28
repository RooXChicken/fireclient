package org.loveroo.fireclient.mixin.modules.particles;

import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.ParticlesModule;
import org.lwjgl.system.CallbackI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Mixin(ParticleManager.class)
public class HideParticlesMixin {
    
    @Inject(method = "createParticle", at = @At("HEAD"), cancellable = true)
    private void hideParticles(ParticleEffect particle, double x, double y, double z, double velX, double velY, double velZ, CallbackInfoReturnable<Particle> info) {
        if(!isHidden(particle.getType())) {
            return;
        }

        info.setReturnValue(null);
    }

    @Inject(method = "addBlockBreakParticles", at = @At("HEAD"), cancellable = true)
    private void hideBlockBreak(BlockPos pos, BlockState direction, CallbackInfo info) {
        if(!isHidden(ParticleTypes.BLOCK)) {
            return;
        }

        info.cancel();
    }

    @Inject(method = "addBlockBreakingParticles", at = @At("HEAD"), cancellable = true)
    private void hideBlockCrumble(BlockPos pos, Direction direction, CallbackInfo info) {
        if(!isHidden(ParticleTypes.BLOCK_CRUMBLE)) {
            return;
        }

        info.cancel();
    }

    @Unique
    private boolean isHidden(ParticleType<?> type) {
        var particles = (ParticlesModule) FireClientside.getModule("particles");
        if(particles == null || !particles.getData().isEnabled()) {
            return false;
        }

        return particles.isHidden(type);
    }
}
