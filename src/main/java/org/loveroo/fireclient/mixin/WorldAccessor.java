package org.loveroo.fireclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.state.WorldRenderState;
import net.minecraft.client.world.ClientWorld;

@Mixin(WorldRenderer.class)
public interface WorldAccessor {

    @Accessor("world")
    public ClientWorld getWorld();

    @Accessor("worldRenderState")
    public WorldRenderState getworldRenderState();
}