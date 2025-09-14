package org.loveroo.fireclient.mixin.modules.zoom;

import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.PerspectiveModule;
import org.loveroo.fireclient.modules.ZoomModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;

@Mixin(Mouse.class)
public abstract class ZoomInMixin {

    @Inject(method = "onMouseScroll(JDD)V", at = @At("HEAD"), cancellable = true)
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo info) {
        var client = MinecraftClient.getInstance();
        if(window != client.getWindow().getHandle() || client.player == null || client.currentScreen != null) {
            return;
        }

        var zoom = (ZoomModule)FireClientside.getModule("zoom");
        if(zoom == null || !zoom.getData().isEnabled() || !zoom.isZooming()) {
            return;
        }
        
        var perspective = (PerspectiveModule) FireClientside.getModule("perspective");
        if(perspective != null && perspective.isUsing() && perspective.isZoomEnabled()) {
            return;
        }

        zoom.incrementZoom((int)Math.round(vertical) * -1);
        info.cancel();
    }
}