package org.loveroo.fireclient.mixin.modules.renderworld;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldRenderer.class)
public interface WorldRendererAccessor {

    @Accessor("world")
    public ClientWorld getWorld();

    @Accessor("renderedEntitiesCount")
    public int getRenderedEntitiesCount();
}
