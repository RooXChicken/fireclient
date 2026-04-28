package org.loveroo.fireclient.mixin.modules.signs;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;

@Mixin(AbstractSignRenderer.class)
public class DisableSignRendering {

    // @Inject(method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/util/math/Vec3d;)V", at = @At("HEAD"), cancellable = true)
    // private void disableRendering(BlockEntity entity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos, CallbackInfo info) {
    //     var signs = (SignModule) FireClientside.getModule("sign");
    //     if(signs == null || !signs.isRenderingDisabled()) {
    //         return;
    //     }

    //     info.cancel();
    // }
}
