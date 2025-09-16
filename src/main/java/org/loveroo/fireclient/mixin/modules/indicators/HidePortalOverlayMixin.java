package org.loveroo.fireclient.mixin.modules.indicators;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.gui.hud.InGameHud;

@Mixin(InGameHud.class)
public abstract class HidePortalOverlayMixin {

    // @Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true)
    // private void hidePortal(DrawContext context, float nauseaStrength, CallbackInfo info) {
    //     var portalIndicator = (PortalIndicator) FireClientside.getModule("indicator_portal");
    //     if(portalIndicator == null || portalIndicator.doesShowOverlay()) {
    //         return;
    //     }

    //     info.cancel();
    // }
}
