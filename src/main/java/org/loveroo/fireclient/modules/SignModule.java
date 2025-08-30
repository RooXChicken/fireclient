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

        widgets.add(ButtonWidget.builder(getToggleText(Text.translatable("fireclient.module.sign.disable_gui.name"), disableGui), this::disableGuiButtonPressed)
                .dimensions(base.width/2 - 130, base.height/2 - 10, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.sign.disable_gui.description")))
                .build());

        widgets.add(ButtonWidget.builder(getToggleText(Text.translatable("fireclient.module.sign.disable_rendering.name"), renderingDisabled), this::disableRenderingButtonPressed)
                .dimensions(base.width/2 + 10, base.height/2 - 10, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.sign.disable_rendering.description")))
                .build());

        return widgets;
    }

    private void disableGuiButtonPressed(ButtonWidget button) {
        disableGui = !disableGui;
        button.setMessage(getToggleText(Text.translatable("fireclient.module.sign.disable_gui.name"), disableGui));
    }

    private void disableRenderingButtonPressed(ButtonWidget button) {
        renderingDisabled = !renderingDisabled;
        button.setMessage(getToggleText(Text.translatable("fireclient.module.sign.disable_rendering.name"), renderingDisabled));
    }

    public boolean isGuiDisabled() {
        return disableGui;
    }

    public boolean isRenderingDisabled() {
        return renderingDisabled;
    }
}
