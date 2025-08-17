package org.loveroo.fireclient.modules;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BigItemsModule extends ModuleBase {

    private final String splitRegex = "[ ,|]+";
    private String items = "golden_apple";

    private HashSet<Item> bigItems = new HashSet<>();

    public BigItemsModule() {
        super(new ModuleData("big_items", "\uD83C\uDF1F Big Items", "Makes certain items render larger"));
        getData().setShownName(generateDisplayName(0xF9FFCC));

        getData().setGuiElement(false);
        

        ClientLifecycleEvents.CLIENT_STARTED.register((client) -> {
            if(getData().isEnabled()) {
                collectItems();
            }
        });

        FireClientside.getKeybindManager().registerKeybind(
                new Keybind("toggle_big_items", Text.of("Toggle"), Text.of("Toggle ").copy().append(getData().getShownName()), true, null,
                        () -> getData().setEnabled(!getData().isEnabled()), null)
        );
    }

    @Override
    public void loadJson(JSONObject json) throws JSONException {
        items = json.optString("items", items);
        getData().setEnabled(json.optBoolean("enabled", getData().isEnabled()));
    }

    @Override
    public JSONObject saveJson() throws JSONException {
        var json = new JSONObject();

        json.put("items", items);
        json.put("enabled", getData().isEnabled());

        return json;
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var client = MinecraftClient.getInstance();
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("toggle_big_items").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 - 10));

        var itemField = new TextFieldWidget(client.textRenderer, base.width/2 - 150, base.height/2 + 20, 300, 15, Text.of(""));
        itemField.setMaxLength(512);

        itemField.setText(items);
        itemField.setChangedListener(this::itemsFieldChanged);

        widgets.add(itemField);
        return widgets;
    }

    public void itemsFieldChanged(String text) {
        items = text;
        collectItems();
    }

    @Override
    public void closeScreen(Screen screen) {
        FireClientside.saveConfig();
    }

    private void collectItems() {
        bigItems.clear();
        var itemSplit = items.split(splitRegex);

        for(var item : itemSplit) {
            if(!item.matches("[a-z0-9/._-]+")) {
                continue;
            }

            var registry = Registries.ITEM.get(Identifier.ofVanilla(item));
            bigItems.add(registry);
        }
    }

    public boolean isBig(Item item) {
        return bigItems.contains(item);
    }

    public interface ItemTypeStorage {

        Item fireclient$getItem();
        void fireclient$setItem(Item item);
    }
}
