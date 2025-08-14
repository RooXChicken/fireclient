package org.loveroo.fireclient.mixin.modules;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftClient.class)
public interface UseItemAccessor {

    @Invoker("doItemUse")
    public void invokeDoItemUse();
}
