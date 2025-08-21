package org.loveroo.fireclient.mixin.settings;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingStandScreenHandler.class)
public abstract class BrewingStandFuelMixin {

    @Inject(method = "quickMove", at = @At("HEAD"), cancellable = true)
    private void detectMove(PlayerEntity player, int slotIndex, CallbackInfoReturnable<ItemStack> info) {
        if(FireClientside.getSetting(FireClientOption.BLAZE_POWDER_FILL) == 0 || slotIndex == 4) {
            return;
        }

        var screen = (BrewingStandScreenHandler)(Object)this;
        var slot = screen.slots.get(slotIndex);

        if(slot == null || !slot.hasStack() || !slot.getStack().isIn(ItemTags.BREWING_FUEL)) {
            return;
        }

        var target = screen.slots.get(3);
        if(target == null || target.hasStack()) {
            return;
        }

        var client = MinecraftClient.getInstance();
        if(client.interactionManager == null) {
            return;
        }

        client.interactionManager.clickSlot(
                screen.syncId,
                slotIndex,
                0,
                SlotActionType.PICKUP,
                client.player
        );

        client.interactionManager.clickSlot(
                screen.syncId,
                target.getIndex(),
                0,
                SlotActionType.PICKUP,
                client.player
        );

        client.interactionManager.clickSlot(
                screen.syncId,
                slotIndex,
                0,
                SlotActionType.PICKUP,
                client.player
        );
    }
}
