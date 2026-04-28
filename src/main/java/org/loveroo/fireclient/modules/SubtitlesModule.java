package org.loveroo.fireclient.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;
import org.loveroo.fireclient.screen.widgets.ToggleButtonWidget;

import java.util.ArrayList;
import java.util.List;

public class SubtitlesModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0xDEDEDE);

    public SubtitlesModule() {
        super(new ModuleData("subtitles", "\uD83D\uDCC4", color));

        getData().setGuiElement(false);

        var toggleBind = new Keybind("toggle_subtitles",
            Component.translatable("fireclient.keybind.generic.toggle.name"),
            Component.translatable("fireclient.keybind.generic.toggle_visibility.description", getData().getShownName()),
            true, null,
            this::subtitlesToggled, null);

        FireClientside.getKeybindManager().registerKeybind(toggleBind);
    }

    @Override
    public List<AbstractWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<AbstractWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("toggle_subtitles").getRebindButton(5, base.height - 25, 120,20));

        widgets.add(new ToggleButtonWidget.ToggleButtonBuilder(Component.translatable("fireclient.module.subtitles.visible.name"))
            .getValue(getData()::isEnabled)
            .setValue(getData()::setEnabled)
            .dimensions(base.width/2 - 60, base.height/2 - 10, 120, 20)
            .tooltip(Tooltip.create(Component.translatable("fireclient.module.subtitles.visible.tooltip")))
            .build());

        return widgets;
    }

    private void subtitlesToggled() {
        var client = Minecraft.getInstance();
        client.options.showSubtitles().set(!isEnabled());
    }

    private boolean isEnabled() {
        return Minecraft.getInstance().options.showSubtitles().get();
    }
}
