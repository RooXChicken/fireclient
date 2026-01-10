package org.loveroo.fireclient.mixin.modules.reachdisplay;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;

@Mixin(ClientPlayerEntity.class)
public interface FindCrosshairTargetAccessor {

    @Invoker("method_76763")
    public HitResult findCrosshairTargetInvoker(Entity camera, double blockInteractionRange, double entityInteractionRange, float tickDelta);
}
