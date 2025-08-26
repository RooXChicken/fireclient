package org.loveroo.fireclient.mixin.modules.shadows;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionType;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.ShadowModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class RenderShadowMixin {

    @Unique
    private static double floorDistance;

    @Unique
    private static float distanceToCamera;

    @ModifyVariable(method = "render(Lnet/minecraft/client/render/entity/state/EntityRenderState;DDDLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V", at = @At("STORE"), ordinal = 6)
    private double modifyDistance(double original) {
        var shadow = (ShadowModule) FireClientside.getModule("shadow");
        if(shadow == null || shadow.isDistanceEffect()) {
            distanceToCamera = (1.0f - (float)original / 256.0f);
            return original;
        }

        distanceToCamera = 1.0f;
        return 0.0;
    }

    @Inject(method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;render(Lnet/minecraft/client/render/entity/state/EntityRenderState;DDDLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V"))
    private void getFloorDistance(CallbackInfo info, @Local(ordinal = 0) EntityRenderState renderState, @Local(ordinal = 0, argsOnly = true) Entity entity) {
        var shadow = (ShadowModule) FireClientside.getModule("shadow");
        if(shadow == null || !shadow.isIncreaseHeight()) {
            return;
        }

        var pos = new Vec3d(renderState.x, renderState.y, renderState.z);
        var rayContext = new RaycastContext(pos, pos.subtract(0, 20, 0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity);
        var ray = entity.getWorld().raycast(rayContext);

        floorDistance = pos.y - (ray.getPos().getY() + 1);
    }

    @Inject(method = "renderShadow", at = @At("HEAD"))
    private static void renderShadow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, EntityRenderState renderState, float opacity, WorldView world, float radius, CallbackInfo ci) {
        ShadowModule.drawingShadow = true;

        var shadow = (ShadowModule) FireClientside.getModule("shadow");
        if(shadow == null || !shadow.isIncreaseHeight()) {
            return;
        }

        renderState.y -= floorDistance;
    }

    @Inject(method = "renderShadow", at = @At("TAIL"))
    private static void stopDrawing(MatrixStack matrices, VertexConsumerProvider vertexConsumers, EntityRenderState renderState, float opacity, WorldView world, float radius, CallbackInfo ci) {
        ShadowModule.drawingShadow = false;
    }

    @Inject(method = "renderShadow", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;peek()Lnet/minecraft/client/util/math/MatrixStack$Entry;", shift = At.Shift.AFTER))
    private static void resetRenderState(MatrixStack matrices, VertexConsumerProvider vertexConsumers, EntityRenderState renderState, float opacity, WorldView world, float radius, CallbackInfo ci) {
        var shadow = (ShadowModule) FireClientside.getModule("shadow");
        if(shadow == null || !shadow.isIncreaseHeight()) {
            return;
        }

        renderState.y += floorDistance;
    }

    @ModifyVariable(method = "renderShadowPart", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private static float modifyShadowOpacity(float original) {
        var shadow = (ShadowModule) FireClientside.getModule("shadow");
        if(shadow == null || !shadow.isIncreaseHeight()) {
            return original;
        }

        var distance = 1.0 - (floorDistance/20.0);
        return (float)(floorDistance/2 + distance) * distanceToCamera;
    }
}
