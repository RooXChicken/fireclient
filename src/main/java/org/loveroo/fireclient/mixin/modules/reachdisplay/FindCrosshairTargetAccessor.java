package org.loveroo.fireclient.mixin.modules.reachdisplay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;

@Mixin(GameRenderer.class)
public interface FindCrosshairTargetAccessor {

    @Invoker("findCrosshairTarget")
    public HitResult findCrosshairTargetInvoker(Entity camera, double blockInteractionRange, double entityInteractionRange, float tickDelta);
}
