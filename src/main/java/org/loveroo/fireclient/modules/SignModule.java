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
import org.loveroo.fireclient.screen.widgets.ToggleButtonWidget;

import java.util.ArrayList;
import java.util.List;

public class SignModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0x786F59);

    @JsonOption(name = "disable_gui")
    private boolean disableGui = false;

    @JsonOption(name = "rendering_disabled")
    private boolean renderingDisabled = false;

    public SignModule() {
        super(new ModuleData("sign", "\uD83E\uDEA7", color));

        getData().setGuiElement(false);
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(new ToggleButtonWidget.ToggleButtonBuilder(Text.translatable("fireclient.module.sign.disable_gui.name"))
            .getValue(() -> { return disableGui; })
            .setValue((value) -> { disableGui = value; })
            .position(base.width/2 - 60, base.height/2 - 10)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.sign.disable_gui.description")))
            .build());

        widgets.add(new ToggleButtonWidget.ToggleButtonBuilder(Text.translatable("fireclient.module.sign.disable_rendering.name"))
            .getValue(() -> { return renderingDisabled; })
            .setValue((value) -> { renderingDisabled = value; })
            .position(base.width/2 - 60, base.height/2 + 20)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.sign.disable_rendering.description")))
            .build());

        return widgets;
    }

    public boolean isGuiDisabled() {
        return disableGui;
    }

    public boolean isRenderingDisabled() {
        return renderingDisabled;
    }
}
