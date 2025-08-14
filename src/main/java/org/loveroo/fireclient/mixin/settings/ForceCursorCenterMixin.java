package org.loveroo.fireclient.mixin.settings;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class ForceCursorCenterMixin {

    private boolean center = false;
/*
GLFW.glfwSetCursorPos(client.getWindow().getHandle(), 100, client.getWindow().getHeight()/2.0);
            FireClient.LOGGER.info("save me");
 */
    @Inject(method = "setScreen(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    public void setScreen(@Nullable Screen screen, CallbackInfo info) {
//        if(FireClientside.getSetting(FireClientOption.FORCE_CENTER_CURSOR) == 0) {
//            return;
//        }
//
//        MinecraftClient client = (MinecraftClient)((Object)this);
//        if(client.currentScreen == null) {
//            center = true;
//        }
    }

    @Inject(method = "setScreen(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("TAIL"))
    public void setScreenEnd(@Nullable Screen screen, CallbackInfo info) {
//        if(FireClientside.getSetting(FireClientOption.FORCE_CENTER_CURSOR) == 0) {
//            return;
//        }
//
//        MinecraftClient client = (MinecraftClient)((Object)this);
//        if(center) {
//            center = false;
//            GLFW.glfwSetCursorPos(client.getWindow().getHandle(), client.getWindow().getWidth()/2.0, client.getWindow().getHeight()/2.0);
//        }
    }
}
