package org.loveroo.fireclient.modules;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.data.ModuleData;

import java.util.ArrayList;
import java.util.List;

public class ShadowModule extends ModuleBase {

    private boolean distanceEffect = true;
    private boolean increaseHeight = false;
    private boolean fullbright = false;

    public boolean drawingShadow = false;

    public ShadowModule() {
        super(new ModuleData("shadow", "\uD83D\uDD73 Shadows", "Modifies shadow rendering"));
        getData().setShownName(generateDisplayName(0x1A042E));

        getData().setSelectable(false);
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

        widgets.add(ButtonWidget.builder(getToggleText(Text.of("Distance Effect"), distanceEffect), this::distanceButtonPressed)
                .dimensions(base.width/2 - 130, base.height/2 - 10, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.shadow.distance_effect")))
                .build());

        widgets.add(ButtonWidget.builder(getToggleText(Text.of("Increase Height"), increaseHeight), this::heightButtonPressed)
                .dimensions(base.width/2 + 10, base.height/2 - 10, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.shadow.increase_height")))
                .build());

        widgets.add(ButtonWidget.builder(getToggleText(Text.of("Fullbright"), fullbright), this::fullbrightButtonPressed)
                .dimensions(base.width/2 - 60, base.height/2 + 20, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.shadow.fullbright")))
                .build());

        return widgets;
    }

    private void distanceButtonPressed(ButtonWidget button) {
        distanceEffect = !distanceEffect;
        button.setMessage(getToggleText(Text.of("Distance Effect"), distanceEffect));
    }

    private void heightButtonPressed(ButtonWidget button) {
        increaseHeight = !increaseHeight;
        button.setMessage(getToggleText(Text.of("Increase Height"), increaseHeight));
    }

    private void fullbrightButtonPressed(ButtonWidget button) {
        fullbright = !fullbright;
        button.setMessage(getToggleText(Text.of("Fullbright"), fullbright));
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
