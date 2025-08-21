package org.loveroo.fireclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Key;
import org.loveroo.fireclient.keybind.Keybind;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CoordsChatModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0x89ECF0);

    private final HashMap<String, String> playerList = new HashMap<>();

    private final String splitRegex = "[ ,|]+";
    private String lastSuggestion = "";

    @Nullable
    private TextFieldWidget playerField;

    public CoordsChatModule() {
        super(new ModuleData("coords_chat", "\uD83D\uDCE8", color));

        getData().setGuiElement(false);

        var useBind = new Keybind("use_coords_chat",
                Text.translatable("fireclient.keybind.generic.use.name"),
                Text.translatable("fireclient.keybind.generic.use.description", getData().getShownName()),
                true, null,
                this::useKey, null);

        FireClientside.getKeybindManager().registerKeybind(useBind);
    }

    private void useKey() {
        sendMessages();
    }

    private void sendMessages() {
        if(!getData().isEnabled()) {
            return;
        }

        var client = MinecraftClient.getInstance();
        if(client.player == null) {
            return;
        }

        var coordsBuilder = new StringBuilder();
        coordsBuilder.append(String.format("X: %.2f ", client.player.getPos().getX()));
        coordsBuilder.append(String.format("Y: %.2f ", client.player.getPos().getY()));
        coordsBuilder.append(String.format("Z: %.2f ", client.player.getPos().getZ()));

        var coords = coordsBuilder.toString();

        if(getCurrent().isEmpty()) {
            RooHelper.sendChatMessage(coords);
        }
        else {
            var handler = RooHelper.getNetworkHandler();
            var players = getPlayerList();

            for(var player : players) {
                var found = false;

                for(var entry : handler.getPlayerList()) {
                    if(entry.getProfile().getName().equalsIgnoreCase(player)) {
                        found = true;
                        break;
                    }
                }

                if(!found) {
                    continue;
                }

                RooHelper.sendChatCommand("msg " + player.trim() + " " + coords);
            }
        }
    }

    @Override
    public void loadJson(JSONObject json) throws JSONException {
        getData().setEnabled(json.optBoolean("enabled", getData().isEnabled()));

        var list = json.optJSONObject("player_list");

        var iter = list.keys();
        while(iter.hasNext()) {
            var entry = (String)iter.next();
            var value = list.optString(entry, "");

            playerList.put(entry, value);
        }
    }

    @Override
    public JSONObject saveJson() throws JSONException {
        var json = new JSONObject();

        json.put("enabled", getData().isEnabled());

        var list = new JSONObject();
        for(var entry : playerList.entrySet()) {
            list.put(entry.getKey(), entry.getValue().replaceAll(splitRegex, ","));
        }

        json.put("player_list", list);

        return json;
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var client = MinecraftClient.getInstance();
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("use_coords_chat").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 - 10));

        playerField = new TextFieldWidget(client.textRenderer, base.width/2 - 150, base.height/2 + 20, 300, 15, Text.of(""));
        playerField.setMaxLength(512);

        playerField.setText(getCurrent());
        playerField.setChangedListener(this::playerFieldChanged);

        widgets.add(playerField);
        return widgets;
    }

    private String getCurrent() {
        return playerList.getOrDefault(getIp(), "");
    }

    private String getIp() {
        var client = MinecraftClient.getInstance();
        if(client.getCurrentServerEntry() != null) {
            return client.getCurrentServerEntry().address;
        }
        else {
            return "__local";
        }
    }

    public void playerFieldChanged(String text) {
        var playerListEntry = getCurrent();

        if(!lastSuggestion.isEmpty() && text.matches(".*" + splitRegex)) {
            playerListEntry = playerListEntry + lastSuggestion + text.substring(text.length()-1);
            playerList.put(getIp(), playerListEntry);

            lastSuggestion = "";
            playerField.setText(playerListEntry);
            return;
        }

        playerListEntry = text;
        playerList.put(getIp(), playerListEntry);

        var list = getPlayerList();
        var suggestion = "";

        if(playerField != null) {
            String currentEntry = "";

            if(list.length > 0) {
                currentEntry = list[list.length-1].toLowerCase();
            }

            suggestion = "";

            for(var entry : filterProfiles(list)) {
                if(entry.toLowerCase().startsWith(currentEntry)) {
                    suggestion = entry.substring(currentEntry.length());
                }
            }

            playerField.setSuggestion(suggestion);
        }

        lastSuggestion = suggestion;
    }

    private List<String> filterProfiles(String[] list) {
        var names = new ArrayList<String>();

        var client = MinecraftClient.getInstance();
        var handler = RooHelper.getNetworkHandler();

        if(handler == null) {
            return names;
        }

        for(var entry : handler.getPlayerList()) {
            if(entry.getProfile().getName().equalsIgnoreCase(client.player.getGameProfile().getName())) {
                continue;
            }

            var shown = false;

            for(var name : list) {
                if(name.equalsIgnoreCase(entry.getProfile().getName())) {
                    shown = true;
                    break;
                }
            }

            if(!shown) {
                names.add(entry.getProfile().getName());
            }
        }

        return names;
    }

    private String[] getPlayerList() {
        return getCurrent().split(splitRegex);
    }

    @Override
    public void closeScreen(Screen screen) {
        FireClientside.saveConfig();
    }
}
