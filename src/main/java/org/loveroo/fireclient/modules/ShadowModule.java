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

public class ShadowModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0x1A042E);

    @JsonOption(name = "distance_effect")
    private boolean distanceEffect = true;
    
    @JsonOption(name = "increase_height")
    private boolean increaseHeight = false;

    @JsonOption(name = "fullbright")
    private boolean fullbright = false;

    @JsonOption(name = "render_on_all")
    private boolean renderOnAll = false;

    public static boolean drawingShadow = false;

    public ShadowModule() {
        super(new ModuleData("shadow", "\uD83D\uDD73", color));

        getData().setGuiElement(false);
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(ButtonWidget.builder(getToggleText(Text.translatable("fireclient.module.shadow.distance_effect.name"), distanceEffect), this::distanceButtonPressed)
                .dimensions(base.width/2 - 130, base.height/2 - 10, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.shadow.distance_effect.tooltip")))
                .build());

        widgets.add(ButtonWidget.builder(getToggleText(Text.translatable("fireclient.module.shadow.increase_height.name"), increaseHeight), this::heightButtonPressed)
                .dimensions(base.width/2 + 10, base.height/2 - 10, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.shadow.increase_height.tooltip")))
                .build());

        widgets.add(ButtonWidget.builder(getToggleText(Text.translatable("fireclient.module.shadow.fullbright.name"), fullbright), this::fullbrightButtonPressed)
                .dimensions(base.width/2 - 130, base.height/2 + 20, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.shadow.fullbright.tooltip")))
                .build());

        widgets.add(ButtonWidget.builder(getToggleText(Text.translatable("fireclient.module.shadow.render_on_all.name"), renderOnAll), this::renderOnAllButtonPressed)
                .dimensions(base.width/2 + 10, base.height/2 + 20, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.shadow.render_on_all.tooltip")))
                .build());

        return widgets;
    }

    private void distanceButtonPressed(ButtonWidget button) {
        distanceEffect = !distanceEffect;
        button.setMessage(getToggleText(Text.translatable("fireclient.module.shadow.distance_effect.name"), distanceEffect));
    }

    private void heightButtonPressed(ButtonWidget button) {
        increaseHeight = !increaseHeight;
        button.setMessage(getToggleText(Text.translatable("fireclient.module.shadow.increase_height.name"), increaseHeight));
    }

    private void fullbrightButtonPressed(ButtonWidget button) {
        fullbright = !fullbright;
        button.setMessage(getToggleText(Text.translatable("fireclient.module.shadow.fullbright.name"), fullbright));
    }

    private void renderOnAllButtonPressed(ButtonWidget button) {
        renderOnAll = !renderOnAll;
        button.setMessage(getToggleText(Text.translatable("fireclient.module.shadow.render_on_all.name"), renderOnAll));
    }

    public boolean isDistanceEffect() {
        return distanceEffect;
    }

    public boolean isIncreaseHeight() {
        return increaseHeight;
    }

    public boolean isFullbright() {
        return fullbright;
    }

    public boolean isRenderOnAll() {
        return renderOnAll;
    }
}
