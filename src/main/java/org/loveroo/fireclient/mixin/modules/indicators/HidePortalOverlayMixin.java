package org.loveroo.fireclient.mixin.modules.indicators;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Gui;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.indicators.PortalIndicator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class HidePortalOverlayMixin {

    @Inject(method = "extractPortalOverlay", at = @At("HEAD"), cancellable = true)
    private void hidePortal(GuiGraphicsExtractor graphics, float alpha, CallbackInfo info) {
        var portalIndicator = (PortalIndicator) FireClientside.getModule("indicator_portal");
        if(portalIndicator == null || portalIndicator.doesShowOverlay()) {
            return;
        }

        info.cancel();
    }
}
