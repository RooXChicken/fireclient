package org.loveroo.fireclient.modules;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.data.ModuleData;

import java.util.List;

public class FlightModificationModule extends ModuleBase {

    private float speed = 0.05f;

    public FlightModificationModule() {
        super(new ModuleData("flight_modification", "☁ Flight Modification", "Allows the control of flight speed"));

        getData().setSelectable(false);
    }

    @Override
    public void loadJson(JSONObject json) throws JSONException {
        getData().setEnabled(json.optBoolean("enabled", getData().isEnabled()));
        speed = (float) json.optDouble("speed", speed);
    }

    @Override
    public JSONObject saveJson() throws JSONException {
        var json = new JSONObject();

        json.put("enabled", getData().isEnabled());
        json.put("speed", speed);

        return json;
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = super.getConfigScreen(base);

        var slider = new SliderWidget(base.width / 2 - 50, base.height / 2 + 15, 100, 20, Text.of(String.format("Speed: %.2f", speed)), (speed - 0.05f)*5) {

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

    public float getSpeed() {
        return speed;
    }
}
