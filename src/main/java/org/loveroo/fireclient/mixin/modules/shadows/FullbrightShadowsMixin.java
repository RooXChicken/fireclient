package org.loveroo.fireclient.mixin.modules.shadows;

import net.minecraft.world.World;
import org.loveroo.fireclient.data.FullbrightShadows;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(World.class)
public abstract class FullbrightShadowsMixin implements FullbrightShadows {
}
