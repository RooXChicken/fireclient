package org.loveroo.fireclient.mixin.modules.kit;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ScreenHandler.class)
public interface AddSlotAccessor {

    @Invoker("addSlot")
    public Slot addSlotAccessed(Slot slot);
}
