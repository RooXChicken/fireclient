package org.loveroo.fireclient.mixin.modules.indicators;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.gui.hud.InGameHud;

@Mixin(InGameHud.class)
public abstract class HideFrostOverlayMixin {

    // @Shadow @Final
    // private static Identifier POWDER_SNOW_OUTLINE;

    // @Inject(method = "renderOverlay", at = @At("HEAD"), cancellable = true)
    // private void hideFrost(DrawContext context, Identifier texture, float opacity, CallbackInfo info) {
    //     if(texture != POWDER_SNOW_OUTLINE) {
    //         return;
    //     }

    //     var frostIndicator = (FrostIndicator) FireClientside.getModule("indicator_frost");
    //     if(frostIndicator == null || frostIndicator.doesShowOverlay()) {
    //         return;
    //     }

    //     info.cancel();
    // }
}
