package org.loveroo.fireclient.modules;

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Key;
import org.loveroo.fireclient.keybind.Keybind;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AutoMessageModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0xD9D9D9);

    private final Pattern noArgRegex = Pattern.compile("\\/[r]+");
    private final Pattern oneArgRegex = Pattern.compile("\\/[(msg)w(tell)]+ \\w+");

    private boolean openGui = false;
    private String lastMessage = "";

    public AutoMessageModule() {
        super(new ModuleData("auto_message", "\uD83D\uDDE8", color));

        getData().setGuiElement(false);

        ClientSendMessageEvents.COMMAND.register(this::onChat);

        var useKey = new Keybind("use_auto_message",
                Text.translatable("fireclient.keybind.generic.use.name"),
                Text.translatable("fireclient.keybind.generic.use.description", getData().getShownName()),
                true, List.of(new Key(GLFW.GLFW_KEY_Y, Key.KeyType.KEY_CODE)),
                this::useKey, null);

        FireClientside.getKeybindManager().registerKeybind(useKey);
        FireClientside.getKeybindManager().getKeybind("use_auto_message").setCancelOnUse(true);
    }

    private void useKey() {
        openGui = true;
    }

    private void onChat(String message) {
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
    }

    @Override
    public void update(MinecraftClient client) {
        if(!getData().isEnabled() || !openGui) {
            return;
        }

        openGui = false;

        if(!lastMessage.isEmpty()) {
            client.setScreen(new ChatScreen(lastMessage + " "));
        }
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("use_auto_message").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 - 10));

        return widgets;
    }
}
