package org.loveroo.fireclient.mixin.modules.fullbright;

import net.minecraft.client.render.LightmapTextureManager;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.FullbrightModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LightmapTextureManager.class)
public abstract class EnableFullbrightMixin {

    @ModifyVariable(method = "update", at = @At("STORE"), ordinal = 10)
    private float increaseGamma(float original) {
        var fullbright = (FullbrightModule) FireClientside.getModule("fullbright");
        if(fullbright == null || !fullbright.getData().isEnabled()) {
            return original;
        }

        return 1000.0f;
    }
}
