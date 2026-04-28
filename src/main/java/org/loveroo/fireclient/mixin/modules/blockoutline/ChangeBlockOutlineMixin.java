package org.loveroo.fireclient.mixin.modules.blockoutline;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.BlockOutlineModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class ChangeBlockOutlineMixin {

    @Unique
    private MultiBufferSource.BufferSource consumer;

    @Inject(method = "renderBlockOutline", at = @At("HEAD"))
    private void getConsumer(MultiBufferSource.BufferSource bufferSource, PoseStack poseStack, boolean onlyTranslucentBlocks, net.minecraft.client.renderer.state.level.LevelRenderState levelRenderState, CallbackInfo ci) {
        consumer = bufferSource;
    }

    @ModifyVariable(method = "renderHitOutline", at = @At("HEAD"), argsOnly = true, name = "color")
    private int changeColor(int color) {
        var outline = (BlockOutlineModule) FireClientside.getModule("block_outline");
        if(outline == null || !outline.getData().isEnabled()) {
            return color;
        }

        return outline.getOutline();
    }

    @ModifyVariable(method = "renderHitOutline", at = @At("HEAD"), argsOnly = true, name = "builder")
    private VertexConsumer changeColor(VertexConsumer builder) {
        var outline = (BlockOutlineModule) FireClientside.getModule("block_outline");
        if(outline == null || !outline.getData().isEnabled()) {
            return builder;
        }

        var layer = (outline.isThick()) ? RenderTypes.secondaryBlockOutline() : RenderTypes.lines();
        return consumer.getBuffer(layer);
    }
}
