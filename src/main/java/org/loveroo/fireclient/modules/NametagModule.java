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
import org.loveroo.fireclient.screen.widgets.ToggleButtonBuilder;

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

        widgets.add(new ToggleButtonBuilder(Text.translatable("fireclient.module.nametag.darker_background.name"))
            .getValue(() -> { return darkerBackground; })
            .setValue((value) -> { darkerBackground = value; })
            .position(base.width/2 - 130, base.height/2 - 10)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.nametag.darker_background.tooltip")))
            .build());

        widgets.add(new ToggleButtonBuilder(Text.translatable("fireclient.module.nametag.show_own.name"))
            .getValue(() -> { return showOwn; })
            .setValue((value) -> { showOwn = value; })
            .position(base.width/2 + 10, base.height/2 - 10)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.nametag.show_own.tooltip")))
            .build());

        widgets.add(new ToggleButtonBuilder(Text.translatable("fireclient.module.nametag.text_shadow.name"))
            .getValue(() -> { return textShadow; })
            .setValue((value) -> { textShadow = value; })
            .position(base.width/2 - 130, base.height/2 + 20)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.nametag.text_shadow.tooltip")))
            .build());

        widgets.add(new ToggleButtonBuilder(Text.translatable("fireclient.module.nametag.unlimit_nametag.name"))
            .getValue(() -> { return unlimitBelowName; })
            .setValue((value) -> { unlimitBelowName = value; })
            .position(base.width/2 + 10, base.height/2 + 20)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.nametag.unlimit_nametag.tooltip")))
            .build());

        return widgets;
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
