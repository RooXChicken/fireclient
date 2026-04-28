package org.loveroo.fireclient.mixin;

import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.multiplayer.ClientLevel;

@Mixin(LevelRenderer.class)
public interface WorldAccessor {

    @Accessor("level")
    public ClientLevel getLevel();

    @Accessor("levelRenderState")
    public LevelRenderState getLevelRenderState();
}