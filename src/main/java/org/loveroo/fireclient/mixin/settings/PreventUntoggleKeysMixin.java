package org.loveroo.fireclient.mixin.settings;

import net.minecraft.client.KeyMapping;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyMapping.class)
public abstract class PreventUntoggleKeysMixin {

    @Inject(method = "resetToggleKeys()V", at = @At("HEAD"), cancellable = true)
    private static void preventUntoggle(CallbackInfo info) {
        if(FireClientside.getSetting(FireClientOption.PREVENT_UNTOGGLE_STICKY) == 0) {
            return;
        }

        info.cancel();
    }
}
