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

public class RenderWorldModule extends ModuleBase {

    private boolean toggled = false;

//    private final KeyBinding toggleButton = KeyBindingHelper.registerKeyBinding(
//            new KeyBinding("key.fireclient.toggle_render_world", GLFW.GLFW_KEY_J, FireClient.KEYBIND_CATEGORY));

    public RenderWorldModule() {
        super(new ModuleData("render_world", "\uD83C\uDF0D Render World", "[CHEAT] Allows the toggling of the world rendering"));
        getData().setShownName(generateDisplayName(0x589157));

        getData().setGuiElement(false);

        FireClientside.getKeybindManager().registerKeybind(
                new Keybind("use_render_world", Text.of("Use"), Text.of("Use ").copy().append(getData().getShownName()), true, null,
                        this::useKey, null)
        );
    }

    private void useKey() {
        toggled = !toggled;
    }

    @Override
    public void update(MinecraftClient client) {
        if(!getData().isEnabled()) {
            toggled = false;
            return;
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

        widgets.add(FireClientside.getKeybindManager().getKeybind("use_render_world").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 - 10));

        return widgets;
    }

    public boolean isToggled() {
        return toggled;
    }
}
