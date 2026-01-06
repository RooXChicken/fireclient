package org.loveroo.fireclient.mixin.modules.fullbright;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.LightmapTextureManager;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.FullbrightModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightmapTextureManager.class)
public abstract class EnableFullbrightMixin {

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;"))
    private Object increaseGamma(SimpleOption instance) {
        if(instance != MinecraftClient.getInstance().options.getGamma()) {
            return instance.getValue();
        }

        var fullbright = (FullbrightModule) FireClientside.getModule("fullbright");
        if(fullbright == null || !fullbright.getData().isEnabled()) {
            return instance.getValue();
        }

        return 1000.0;
    }
}
