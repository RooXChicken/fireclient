package org.loveroo.fireclient.mixin.settings;

import net.minecraft.world.inventory.ContainerInput;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.BrewingStandMenu;

@Mixin(BrewingStandMenu.class)
public abstract class BrewingStandFuelMixin {

    @Inject(method = "quickMoveStack", at = @At("HEAD"))
    private void detectMove(Player player, int slotIndex, CallbackInfoReturnable<ItemStack> info) {
        if(FireClientside.getSetting(FireClientOption.BLAZE_POWDER_FILL) == 0 || slotIndex == 4) {
            return;
        }

        var screen = (BrewingStandMenu)(Object)this;
        var slot = screen.slots.get(slotIndex);

        if(slot == null || !slot.hasItem() || !slot.getItem().is(ItemTags.BREWING_FUEL)) {
            return;
        }

        var target = screen.slots.get(3);
        if(target == null || target.hasItem()) {
            return;
        }

        var client = Minecraft.getInstance();
        if(client.gameMode == null) {
            return;
        }

        client.gameMode.handleContainerInput(
            screen.containerId,
            slotIndex,
            0,
            ContainerInput.PICKUP,
            client.player
        );

        client.gameMode.handleContainerInput(
            screen.containerId,
            target.getContainerSlot(),
            0,
            ContainerInput.PICKUP,
            client.player
        );

        client.gameMode.handleContainerInput(
            screen.containerId,
            slotIndex,
            0,
            ContainerInput.PICKUP,
            client.player
        );
    }
}
