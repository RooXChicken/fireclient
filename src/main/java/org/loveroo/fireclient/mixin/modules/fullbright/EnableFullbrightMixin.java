package org.loveroo.fireclient.mixin.modules.fullbright;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.FullbrightModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightmapRenderStateExtractor.class)
public abstract class EnableFullbrightMixin {

    @WrapOperation(method = "extract", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;", ordinal = 1))
    private Object increaseGamma(OptionInstance<?> instance, Operation<Object> original) {
        if(instance != Minecraft.getInstance().options.gamma()) {
            return instance.get();
        }

        var fullbright = (FullbrightModule) FireClientside.getModule("fullbright");
        if(fullbright == null || !fullbright.getData().isEnabled()) {
            return instance.get();
        }

        return 1000.0;
    }
}
