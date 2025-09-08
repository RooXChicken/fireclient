package org.loveroo.fireclient.mixin.settings;

import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.screen.slot.SlotActionType;

@Mixin(ClientPlayNetworkHandler.class)
public class AutofillLapisMixin {
    
    @Inject(method = "onOpenScreen", at = @At("TAIL"))
    private void autoFill(OpenScreenS2CPacket packet, CallbackInfo info) {
        var client = MinecraftClient.getInstance();
        if(client.interactionManager == null || client.player == null) {
            return;
        }

        if(!(client.currentScreen instanceof EnchantmentScreen)) {
            return;
        }

        if(FireClientside.getSetting(FireClientOption.AUTOFILL_LAPIS) == 0) {
            return;
        }


        var slots = client.player.currentScreenHandler.slots;

        var index = -1;
        for(var slot : slots) {
            var item = slot.inventory.getStack(slot.getIndex());
            if(item == null || !item.isOf(Items.LAPIS_LAZULI)) {
                continue;
            }

            index = slot.id;
            break;
        }

        if(index == -1) {
            return;
        }

        client.interactionManager.clickSlot(
            packet.getSyncId(),
            index,
            0,
            SlotActionType.PICKUP,
            client.player
        );

        client.interactionManager.clickSlot(
            packet.getSyncId(),
            client.player.currentScreenHandler.getSlot(1).id,
            0,
            SlotActionType.PICKUP,
            client.player
        );
    }
}
