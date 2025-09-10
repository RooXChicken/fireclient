package org.loveroo.fireclient.mixin.modules.zoom;

import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.ZoomModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;

@Mixin(SimpleOption.class)
public abstract class ZoomLevelMixin<T> {

    @Shadow
    private T value;

    @Inject(method = "getValue", at = @At("HEAD"), cancellable = true)
    private void modifyFov(CallbackInfoReturnable<Object> info) {
        var client = MinecraftClient.getInstance();
        if((Object)this != client.options.getFov()) {
            return;
        }
        
        var zoom = (ZoomModule) FireClientside.getModule("zoom");
        if(zoom == null || !zoom.isZooming()) {
            return;
        }

        info.setReturnValue(Math.clamp(zoom.getZoomLevel(), 3, (Integer)value));
    }
}
