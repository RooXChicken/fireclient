package org.loveroo.fireclient.mixin.settings;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.SplashOverlay;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftClient.class)
abstract class AllowReloadInputsClientMixin {

    @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;overlay:Lnet/minecraft/client/gui/screen/Overlay;"))
    private Overlay returnNoOverlay(MinecraftClient instance) {
        return RooHelper.getOverlay(instance);
    }

    @Redirect(method = "handleInputEvents", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;overlay:Lnet/minecraft/client/gui/screen/Overlay;"))
    private Overlay returnNoOverlayInput(MinecraftClient instance) {
        return RooHelper.getOverlay(instance);
    }
}

@Mixin(Mouse.class)
abstract class AllowReloadInputsMouseMixin {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getOverlay()Lnet/minecraft/client/gui/screen/Overlay;"))
    private Overlay returnNoOverlay(MinecraftClient instance) {
        return RooHelper.getOverlay(instance);
    }

    @Redirect(method = "onMouseButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getOverlay()Lnet/minecraft/client/gui/screen/Overlay;"))
    private Overlay returnNoOverlayInput(MinecraftClient instance) {
        return RooHelper.getOverlay(instance);
    }
}

@Mixin(Keyboard.class)
abstract class AllowReloadInputsKeyboardMixin {

    @Redirect(method = "onChar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getOverlay()Lnet/minecraft/client/gui/screen/Overlay;"))
    private Overlay returnNoOverlay(MinecraftClient instance) {
        return RooHelper.getOverlay(instance);
    }
}
