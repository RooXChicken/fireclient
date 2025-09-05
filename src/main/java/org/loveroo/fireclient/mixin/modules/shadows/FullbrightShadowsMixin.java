package org.loveroo.fireclient.mixin.modules.shadows;

import org.loveroo.fireclient.settings.FullbrightShadows;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.World;

@Mixin(World.class)
public abstract class FullbrightShadowsMixin implements FullbrightShadows {
}
