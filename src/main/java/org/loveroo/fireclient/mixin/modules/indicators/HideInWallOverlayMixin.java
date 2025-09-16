package org.loveroo.fireclient.mixin.modules.indicators;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.gui.hud.InGameOverlayRenderer;

@Mixin(InGameOverlayRenderer.class)
public abstract class HideInWallOverlayMixin {

    // @Inject(method = "renderInWallOverlay", at = @At("HEAD"), cancellable = true)
    // private static void hideInWall(Sprite sprite, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo info) {
    //     var inWallIndicator = (InWallIndicator) FireClientside.getModule("indicator_in_wall");
    //     if(inWallIndicator == null) {
    //         return;
    //     }

    //     inWallIndicator.isInWall = true;

    //     if(inWallIndicator.doesShowOverlay()) {
    //         return;
    //     }

    //     info.cancel();
    // }
}