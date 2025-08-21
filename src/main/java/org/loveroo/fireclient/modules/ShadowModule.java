package org.loveroo.fireclient.modules;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;

import java.util.ArrayList;
import java.util.List;

public class ShadowModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0x1A042E);

    private boolean distanceEffect = true;
    private boolean increaseHeight = false;
    private boolean fullbright = false;

    public boolean drawingShadow = false;

    public ShadowModule() {
        super(new ModuleData("shadow", "\uD83D\uDD73", color));

        getData().setGuiElement(false);
    }

    @Override
    public void loadJson(JSONObject json) throws JSONException {
        distanceEffect = json.optBoolean("distance_effect", distanceEffect);
        increaseHeight = json.optBoolean("increase_height", increaseHeight);
        fullbright = json.optBoolean("fullbright", fullbright);
    }

    @Override
    public JSONObject saveJson() throws JSONException {
        var json = new JSONObject();

        json.put("distance_effect", distanceEffect);
        json.put("increase_height", increaseHeight);
        json.put("fullbright", fullbright);

        return json;
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
                .dimensions(base.width/2 - 60, base.height/2 + 20, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.shadow.fullbright.tooltip")))
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

    public boolean isDistanceEffect() {
        return distanceEffect;
    }

    public boolean isIncreaseHeight() {
        return increaseHeight;
    }

    public boolean isFullbright() {
        return fullbright;
    }
}
