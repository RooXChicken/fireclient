package org.loveroo.fireclient.mixin.modules.blockoutline;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.phys.shapes.Shapes;
import org.joml.Quaternionf;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.BlockOutlineModule;
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
    private ProjectionMatrixBuffer proj = null;
    private final Projection projection = new Projection();

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;endFrame()V"))
    private void renderOutline(DeltaTracker deltaTracker, boolean advanceGameTime, CallbackInfo info) {
        var client = Minecraft.getInstance();
        if(!(client.screen instanceof ModuleConfigScreen)) {
            return;
        }

        var outline = (BlockOutlineModule) FireClientside.getModule("block_outline");
        if(outline == null || !outline.isScreenOpen()) {
            return;
        }

        var shape = Shapes.box(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5);
        final var scale = 25.0f;

        var window = Minecraft.getInstance().getWindow();
        var width = (float)(window.getWidth() / window.getGuiScale());
        var height = (float)(window.getHeight() / window.getGuiScale());

        if(proj == null) {
            proj = new ProjectionMatrixBuffer("outline_proj");
            projection.setupOrtho(0.0f, 210000.0F, width, height, true);
        }

        var slice = proj.getBuffer(projection);
        RenderSystem.setProjectionMatrix(slice, ProjectionType.ORTHOGRAPHIC);

        var matrix = new PoseStack();
        matrix.pushPose();

        rot -= deltaTracker.getGameTimeDeltaTicks()*2.0f;

        while(rot < 180) {
            rot += 360;
        }

        matrix.translate(width/2.0f, height/2.0f - 40, -11000.0F);
        matrix.scale(scale, scale, scale);
        matrix.mulPose(new Quaternionf().rotateXYZ(0.130f, (float)Math.toRadians(rot), 0.0f));

        var color = (outline.getData().isEnabled()) ? outline.getOutline() : outline.getDefaultOutline();

        var layer = (outline.getData().isEnabled() && outline.isThick()) ? RenderTypes.secondaryBlockOutline() : RenderTypes.lines();
        var vertex = Minecraft.getInstance().renderBuffers().bufferSource();

        ShapeRenderer.renderShape(matrix, vertex.getBuffer(layer), shape, 0, 0, 0, color, 1.0f);
        vertex.endLastBatch();

        matrix.popPose();
    }
}
