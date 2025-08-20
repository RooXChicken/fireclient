package org.loveroo.fireclient.mixin.modules.blockoutline;

import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.BlockOutlineModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class ChangeBlockOutlineMixin {

    @Unique
    private VertexConsumerProvider.Immediate consumer;

    @Inject(method = "renderTargetBlockOutline", at = @At("HEAD"))
    private void getConsumer(Camera camera, VertexConsumerProvider.Immediate vertexConsumers, MatrixStack matrices, boolean translucent, CallbackInfo info) {
        consumer = vertexConsumers;
    }

    @ModifyVariable(method = "drawBlockOutline", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private int changeColor(int original) {
        var outline = (BlockOutlineModule) FireClientside.getModule("block_outline");
        if(outline == null || !outline.getData().isEnabled()) {
            return original;
        }

        return outline.getOutline();
    }

    @ModifyVariable(method = "drawBlockOutline", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private VertexConsumer changeColor(VertexConsumer original) {
        var outline = (BlockOutlineModule) FireClientside.getModule("block_outline");
        if(outline == null || !outline.getData().isEnabled()) {
            return original;
        }

        var layer = (outline.isThick()) ? RenderLayer.getSecondaryBlockOutline() : RenderLayer.getLines();
        return consumer.getBuffer(layer);
    }
}
