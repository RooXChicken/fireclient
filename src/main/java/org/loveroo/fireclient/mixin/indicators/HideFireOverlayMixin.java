package org.loveroo.fireclient.mixin.indicators;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.indicators.FireIndicator;
import org.loveroo.fireclient.modules.indicators.FrostIndicator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameOverlayRenderer.class)
public abstract class HideFireOverlayMixin {

    @Inject(method = "renderFireOverlay", at = @At("HEAD"), cancellable = true)
    private static void hideFire(MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo info) {
        var fireIndicator = (FireIndicator) FireClientside.getModule("indicator_fire");
        if(fireIndicator == null || fireIndicator.doesShowOverlay()) {
            return;
        }

        info.cancel();
    }
}
