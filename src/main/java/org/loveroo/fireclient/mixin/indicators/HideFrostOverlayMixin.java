package org.loveroo.fireclient.mixin.indicators;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.indicators.FrostIndicator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class HideFrostOverlayMixin {

    @Shadow @Final
    private static Identifier POWDER_SNOW_OUTLINE;

    @Inject(method = "renderOverlay", at = @At("HEAD"), cancellable = true)
    private void hideFrost(DrawContext context, Identifier texture, float opacity, CallbackInfo info) {
        if(texture != POWDER_SNOW_OUTLINE) {
            return;
        }

        var frostIndicator = (FrostIndicator) FireClientside.getModule("indicator_frost");
        if(frostIndicator == null || frostIndicator.doesShowOverlay()) {
            return;
        }

        info.cancel();
    }
}
