package org.loveroo.fireclient.mixin.modules.indicators;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.Identifier;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.indicators.FrostIndicator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class HideFrostOverlayMixin {

    @Shadow @Final
    private static Identifier POWDER_SNOW_OUTLINE_LOCATION;

    @Inject(method = "extractTextureOverlay", at = @At("HEAD"), cancellable = true)
    private void hideFrost(GuiGraphicsExtractor graphics, Identifier texture, float alpha, CallbackInfo info) {
        if(texture != POWDER_SNOW_OUTLINE_LOCATION) {
            return;
        }

        var frostIndicator = (FrostIndicator) FireClientside.getModule("indicator_frost");
        if(frostIndicator == null || frostIndicator.doesShowOverlay()) {
            return;
        }

        info.cancel();
    }
}
