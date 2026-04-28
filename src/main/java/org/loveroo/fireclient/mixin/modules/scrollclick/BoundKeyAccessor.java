package org.loveroo.fireclient.mixin.modules.scrollclick;

import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyMapping.class)
public interface BoundKeyAccessor {

    @Accessor("key")
    InputConstants.Key getKey();
}
