package org.loveroo.fireclient.modules;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.world.inventory.ContainerInput;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.network.chat.Component;

public class ElytraSwapModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0xFFFFFF);

    private boolean activeSinceJoined = false;
    private boolean usedSinceJoined = false;

    public ElytraSwapModule() {
        // the butterfly was the best i could find i promise
        super(new ModuleData("elytra_swap", "\uD83E\uDD8B", color));

        getData().setEnabled(false);
        getData().setGuiElement(false);

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            activeSinceJoined = false;
            usedSinceJoined = false;
        });

        var useBind = new Keybind("use_elytra_swap",
            Component.translatable("fireclient.keybind.generic.use.name"),
            Component.translatable("fireclient.keybind.generic.use.description", getData().getShownName()),
            true, null,
            this::useKey, null);

        FireClientside.getKeybindManager().registerKeybind(useBind);
    }

    @Override
    public void update(Minecraft client) {
        if(!activeSinceJoined && getData().isEnabled()) {
            activeSinceJoined = true;
            FireClient.LOGGER.info("ElytraSwap is enabled!");
        }
    }

    private void useKey() {
        if(!getData().isEnabled()) {
            return;
        }

        var client = Minecraft.getInstance();
        if(client.player == null) {
            return;
        }

        var hasElytra = client.player.getInventory().getItem(38).is(Items.ELYTRA);
        var slot = (hasElytra) ? firstOf((item) -> item.is(ItemTags.CHEST_ARMOR)) : firstOf((item) -> item.is(Items.ELYTRA));

        if(slot == -1) {
            return;
        }

        if(!usedSinceJoined) {
            usedSinceJoined = true;
            FireClient.LOGGER.info("ElytraSwap was used!");
        }

        swapArmor(slot, 6);
    }

    private int firstOf(Predicate<ItemStack> itemCheck) {
        var client = Minecraft.getInstance();
        if(client.player == null) {
            return -1;
        }

        var slot = -1;

        for(var i = 0; i < client.player.getInventory().getContainerSize(); i++) {
            var item = client.player.getInventory().getItem(i);
            if(!itemCheck.test(item)) {
                continue;
            }

            slot = i;
            break;
        }

        return slot;
    }

    private void swapArmor(int sourceSlot, int destSlot) {
        var client = Minecraft.getInstance();
        if(client.player == null || client.gameMode == null) {
            return;
        }

        if(sourceSlot < 9) {
            sourceSlot += 36;
        }

        client.gameMode.handleContainerInput(
                client.player.inventoryMenu.containerId,
                sourceSlot,
                0,
                ContainerInput.PICKUP,
                client.player
        );

        client.gameMode.handleContainerInput(
                client.player.inventoryMenu.containerId,
                destSlot,
                0,
                ContainerInput.PICKUP,
                client.player
        );

        client.gameMode.handleContainerInput(
                client.player.inventoryMenu.containerId,
                sourceSlot,
                0,
                ContainerInput.PICKUP,
                client.player
        );
    }

    @Override
    public List<AbstractWidget> getConfigScreen(Screen base) {
        var widgets = super.getConfigScreen(base);

        widgets.add(FireClientside.getKeybindManager().getKeybind("use_elytra_swap").getRebindButton(5, base.height - 25, 120,20));

        return widgets;
    }
}
