package org.loveroo.fireclient.mixin.modules.kit;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerMenu.class)
public interface AddSlotAccessor {

    @Invoker("addSlot")
    public Slot addSlotAccessed(Slot slot);
}
