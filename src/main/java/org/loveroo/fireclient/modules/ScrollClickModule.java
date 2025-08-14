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
import org.loveroo.fireclient.mixin.modules.UseItemAccessor;

import java.util.ArrayList;
import java.util.List;

public class ScrollClickModule extends ModuleBase {

    private int clicks = 0;

    public ScrollClickModule() {
        super(new ModuleData("scroll_click", "\uD83D\uDDB1 Scroll Click", "Allows you to simulate a click with every scroll"));
        getData().setShownName(generateDisplayName(0xC9B5B5));

        getData().setGuiElement(false);
        getData().setEnabled(false);

        FireClientside.getKeybindManager().registerKeybind(
                new Keybind("toggle_scroll_click", Text.of("Toggle"), Text.of("Toggle ").copy().append(getData().getShownName()), true, null,
                        () -> getData().setEnabled(!getData().isEnabled()), null)
        );
    }

    @Override
    public void update(MinecraftClient client) {
        if(!getData().isEnabled() || client.player == null || client.currentScreen != null) {
            clicks = 0;
            return;
        }

        if(clicks > 0) {
            clicks = Math.min(3, clicks - 1);

            var useItemClient = (UseItemAccessor)client;
            useItemClient.invokeDoItemUse();
        }
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

        widgets.add(FireClientside.getKeybindManager().getKeybind("toggle_scroll_click").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 - 10));

        return widgets;
    }

    public void incrementClicks() {
        clicks++;
    }
}
