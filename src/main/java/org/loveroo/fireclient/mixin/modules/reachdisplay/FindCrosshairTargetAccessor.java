package org.loveroo.fireclient.mixin.modules.reachdisplay;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;

@Mixin(LocalPlayer.class)
public interface FindCrosshairTargetAccessor {

    @Invoker("pick")
    public HitResult findCrosshairTargetInvoker(Entity camera, double blockInteractionRange, double entityInteractionRange, float tickDelta);
}
