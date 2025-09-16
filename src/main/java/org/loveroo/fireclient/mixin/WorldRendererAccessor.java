package org.loveroo.fireclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;

@Mixin(WorldRenderer.class)
public interface WorldRendererAccessor {

    @Accessor("world")
    public ClientWorld getWorld();

    @Accessor("regularEntityCount")
    public int getRenderedEntitiesCount();
}
