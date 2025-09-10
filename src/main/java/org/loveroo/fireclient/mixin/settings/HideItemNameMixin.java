package org.loveroo.fireclient.mixin.settings;

import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;

@Mixin(InGameHud.class)
public class HideItemNameMixin {
    
    @Inject(method = "renderHeldItemTooltip", at = @At("HEAD"), cancellable = true)
    private void hideTooltip(DrawContext context, CallbackInfo info) {
        if(FireClientside.getSetting(FireClientOption.HIDE_ITEM_NAMES) == 0) {
            return;
        }

        info.cancel();
    }
}
