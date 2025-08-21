package org.loveroo.fireclient.modules;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;

import java.util.ArrayList;
import java.util.List;

public class DeathInfoModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0xFFFFFF);

    public DeathInfoModule() {
        super(new ModuleData("death_info", "☠", color));

        getData().setGuiElement(false);
        getData().setEnabled(true);
    }

    @Override
    public void loadJson(JSONObject json) throws JSONException {
        getData().setEnabled(json.optBoolean("enabled", getData().isEnabled()));
    }

    @Override
    public JSONObject saveJson() throws JSONException {
        var json = new JSONObject();

        json.put("enabled", getData().isEnabled());

        return json;
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 - 10));

        return widgets;
    }
}
