package org.loveroo.fireclient.mixin.indicators;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.indicators.FireIndicator;
import org.loveroo.fireclient.modules.indicators.NauseaIndicator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class HideNauseaOverlayMixin {

    @Shadow @Final
    public static Identifier NAUSEA_TEXTURE;

    @Inject(method = "renderNauseaOverlay", at = @At("HEAD"), cancellable = true)
    private void hideNausea(DrawContext context, float nauseaStrength, CallbackInfo info) {
        var nauseaIndicator = (NauseaIndicator) FireClientside.getModule("indicator_nausea");
        if(nauseaIndicator == null || nauseaIndicator.doesShowOverlay()) {
            return;
        }

        info.cancel();
    }

    @Inject(method = "renderOverlay", at = @At("HEAD"), cancellable = true)
    private void hideNausea(DrawContext context, Identifier texture, float opacity, CallbackInfo info) {
        if(texture != NAUSEA_TEXTURE) {
            return;
        }

        var nauseaIndicator = (NauseaIndicator) FireClientside.getModule("indicator_nausea");
        if(nauseaIndicator == null || nauseaIndicator.doesShowOverlay()) {
            return;
        }

        info.cancel();
    }
}
