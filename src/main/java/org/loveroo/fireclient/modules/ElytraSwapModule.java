package org.loveroo.fireclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;

import java.util.List;
import java.util.function.Predicate;

public class ElytraSwapModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0xFFFFFF);

    public ElytraSwapModule() {
        // the butterfly was the best i could find i promise
        super(new ModuleData("elytra_swap", "\uD83E\uDD8B", color));

        getData().setEnabled(false);
        getData().setGuiElement(false);

        var useBind = new Keybind("use_elytra_swap",
                Text.translatable("fireclient.keybind.generic.use.name"),
                Text.translatable("fireclient.keybind.generic.use.description", getData().getShownName()),
                true, null,
                this::useKey, null);

        FireClientside.getKeybindManager().registerKeybind(useBind);
    }

    private void useKey() {
        if(!getData().isEnabled()) {
            return;
        }

        var client = MinecraftClient.getInstance();
        if(client.player == null) {
            return;
        }

        var hasElytra = client.player.getInventory().getStack(38).isOf(Items.ELYTRA);
        var slot = (hasElytra) ? firstOf((item) -> item.isIn(ItemTags.CHEST_ARMOR)) : firstOf((item) -> item.isOf(Items.ELYTRA));

        if(slot == -1) {
            return;
        }

        swapArmor(slot, 6);
    }

    private int firstOf(Predicate<ItemStack> itemCheck) {
        var client = MinecraftClient.getInstance();
        if(client.player == null) {
            return -1;
        }

        var slot = -1;

        for(var i = 0; i < client.player.getInventory().size(); i++) {
            var item = client.player.getInventory().getStack(i);
            if(!itemCheck.test(item)) {
                continue;
            }

            slot = i;
            break;
        }

        return slot;
    }

    private void swapArmor(int sourceSlot, int destSlot) {
        var client = MinecraftClient.getInstance();
        if(client.player == null || client.interactionManager == null) {
            return;
        }

        if(sourceSlot < 9) {
            sourceSlot += 36;
        }

        client.interactionManager.clickSlot(
                client.player.playerScreenHandler.syncId,
                sourceSlot,
                0,
                SlotActionType.PICKUP,
                client.player
        );

        client.interactionManager.clickSlot(
                client.player.playerScreenHandler.syncId,
                destSlot,
                0,
                SlotActionType.PICKUP,
                client.player
        );

        client.interactionManager.clickSlot(
                client.player.playerScreenHandler.syncId,
                sourceSlot,
                0,
                SlotActionType.PICKUP,
                client.player
        );
    }

    @Override
    public void loadJson(JSONObject json) throws JSONException {
        getData().setEnabled(json.optBoolean("enabled", getData().isEnabled()));
    }

    @Override
    public JSONObject saveJson() throws JSONException {
        var json = new JSONObject();

        json.put("enabled", getData().isEnabled());

        return json;
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = super.getConfigScreen(base);

        widgets.add(FireClientside.getKeybindManager().getKeybind("use_elytra_swap").getRebindButton(5, base.height - 25, 120,20));

        return widgets;
    }
}
