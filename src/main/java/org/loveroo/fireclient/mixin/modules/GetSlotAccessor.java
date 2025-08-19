package org.loveroo.fireclient.mixin.modules;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HandledScreen.class)
public interface GetSlotAccessor {

    @Invoker("getSlotAt")
    public Slot getSlotAtAccessed(double mouseX, double mouseY);
}
