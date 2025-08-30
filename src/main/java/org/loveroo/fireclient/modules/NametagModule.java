package org.loveroo.fireclient.modules;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.JsonOption;
import org.loveroo.fireclient.data.ModuleData;

import java.util.ArrayList;
import java.util.List;

public class NametagModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0xE0E7FF);

    @JsonOption(name = "show_own")
    private boolean showOwn = false;

    @JsonOption(name = "darker_background")
    private boolean darkerBackground = false;

    @JsonOption(name = "text_shadow")
    private boolean textShadow = false;

    @JsonOption(name = "unlimit_below_name")
    private boolean unlimitBelowName = false;

    public NametagModule() {
        super(new ModuleData("nametag", "\uD83C\uDFF7", color));

        getData().setGuiElement(false);
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(ButtonWidget.builder(getToggleText(Text.translatable("fireclient.module.nametag.darker_background.name"), darkerBackground), this::darkerBackgroundButtonPressed)
            .dimensions(base.width/2 - 130, base.height/2 - 10, 120, 20)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.nametag.darker_background.tooltip")))
            .build());

        widgets.add(ButtonWidget.builder(getToggleText(Text.translatable("fireclient.module.nametag.show_own.name"), showOwn), this::showOwnButtonPressed)
            .dimensions(base.width/2 + 10, base.height/2 - 10, 120, 20)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.nametag.show_own.tooltip")))
            .build());

        widgets.add(ButtonWidget.builder(getToggleText(Text.translatable("fireclient.module.nametag.text_shadow.name"), textShadow), this::textShadowButtonPressed)
            .dimensions(base.width/2 - 130, base.height/2 + 20, 120, 20)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.nametag.text_shadow.tooltip")))
            .build());

        widgets.add(ButtonWidget.builder(getToggleText(Text.translatable("fireclient.module.nametag.unlimit_nametag.name"), unlimitBelowName), this::unlimitButtonPressed)
            .dimensions(base.width/2 + 10, base.height/2 + 20, 120, 20)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.nametag.unlimit_nametag.tooltip")))
            .build());

        return widgets;
    }

    private void darkerBackgroundButtonPressed(ButtonWidget button) {
        darkerBackground = !darkerBackground;
        button.setMessage(getToggleText(Text.translatable("fireclient.module.nametag.darker_background.name"), darkerBackground));
    }

    private void showOwnButtonPressed(ButtonWidget button) {
        showOwn = !showOwn;
        button.setMessage(getToggleText(Text.translatable("fireclient.module.nametag.show_own.name"), showOwn));
    }

    private void textShadowButtonPressed(ButtonWidget button) {
        textShadow = !textShadow;
        button.setMessage(getToggleText(Text.translatable("fireclient.module.nametag.text_shadow.name"), textShadow));
    }

    private void unlimitButtonPressed(ButtonWidget button) {
        unlimitBelowName = !unlimitBelowName;
        button.setMessage(getToggleText(Text.translatable("fireclient.module.nametag.unlimit_nametag.name"), unlimitBelowName));
    }

    public boolean isDarkerBackground() {
        return darkerBackground;
    }

    public boolean isShowOwn() {
        return showOwn;
    }

    public boolean isTextShadow() {
        return textShadow;
    }

    public boolean isUnlimitBelowName() {
        return unlimitBelowName;
    }
}
