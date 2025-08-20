package org.loveroo.fireclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;

import java.util.ArrayList;
import java.util.List;

public class FullbrightModule extends ModuleBase {

    public FullbrightModule() {
        super(new ModuleData("fullbright", "\uD83D\uDCA1 Fullbright", "Makes everything fully lit"));
        getData().setShownName(generateDisplayName(0xFFF466));

        getData().setGuiElement(false);

        FireClientside.getKeybindManager().registerKeybind(
                new Keybind("toggle_fullbright", Text.of("Toggle"), Text.of("Toggle ").copy().append(getData().getShownName()), true, null,
                        this::useKey, null)
        );
    }

    private void useKey() {
        getData().setEnabled(!getData().isEnabled());
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

        widgets.add(FireClientside.getKeybindManager().getKeybind("toggle_fullbright").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 - 10));

        return widgets;
    }
}
