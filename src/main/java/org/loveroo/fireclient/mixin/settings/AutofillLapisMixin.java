package org.loveroo.fireclient.mixin.settings;

import net.minecraft.world.inventory.ContainerInput;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.item.Items;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;

@Mixin(ClientPacketListener.class)
public class AutofillLapisMixin {
    
    @Inject(method = "handleOpenScreen", at = @At("TAIL"))
    private void autoFill(ClientboundOpenScreenPacket packet, CallbackInfo info) {
        var client = Minecraft.getInstance();
        if(client.gameMode == null || client.player == null) {
            return;
        }

        if(!(client.screen instanceof EnchantmentScreen)) {
            return;
        }

        if(FireClientside.getSetting(FireClientOption.AUTOFILL_LAPIS) == 0) {
            return;
        }


        var slots = client.player.containerMenu.slots;

        var index = -1;
        for(var slot : slots) {
            var item = slot.container.getItem(slot.getContainerSlot());
            if(item == null || !item.is(Items.LAPIS_LAZULI)) {
                continue;
            }

            index = slot.index;
            break;
        }

        if(index == -1) {
            return;
        }

        client.gameMode.handleContainerInput(
            packet.getContainerId(),
            index,
            0,
            ContainerInput.PICKUP,
            client.player
        );

        client.gameMode.handleContainerInput(
            packet.getContainerId(),
            client.player.containerMenu.getSlot(1).index,
            0,
                ContainerInput.PICKUP,
            client.player
        );
    }
}
