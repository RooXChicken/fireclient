package org.loveroo.fireclient.mixin.modules.blockoutline;

import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.shape.VoxelShapes;
import org.joml.Quaternionf;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.BlockOutlineModule;
import org.loveroo.fireclient.screen.config.FireClientSettingsScreen;
import org.loveroo.fireclient.screen.config.ModuleConfigScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class RenderPreviewMixin {

    @Unique
    private float rot = 180.0f;

    @Unique
    private ProjectionMatrix2 proj = null;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;incrementFrame()V"))
    private void renderOutline(RenderTickCounter tickCounter, boolean tick, CallbackInfo info) {
        var client = MinecraftClient.getInstance();
        if(!(client.currentScreen instanceof ModuleConfigScreen)) {
            return;
        }

        var outline = (BlockOutlineModule) FireClientside.getModule("block_outline");
        if(outline == null || !outline.isScreenOpen()) {
            return;
        }

        var shape = VoxelShapes.cuboid(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5);
        final var scale = 25.0f;

        var window = MinecraftClient.getInstance().getWindow();

        if(proj == null) {
            proj = new ProjectionMatrix2("outline_proj", 0.0f, 210000.0F, true);
        }

        var width = (float)(window.getFramebufferWidth() / window.getScaleFactor());
        var height = (float)(window.getFramebufferHeight() / window.getScaleFactor());

        var slice = proj.set(width, height);
        RenderSystem.setProjectionMatrix(slice, ProjectionType.ORTHOGRAPHIC);

        var matrix = new MatrixStack();
        matrix.push();

        rot -= tickCounter.getDynamicDeltaTicks()*2.0f;

        while(rot < 180) {
            rot += 360;
        }

        matrix.translate(width/2.0f, height/2.0f - 40, -11000.0F);
        matrix.scale(scale, scale, scale);
        matrix.multiply(new Quaternionf().rotateXYZ(0.130f, (float)Math.toRadians(rot), 0.0f));

        var color = (outline.getData().isEnabled()) ? outline.getOutline() : outline.getDefaultOutline();

        var layer = (outline.getData().isEnabled() && outline.isThick()) ? RenderLayer.getSecondaryBlockOutline() : RenderLayer.getLines();
        var vertex = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

        VertexRendering.drawOutline(matrix, vertex.getBuffer(layer), shape, 0, 0, 0, color);
        vertex.drawCurrentLayer();

        matrix.pop();
    }
}
