package org.loveroo.fireclient.mixin.modules.indicators;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.Identifier;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.indicators.NauseaIndicator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class HideNauseaOverlayMixin {

    @Shadow @Final
    public static Identifier NAUSEA_LOCATION;

    @Inject(method = "extractConfusionOverlay", at = @At("HEAD"), cancellable = true)
    private void hideNausea(GuiGraphicsExtractor graphics, float strength, CallbackInfo info) {
        var nauseaIndicator = (NauseaIndicator) FireClientside.getModule("indicator_nausea");
        if(nauseaIndicator == null || nauseaIndicator.doesShowOverlay()) {
            return;
        }

        info.cancel();
    }

    @Inject(method = "extractTextureOverlay", at = @At("HEAD"), cancellable = true)
    private void hideNausea(GuiGraphicsExtractor graphics, Identifier texture, float alpha, CallbackInfo info) {
        if(texture != NAUSEA_LOCATION) {
            return;
        }

        var nauseaIndicator = (NauseaIndicator) FireClientside.getModule("indicator_nausea");
        if(nauseaIndicator == null || nauseaIndicator.doesShowOverlay()) {
            return;
        }

        info.cancel();
    }
}
