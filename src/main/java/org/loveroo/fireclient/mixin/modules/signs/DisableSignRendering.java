package org.loveroo.fireclient.mixin.modules.signs;

import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.SignModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(SignBlockEntityRenderer.class)
public class DisableSignRendering {

    @Inject(method = "render(Lnet/minecraft/block/entity/SignBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At("HEAD"), cancellable = true)
    private void disableRendering(SignBlockEntity signBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo info) {
        var signs = (SignModule) FireClientside.getModule("sign");
        if(signs == null || !signs.isRenderingDisabled()) {
            return;
        }

        info.cancel();
    }
}
