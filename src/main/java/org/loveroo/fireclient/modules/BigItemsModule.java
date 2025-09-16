package org.loveroo.fireclient.modules;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;
import org.loveroo.fireclient.mixin.modules.mutesounds.GetSuggestionAccessor;
import org.loveroo.fireclient.screen.base.ScrollableWidget;
import org.loveroo.fireclient.screen.widgets.RenderItemWidget;
import org.loveroo.fireclient.screen.widgets.ToggleButtonWidget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BigItemsModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0xF9FFCC);

    private final List<BigItem> bigItems = new ArrayList<>();

    private static double scrollPos = 0.0;
    private final int soundsWidgetWidth = 300;
    private final int soundsWidgetHeight = 100;

    @Nullable
    private ScrollableWidget scroll;

    @Nullable
    private TextFieldWidget itemField;

    public BigItemsModule() {
        super(new ModuleData("big_items", "+", color));

        getData().setEnabled(true);
        getData().setGuiElement(false);

        var toggleBind = new Keybind("toggle_big_items",
                Text.translatable("fireclient.keybind.generic.toggle.name"),
                Text.translatable("fireclient.keybind.generic.toggle.description", getData().getShownName()),
                true, null,
                () -> getData().setEnabled(!getData().isEnabled()), null);

        FireClientside.getKeybindManager().registerKeybind(toggleBind);
    }

    @Override
    public void loadJson(JSONObject json) throws Exception {
        super.loadJson(json);

        var itemList = json.optJSONArray("items");
        if(itemList == null) {
            itemList = new JSONArray();
        }

        for(var i = 0; i < itemList.length(); i++) {
            var item = itemList.optJSONObject(i);
            if(item == null) {
                continue;
            }

            bigItems.add(new BigItem(item.optString("id", ""), item.optBoolean("enabled", true)));
        }
    }

    @Override
    public JSONObject saveJson() throws Exception {
        var json = super.saveJson();

        var itemList = new JSONArray();

        for(var item : bigItems) {
            var itemJson = new JSONObject();

            itemJson.put("id", item.getItem());
            itemJson.put("enabled", item.isEnabled());

            itemList.put(itemJson);
        }

        json.put("items", itemList);

        return json;
    }

    @Override
    public void moduleConfigPressed(ButtonWidget button) {
        scrollPos = 0.0;
        super.moduleConfigPressed(button);
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var client = MinecraftClient.getInstance();
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("toggle_big_items").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 + 95));

        itemField = new TextFieldWidget(client.textRenderer, base.width/2 - 140, base.height/2 - 40, soundsWidgetWidth - 50, 15, Text.of(""));
        itemField.setMaxLength(128);
        itemField.setChangedListener((text) -> itemTextChanged(itemField, text));

        widgets.add(itemField);

        widgets.add(ButtonWidget.builder(Text.translatable("fireclient.module.big_items.add_item.name"), (button) -> addItemButtonPressed(itemField))
            .dimensions(base.width/2 + 115, base.height/2 - 40, 20, 15)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.big_items.add_item.tooltip")))
            .build());

        var entries = new ArrayList<ScrollableWidget.ElementEntry>();
        for(var item : bigItems) {
            var entryWidgets = new ArrayList<ClickableWidget>();

            var text = new TextWidget(Text.literal(item.getItem()), client.textRenderer);
            text.setPosition(base.width/2 - 120, 4);

            entryWidgets.add(text);

            entryWidgets.add(new RenderItemWidget(item.getItemEntry(), base.width/2 - 140, 0));

            entryWidgets.add(new ToggleButtonWidget.ToggleButtonBuilder(null)
                .getValue(item::isEnabled)
                .setValue(item::setEnabled)
                .dimensions(base.width/2 + 90, 0,20,15)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.big_items.toggle_item.tooltip", item.getItem())))
                .build());

            entryWidgets.add(ButtonWidget.builder(Text.translatable("fireclient.module.big_items.remove_item.name").withColor(0xD63C3C), (button) -> removeSound(item))
                .dimensions(base.width/2 + 115, 0,20,15)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.big_items.remove_item.tooltip", item.getItem())))
                .build());

            entries.add(new ScrollableWidget.ElementEntry(entryWidgets));
        }

        scroll = new ScrollableWidget(base, soundsWidgetWidth, soundsWidgetHeight,  0, 20, entries);
        scroll.setScrollAmount(scrollPos);
        scroll.setPosition(base.width/2 - (soundsWidgetWidth/2), base.height/2 - 10);

        widgets.add(scroll);

        return widgets;
    }

    @Override
    public void openScreen(Screen base) {
        base.setFocused(itemField);
    }

    private void itemTextChanged(TextFieldWidget widget, String text) {
        if(!text.isEmpty()) {
            var check = text.substring(text.length()-1);
            if(" ,|".contains(check)) {
                widget.setText(text.substring(0, text.length()-1));
                addItemButtonPressed(widget);
                return;
            }
        }

        widget.setSuggestion(getSuggestion(text));
    }

    private String getSuggestion(String text) {
        if(text.isEmpty()) {
            return "";
        }

        var input = RooHelper.filterIdInput(text);

        if(bigItems.stream().noneMatch((bigItem) -> { return bigItem.getItem().equalsIgnoreCase(input); })) {
            var itemId = Identifier.ofVanilla(input);
            if(Registries.ITEM.containsId(itemId)) {
                return "";
            }
        }

        var filteredItems = Registries.ITEM.getIds().stream().filter((id) ->
            id.getPath().startsWith(input) && bigItems.stream().noneMatch((bigItem -> (bigItem.item.equalsIgnoreCase(id.getPath()))))
        ).toList();

        if(filteredItems.isEmpty()) {
            return "";
        }

        var item = filteredItems.getFirst().getPath();
        return item.substring(input.length());
    }

    private void addItemButtonPressed(TextFieldWidget text) {
        var suggestion = ((GetSuggestionAccessor)text).getSuggestion();
        if(suggestion == null) {
            suggestion = "";
        }

        var item = RooHelper.filterIdInput(text.getText()) + suggestion;

        if(bigItems.stream().anyMatch((bigItem -> bigItem.getItem().equalsIgnoreCase(item)))) {
            RooHelper.sendNotification(
                Text.translatable("fireclient.module.big_items.add_item.failure.title"),
                Text.translatable("fireclient.module.big_items.add_item.already_exists.contents")
            );

            return;
        }

        bigItems.add(new BigItem(item, true));
        text.setSuggestion("");

        reloadScreen();
    }

    private void removeSound(BigItem item) {
        bigItems.remove(item);
        reloadScreen();
    }

    @Override
    public void drawScreen(Screen base, DrawContext context, float delta) {
        if(scroll != null) {
            scrollPos = scroll.getScrollAmount();
        }

        drawScreenHeader(context, base.width/2, base.height/2 - 70);
    }

    @Override
    public void closeScreen(Screen screen) {
        FireClientside.saveConfig();
    }

    public boolean isBig(Item item) {
        return bigItems.stream().anyMatch((bigItem) -> (bigItem.isEnabled() && bigItem.getItemEntry().equals(item)));
    }

    static class BigItem {

        private final String item;
        private final Item itemEntry;
        private boolean enabled;

        public BigItem(String item, boolean enabled) {
            this.item = item;
            this.enabled = enabled;

            this.itemEntry = Registries.ITEM.get(Identifier.ofVanilla(this.item));
        }

        public String getItem() {
            return item;
        }

        public Item getItemEntry() {
            return itemEntry;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public interface ItemTypeStorage {

        Item fireclient$getItem();
        void fireclient$setItem(Item item);
    }
}
