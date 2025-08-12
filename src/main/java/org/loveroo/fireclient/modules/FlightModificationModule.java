package org.loveroo.fireclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.data.ModuleData;

import java.util.List;

public class FlightModificationModule extends ModuleBase {

    private float speed = 0.05f;

    private int sneakTicks = 0;
    private boolean toggleWithSneak = false;

    public FlightModificationModule() {
        super(new ModuleData("flight_modification", "☁ Flight Modification", "Allows the control of flight speed"));

        getData().setSelectable(false);
    }

    @Override
    public void update(MinecraftClient client) {
        if(!toggleWithSneak || client.player == null || !client.player.getAbilities().flying) {
            return;
        }

        if(client.player.isSneaking()) {
            sneakTicks++;
        }
        else {
            if(sneakTicks > 0 && sneakTicks < 5) {
                getData().setEnabled(!getData().isEnabled());
            }

            sneakTicks = 0;
        }
    }

    @Override
    public void loadJson(JSONObject json) throws JSONException {
        getData().setEnabled(json.optBoolean("enabled", getData().isEnabled()));

        speed = (float) json.optDouble("speed", speed);
        toggleWithSneak = json.optBoolean("toggle_with_sneak", toggleWithSneak);
    }

    @Override
    public JSONObject saveJson() throws JSONException {
        var json = new JSONObject();

        json.put("enabled", getData().isEnabled());

        json.put("speed", speed);
        json.put("toggle_with_sneak", toggleWithSneak);

        return json;
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = super.getConfigScreen(base);

        widgets.add(ButtonWidget.builder(getToggleText(Text.of("Toggle with Sneak"), toggleWithSneak), this::sneakButtonPressed)
                .dimensions(base.width/2 - 60, base.height/2 + 20, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.flight_module.toggle_with_sneak")))
                .build());

        var slider = new SliderWidget(base.width / 2 - 50, base.height / 2 + 45, 100, 20, Text.of(String.format("Speed: %.2f", speed)), (speed - 0.05f)*5) {

            @Override
            protected void updateMessage() {
                setMessage(Text.of(String.format("Speed: %.2f", speed)));
            }

            @Override
            protected void applyValue() {
                speed = (float) Math.min(0.5f, value/5) + 0.05f;
            }
        };

        widgets.add(slider);
        return widgets;
    }

    private void sneakButtonPressed(ButtonWidget button) {
        toggleWithSneak = !toggleWithSneak;
        button.setMessage(getToggleText(Text.of("Toggle with Sneak"), toggleWithSneak));
    }

    public float getSpeed() {
        return speed;
    }
}
