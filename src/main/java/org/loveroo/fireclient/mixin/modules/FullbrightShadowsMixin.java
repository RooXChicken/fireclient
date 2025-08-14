package org.loveroo.fireclient.mixin.modules;

import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.world.dimension.DimensionType;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.ShadowModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightmapTextureManager.class)
public class FullbrightShadowsMixin {

    @Inject(method = "getBrightness(Lnet/minecraft/world/dimension/DimensionType;I)F", at = @At("HEAD"), cancellable = true)
    private static void setShadowBrightness(DimensionType type, int lightLevel, CallbackInfoReturnable<Float> info) {
        var shadow = (ShadowModule) FireClientside.getModule("shadow");
        if(shadow == null || !shadow.drawingShadow) {
            return;
        }

        shadow.drawingShadow = false;

        if(!shadow.isFullbright()) {
            return;
        }

        info.setReturnValue(1.0f);
    }
}
