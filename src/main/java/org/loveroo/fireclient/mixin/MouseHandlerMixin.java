package org.loveroo.fireclient.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;

import net.minecraft.client.input.MouseButtonInfo;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.keybind.Key.KeyType;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {

    @Shadow @Final
    private Minecraft minecraft;

    @Inject(method = "onButton", at = @At("HEAD"), cancellable = true)
    private void onKey(long handle, MouseButtonInfo rawButtonInfo, int action, CallbackInfo info) {
        if (handle == minecraft.getWindow().handle()) {
            var key = GLFW.GLFW_MOUSE_BUTTON_1 + rawButtonInfo.button();
            var status = FireClientside.getKeybindManager().onKey(KeyType.MOUSE, key, -1, action, rawButtonInfo.modifiers());

            if(!status) {
                info.cancel();
            }
        }
    }
}
