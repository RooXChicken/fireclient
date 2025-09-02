package org.loveroo.fireclient.mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;

import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.keybind.Key.KeyType;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin {

    @Shadow @Final
    private MinecraftClient client;

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void onKey(long window, int button, int action, int mods, CallbackInfo info) {
        if (window == client.getWindow().getHandle()) {
            var key = GLFW.GLFW_MOUSE_BUTTON_1 + button;
            var status = FireClientside.getKeybindManager().onKey(KeyType.MOUSE, key, -1, action, mods);

            if(!status) {
                info.cancel();
            }
        }
    }
}
