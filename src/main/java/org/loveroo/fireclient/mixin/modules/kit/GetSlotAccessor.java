package org.loveroo.fireclient.mixin.modules.kit;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerScreen.class)
public interface GetSlotAccessor {

    @Invoker("getHoveredSlot")
    public Slot getSlotAtAccessed(double mouseX, double mouseY);
}
