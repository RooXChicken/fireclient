package org.loveroo.fireclient.mixin.modules.scrollclick;

import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.PerspectiveModule;
import org.loveroo.fireclient.modules.ScrollClickModule;
import org.loveroo.fireclient.modules.ZoomModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;

@Mixin(MouseHandler.class)
public abstract class ScrollClickMixin {

    @Inject(method = "onScroll(JDD)V", at = @At("HEAD"), cancellable = true)
    private void onMouseScroll(long handle, double xoffset, double yoffset, CallbackInfo info) {
        var client = Minecraft.getInstance();
        if(handle != client.getWindow().handle() || client.player == null || client.screen != null) {
            return;
        }

        var scrollClick = (ScrollClickModule)FireClientside.getModule("scroll_click");
        if(scrollClick == null || !scrollClick.getData().isEnabled()) {
            return;
        }

        var perspective = (PerspectiveModule) FireClientside.getModule("perspective");
        if(perspective != null && perspective.isUsing() && perspective.isZoomEnabled()) {
            return;
        }

        var zoom = (ZoomModule) FireClientside.getModule("zoom");
        if(zoom != null && zoom.isZooming() && zoom.doesScrollToZoom()) {
            return;
        }

        scrollClick.incrementClicks(yoffset);
        info.cancel();
    }
}