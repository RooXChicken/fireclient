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
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.JsonOption;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;

import java.util.List;

public class FlightSpeedModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0xFFFFFF);

    @JsonOption(name = "speed")
    private double speed = 0.05;

    private int sneakTicks = 0;

    @JsonOption(name = "toggle_with_sneak")
    private boolean toggleWithSneak = false;

    public FlightSpeedModule() {
        super(new ModuleData("flight_speed", "☁", color));

        getData().setGuiElement(false);

        var toggleBind = new Keybind("toggle_flight_speed",
                Text.translatable("fireclient.keybind.generic.toggle.name"),
                Text.translatable("fireclient.keybind.generic.toggle.description", getData().getShownName()),
                true, null,
                () -> getData().setEnabled(!getData().isEnabled()), null);

        FireClientside.getKeybindManager().registerKeybind(toggleBind);
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
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = super.getConfigScreen(base);

        widgets.add(FireClientside.getKeybindManager().getKeybind("toggle_flight_speed").getRebindButton(5, base.height - 25, 120,20));

        widgets.add(ButtonWidget.builder(getToggleText(Text.translatable("fireclient.module.flight_speed.toggle_with_sneak.name"), toggleWithSneak), this::sneakButtonPressed)
                .dimensions(base.width/2 - 60, base.height/2 + 20, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.flight_speed.toggle_with_sneak.tooltip")))
                .build());

        var slider = new SliderWidget(base.width / 2 - 50, base.height / 2 + 45, 100, 20, getSpeedText(), (speed - 0.05f)*5) {

            @Override
            protected void updateMessage() {
                setMessage(getSpeedText());
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
        button.setMessage(getToggleText(Text.translatable("fireclient.module.flight_speed.toggle_with_sneak.name"), toggleWithSneak));
    }

    private Text getSpeedText() {
        return Text.translatable("fireclient.module.flight_speed.display", String.format("%.2f", speed));
    }

    public float getSpeed() {
        return (float)speed;
    }
}
