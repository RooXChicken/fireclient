package org.loveroo.fireclient.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.NametagModule;
import org.loveroo.fireclient.modules.ShadowModule;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class RenderShadowMixin<E extends Entity, S extends EntityRenderState> {

    @Shadow private World world;

    @Unique
    private static double floorDistance = 0.0;

    @ModifyVariable(method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V", at = @At("STORE"), ordinal = 6)
    private double modifyDistance(double original) {
        return 0.0;
    }

    @Inject(method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;renderShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/entity/state/EntityRenderState;FFLnet/minecraft/world/WorldView;F)V"))
    private void getFloorDistance(CallbackInfo info, @Local(ordinal = 0) S renderState, @Local(ordinal = 0, argsOnly = true) E entity) {
        var shadow = (ShadowModule) FireClientside.getModule("shadow");
        if(shadow == null || !shadow.getData().isEnabled()) {
            return;
        }

        var pos = new Vec3d(renderState.x, renderState.y, renderState.z);
        var rayContext = new RaycastContext(pos, pos.subtract(0, 20, 0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity);
        var ray = world.raycast(rayContext);

        floorDistance = pos.y - (ray.getBlockPos().getY() + 1);
    }

    @Inject(method = "renderShadow", at = @At("HEAD"))
    private static void renderShadow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, EntityRenderState renderState, float opacity, float tickDelta, WorldView world, float radius, CallbackInfo info) {
        renderState.y -= floorDistance;
    }

    @Inject(method = "renderShadow", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;peek()Lnet/minecraft/client/util/math/MatrixStack$Entry;", shift = At.Shift.AFTER))
    private static void resetRenderState(MatrixStack matrices, VertexConsumerProvider vertexConsumers, EntityRenderState renderState, float opacity, float tickDelta, WorldView world, float radius, CallbackInfo ci) {
        renderState.y += floorDistance;
    }

    @ModifyVariable(method = "renderShadowPart", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private static float moveDown(float original) {
        var shadow = (ShadowModule) FireClientside.getModule("shadow");
        if(shadow == null || !shadow.getData().isEnabled()) {
            return original;
        }

        var distance = 1.0 - (floorDistance/20.0);
        return (float)(floorDistance/2 + distance);
    }
}
