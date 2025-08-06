package org.loveroo.fireclient.modules;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.data.ModuleData;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AutoMessageModule extends ModuleBase {

    private final KeyBinding toggleButton = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.fireclient.toggle_auto_message", GLFW.GLFW_KEY_Y, FireClient.KEYBIND_CATEGORY));

    private final Pattern noArgRegex = Pattern.compile("\\/[r]+");
    private final Pattern oneArgRegex = Pattern.compile("\\/[(msg)w(tell)]+ \\w+");

    private String lastMessage = "";

    public AutoMessageModule() {
        super(new ModuleData("AutoMessage", "auto_message"));

        getData().setSelectable(false);

        ClientSendMessageEvents.COMMAND.register((message) -> {
            if(!getData().isEnabled()) {
                return;
            }

            var command = "/" + message;

            var noArgRes = noArgRegex.matcher(command);
            if(noArgRes.find()) {
                lastMessage = noArgRes.group(0);
                return;
            }

            var oneArgRes = oneArgRegex.matcher(command);
            if(oneArgRes.find()) {
                lastMessage = oneArgRes.group(0);
                return;
            }
        });
    }

    @Override
    public void update(MinecraftClient client) {
        if(!getData().isEnabled()) {
            return;
        }

        if(toggleButton.wasPressed() && !lastMessage.isEmpty()) {
            client.setScreen(new ChatScreen(lastMessage + " "));
        }
    }

    @Override
    public void loadJson(JSONObject json) throws JSONException {
        getData().setEnabled(json.optBoolean("show_own", getData().isEnabled()));
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
