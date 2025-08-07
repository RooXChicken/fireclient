package org.loveroo.fireclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.data.ModuleData;

import java.util.ArrayList;
import java.util.List;

public class NametagModule extends ModuleBase {

    private boolean showOwn = false;
    private boolean darkerBackground = false;

    public NametagModule() {
        super(new ModuleData("nametag", "\uD83C\uDFF7 Nametag", "Allows the modification of vanilla nametags"));

        getData().setSelectable(false);
    }

    @Override
    public void loadJson(JSONObject json) throws JSONException {
        showOwn = json.optBoolean("show_own", showOwn);
        darkerBackground = json.optBoolean("darker_background", darkerBackground);
    }

    @Override
    public JSONObject saveJson() throws JSONException {
        var json = new JSONObject();

        json.put("show_own", showOwn);
        json.put("darker_background", darkerBackground);

        return json;
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(ButtonWidget.builder(Text.of("Darker: " + darkerBackground), this::darkerBackgroundButtonPressed)
                .dimensions(base.width/2 - 130, base.height/2 - 10, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.nametag.darker_background")))
                .build());

        widgets.add(ButtonWidget.builder(Text.of("Show Own: " + showOwn), this::showOwnButtonPressed)
                .dimensions(base.width/2 + 10, base.height/2 - 10, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.nametag.show_own")))
                .build());

        return widgets;
    }

    private void darkerBackgroundButtonPressed(ButtonWidget button) {
        darkerBackground = !darkerBackground;
        button.setMessage(Text.of("Darker: " + darkerBackground));
    }

    private void showOwnButtonPressed(ButtonWidget button) {
        showOwn = !showOwn;
        button.setMessage(Text.of("Show Own: " + showOwn));
    }

    public boolean isDarkerBackground() {
        return darkerBackground;
    }

    public boolean isShowOwn() {
        return showOwn;
    }
}
