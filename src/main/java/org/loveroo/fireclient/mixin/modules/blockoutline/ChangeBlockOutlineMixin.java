package org.loveroo.fireclient.mixin.modules.blockoutline;

import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.BlockOutlineModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

@Mixin(WorldRenderer.class)
public class ChangeBlockOutlineMixin {

    @Unique
    private VertexConsumer consumer;

    @Inject(method = "drawBlockOutline", at = @At("HEAD"))
    private void getConsumer(MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos pos, BlockState state, CallbackInfo info) {
        consumer = vertexConsumer;
    }

    // @ModifyVariable(method = "drawBlockOutline", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    // private int changeColor(int original) {
    //     var outline = (BlockOutlineModule) FireClientside.getModule("block_outline");
    //     if(outline == null || !outline.getData().isEnabled()) {
    //         return original;
    //     }

    //     return outline.getOutline();
    // }

    @ModifyVariable(method = "drawBlockOutline", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private VertexConsumer changeColor(VertexConsumer original) {
        var outline = (BlockOutlineModule) FireClientside.getModule("block_outline");
        if(outline == null || !outline.getData().isEnabled()) {
            return original;
        }

        // TODO fix
        // var layer = (outline.isThick()) ? RenderLayer.getLines() : RenderLayer.getLines();
        // return consumer.getBuffer(layer);
        return consumer;
    }
}
