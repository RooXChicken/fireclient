package org.loveroo.fireclient.mixin.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.ScrollClickModule;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class ScrollClickMixin {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onMouseScroll(JDD)V", at = @At("HEAD"), cancellable = true)
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo info) {
        var client = MinecraftClient.getInstance();
        if(window != client.getWindow().getHandle() || client.player == null || client.currentScreen != null) {
            return;
        }

        var scrollClick = (ScrollClickModule)FireClientside.getModule("scroll_click");
        if(scrollClick == null || !scrollClick.getData().isEnabled()) {
            return;
        }

        scrollClick.incrementClicks(vertical);
        info.cancel();
    }
}