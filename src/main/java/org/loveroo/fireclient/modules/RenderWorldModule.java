package org.loveroo.fireclient.modules;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class RenderWorldModule extends ModuleBase {

    private boolean toggled = false;

    private final KeyBinding toggleButton = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.fireclient.toggle_render_world", GLFW.GLFW_KEY_J, FireClient.KEYBIND_CATEGORY));

    public RenderWorldModule() {
        super(new ModuleData("render_world", "\uD83C\uDF0D Render World", "[CHEAT] Allows the toggling of the world rendering"));

        getData().setSelectable(false);
    }

    @Override
    public void update(MinecraftClient client) {
        if(!getData().isEnabled()) {
            toggled = false;
            return;
        }

        if(toggleButton.wasPressed()) {
            toggled = !toggled;
        }
    }

    @Override
    public void loadJson(JSONObject json) throws JSONException {
        getData().setEnabled(json.optBoolean("enabled", getData().isEnabled()));
        toggled = json.optBoolean("toggled", false);
    }

    @Override
    public JSONObject saveJson() throws JSONException {
        var json = new JSONObject();

        json.put("enabled", getData().isEnabled());
        json.put("toggled", toggled);

        return json;
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 - 10));

        return widgets;
    }

    public boolean isToggled() {
        return toggled;
    }
}
