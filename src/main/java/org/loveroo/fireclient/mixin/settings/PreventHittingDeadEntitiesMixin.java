package org.loveroo.fireclient.mixin.settings;

import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntity.class)
public abstract class PreventHittingDeadEntitiesMixin {

    @Shadow
    public abstract boolean isDead();

    @Inject(method = "canHit", at = @At("RETURN"), cancellable = true)
    private void dontHitDead(CallbackInfoReturnable<Boolean> info) {
        if(FireClientside.getSetting(FireClientOption.DONT_HIT_DEAD) == 0) {
            return;
        }

        info.setReturnValue(info.getReturnValue() && !isDead());
    }
    
}
