package org.loveroo.fireclient.mixin.settings;

import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Gui;

@Mixin(Gui.class)
public abstract class HideItemNameMixin {
    
    @Inject(method = "extractSelectedItemName", at = @At("HEAD"), cancellable = true)
    private void hideTooltip(GuiGraphicsExtractor graphics, CallbackInfo info) {
        if(FireClientside.getSetting(FireClientOption.HIDE_ITEM_NAMES) == 0) {
            return;
        }

        info.cancel();
    }
}
