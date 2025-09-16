package org.loveroo.fireclient.mixin.modules.indicators;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.gui.hud.InGameOverlayRenderer;

@Mixin(InGameOverlayRenderer.class)
public abstract class HideFireOverlayMixin {

    // @Inject(method = "renderFireOverlay", at = @At("HEAD"), cancellable = true)
    // private static void hideFire(MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo info) {
    //     var fireIndicator = (FireIndicator) FireClientside.getModule("indicator_fire");
    //     if(fireIndicator == null || fireIndicator.doesShowOverlay()) {
    //         return;
    //     }

    //     info.cancel();
    // }
}
