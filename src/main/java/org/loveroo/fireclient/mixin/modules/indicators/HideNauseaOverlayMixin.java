package org.loveroo.fireclient.mixin.modules.indicators;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.gui.hud.InGameHud;

@Mixin(InGameHud.class)
public abstract class HideNauseaOverlayMixin {

    // @Shadow @Final
    // public static Identifier NAUSEA_TEXTURE;

    // @Inject(method = "renderNauseaOverlay", at = @At("HEAD"), cancellable = true)
    // private void hideNausea(DrawContext context, float nauseaStrength, CallbackInfo info) {
    //     var nauseaIndicator = (NauseaIndicator) FireClientside.getModule("indicator_nausea");
    //     if(nauseaIndicator == null || nauseaIndicator.doesShowOverlay()) {
    //         return;
    //     }

    //     info.cancel();
    // }

    // @Inject(method = "renderOverlay", at = @At("HEAD"), cancellable = true)
    // private void hideNausea(DrawContext context, Identifier texture, float opacity, CallbackInfo info) {
    //     if(texture != NAUSEA_TEXTURE) {
    //         return;
    //     }

    //     var nauseaIndicator = (NauseaIndicator) FireClientside.getModule("indicator_nausea");
    //     if(nauseaIndicator == null || nauseaIndicator.doesShowOverlay()) {
    //         return;
    //     }

    //     info.cancel();
    // }
}
