package org.loveroo.fireclient.mixin.indicators;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.indicators.NauseaIndicator;
import org.loveroo.fireclient.modules.indicators.PortalIndicator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class HidePortalOverlayMixin {

    @Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true)
    private void hidePortal(DrawContext context, float nauseaStrength, CallbackInfo info) {
        var portalIndicator = (PortalIndicator) FireClientside.getModule("indicator_portal");
        if(portalIndicator == null || portalIndicator.doesShowOverlay()) {
            return;
        }

        info.cancel();
    }
}
