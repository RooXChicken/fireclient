package org.loveroo.fireclient.mixin.settings;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class CleanViewMixin {

    @Inject(method = "tickEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SynchedEntityData;get(Lnet/minecraft/network/syncher/EntityDataAccessor;)Ljava/lang/Object;"), cancellable = true)
    private void preventParticles(CallbackInfo info) {
        if(FireClientside.getSetting(FireClientOption.CLEARVIEW) == 0 || Minecraft.getInstance().player != (Object)this) {
            return;
        }

        info.cancel();
    }
}