package org.loveroo.fireclient.mixin;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.keybind.Key.KeyType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {

    @Shadow @Final
    private Minecraft minecraft;

    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    private void onKey(long handle, int action, KeyEvent event, CallbackInfo info) {
        if (handle == minecraft.getWindow().handle()) {
            var status = FireClientside.getKeybindManager().onKey(KeyType.KEY_CODE, event.key(), event.scancode(), action, event.modifiers());

            if(!status) {
                info.cancel();
            }
        }
    }
}
