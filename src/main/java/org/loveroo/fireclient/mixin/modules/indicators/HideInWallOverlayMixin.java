package org.loveroo.fireclient.mixin.modules.indicators;

import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.indicators.InWallIndicator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameOverlayRenderer.class)
public abstract class HideInWallOverlayMixin {

    @Inject(method = "renderInWallOverlay", at = @At("HEAD"), cancellable = true)
    private static void hideInWall(Sprite sprite, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo info) {
        var inWallIndicator = (InWallIndicator) FireClientside.getModule("indicator_in_wall");
        if(inWallIndicator == null) {
            return;
        }

        inWallIndicator.isInWall = true;

        if(inWallIndicator.doesShowOverlay()) {
            return;
        }

        info.cancel();
    }
}