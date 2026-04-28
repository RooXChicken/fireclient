package org.loveroo.fireclient.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;
import org.loveroo.fireclient.mixin.modules.mutesounds.GetSuggestionAccessor;
import org.loveroo.fireclient.screen.base.ScrollableWidget;
import org.loveroo.fireclient.screen.widgets.PlayerHeadWidget;
import org.loveroo.fireclient.screen.widgets.ToggleButtonWidget;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;

public class CoordsChatModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0x89ECF0);

    private final HashMap<String, ArrayList<PlayerEntry>> playerEntries = new HashMap<>();

    private static double scrollPos = 0.0;
    private final int playersWidgetWidth = 300;
    private final int playersWidgetHeight = 100;

    @Nullable
    private ScrollableWidget scroll;

    @Nullable
    private EditBox playerInputField;

    public CoordsChatModule() {
        super(new ModuleData("coords_chat", "\uD83D\uDCE8", color));

        getData().setEnabled(true);
        getData().setGuiElement(false);

        var useBind = new Keybind("use_coords_chat",
            Component.translatable("fireclient.keybind.generic.use.name"),
            Component.translatable("fireclient.keybind.generic.use.description", getData().getShownName()),
            true, null,
            this::useKey, null);

        FireClientside.getKeybindManager().registerKeybind(useBind);
    }

    private void useKey() {
        if(!getData().isEnabled()) {
            return;
        }

        var client = Minecraft.getInstance();
        if(client.player == null) {
            return;
        }

        var coordsBuilder = new StringBuilder();
        coordsBuilder.append(String.format("X: %.2f ", client.player.getX()));
        coordsBuilder.append(String.format("Y: %.2f ", client.player.getY()));
        coordsBuilder.append(String.format("Z: %.2f ", client.player.getZ()));

        var coords = coordsBuilder.toString();

        var players = getPlayers();

        for(var player : players) {
            if(!player.isEnabled()) {
                continue;
            }

            if(getOnlinePlayers().stream().noneMatch((playerName) -> player.getName().equalsIgnoreCase(playerName))) {
                continue;
            }

            RooHelper.sendChatCommand("msg " + player.getName().trim() + " " + coords);
        }
    }

    @Override
    public void loadJson(JSONObject json) throws Exception {
        super.loadJson(json);

        var serverList = json.optJSONObject("entries");
        if(serverList == null) {
            serverList = new JSONObject();
        }

        var iter = serverList.keys();
        while(iter.hasNext()) {
            var server = (String) iter.next();

            var entries = serverList.optJSONArray(server);
            if(entries == null) {
                entries = new JSONArray();
            }

            var loadedPlayers = new ArrayList<PlayerEntry>();
            for(var i = 0; i < entries.length(); i++) {
                var playerJson = entries.getJSONObject(i);

                var name = playerJson.optString("name", "");

                UUID uuid = null;
                var uuidString = playerJson.optString("uuid");
                if(uuidString != null && !uuidString.isEmpty()) {
                    uuid = UUID.fromString(uuidString);
                }

                var enabled = playerJson.optBoolean("enabled", true);

                var playerEntry = new PlayerEntry(name, uuid, enabled);
                loadedPlayers.add(playerEntry);
            }

            playerEntries.put(server, loadedPlayers);
        }
    }

    @Override
    public JSONObject saveJson() throws Exception {
        var json = super.saveJson();

        var serverList = new JSONObject();

        for(var entry : playerEntries.entrySet()) {
            var entries = new JSONArray();

            for(var player : entry.getValue()) {
                var playerJson = new JSONObject();

                playerJson.put("name", player.getName());
                if(player.getUUID() != null) {
                    playerJson.put("uuid", player.getUUID().toString());
                }

                playerJson.put("enabled", player.isEnabled());

                entries.put(playerJson);
            }

            serverList.put(entry.getKey(), entries);
        }

        json.put("entries", serverList);

        return json;
    }

    @Override
    public void moduleConfigPressed(Button button) {
        scrollPos = 0.0;
        super.moduleConfigPressed(button);
    }

    @Override
    public List<AbstractWidget> getConfigScreen(Screen base) {
        var client = Minecraft.getInstance();
        var widgets = new ArrayList<AbstractWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("use_coords_chat").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 + 95));

        playerInputField = new EditBox(client.font, base.width/2 - 140, base.height/2 - 40, playersWidgetWidth - 50, 15, Component.nullToEmpty(""));
        playerInputField.setMaxLength(24);
        playerInputField.setResponder((text) -> playerInputFieldChanged(playerInputField, text));

        widgets.add(playerInputField);

        widgets.add(Button.builder(Component.translatable("fireclient.module.coords_chat.add_player.name"), (button) -> addPlayerButtonPressed(playerInputField, false))
            .bounds(base.width/2 + 115, base.height/2 - 40, 20, 15)
            .tooltip(Tooltip.create(Component.translatable("fireclient.module.coords_chat.add_player.tooltip")))
            .build());

        var online = getOnlinePlayers().stream().map((name) -> name.toLowerCase()).collect(Collectors.toSet());

        var entries = new ArrayList<ScrollableWidget.ElementEntry>();
        for(var player : getPlayers()) {
            var entryWidgets = new ArrayList<AbstractWidget>();

            if(player.getUUID() == null) {
                if(online.contains(player.getName().toLowerCase())) {
                    var profile = getProfile(player.getName());
                    player.setUUID(profile.id());
                }
            }

            var head = new PlayerHeadWidget(player.getName(), player.getUUID(), base.width/2 - 140, 2);
            entryWidgets.add(head);

            var text = new StringWidget(Component.literal(player.getName()), base.getFont());
            text.setPosition(base.width/2 - 120, 4);

            entryWidgets.add(text);

            entryWidgets.add(new ToggleButtonWidget.ToggleButtonBuilder(null)
                .getValue(player::isEnabled)
                .setValue(player::setEnabled)
                .dimensions(base.width/2 + 90, 0, 20, 15)
                .tooltip(Tooltip.create(Component.translatable("fireclient.module.coords_chat.toggle_player.tooltip", player.getName())))
                .build());

            entryWidgets.add(Button.builder(Component.translatable("fireclient.module.coords_chat.remove_player.name").withColor(0xD63C3C), (button) -> removeplayer(player))
                .bounds(base.width/2 + 115, 0,20,15)
                .tooltip(Tooltip.create(Component.translatable("fireclient.module.coords_chat.remove_player.tooltip", player.getName())))
                .build());

            entries.add(new ScrollableWidget.ElementEntry(entryWidgets));
        }

        scroll = new ScrollableWidget(base, playersWidgetWidth, playersWidgetHeight, 0, 20, entries);
        scroll.setScrollAmount(scrollPos);
        scroll.setPosition(base.width/2 - (playersWidgetWidth/2), base.height/2 - 10);

        widgets.add(scroll);
        return widgets;
    }

    @Override
    public void openScreen(Screen base) {
        base.setFocused(playerInputField);
    }

    private ArrayList<PlayerEntry> getPlayers() {
        var ip = RooHelper.getIp();
        if(!playerEntries.containsKey(ip)) {
            playerEntries.put(ip, new ArrayList<>());
        }

        return playerEntries.getOrDefault(RooHelper.getIp(), new ArrayList<>());
    }

    private Set<String> getOnlinePlayers() {
        var client = Minecraft.getInstance();
        if(client.player == null) {
            return Set.of();
        }

        var ownName = client.player.getGameProfile().name();

        var network = RooHelper.getNetworkHandler();
        if(network == null) {
            return Set.of();
        }

        return network.getOnlinePlayers().stream()
            .map((entry) -> {
                return entry.getProfile().name();
            })
            .filter((name) -> !ownName.equalsIgnoreCase(name))
            .collect(Collectors.toUnmodifiableSet());
    }

    @Nullable
    private GameProfile getProfile(String playerName) {
        var network = RooHelper.getNetworkHandler();
        if(network == null) {
            return null;
        }

        var ids = network.getOnlinePlayers().stream()
            .filter((entry) -> entry.getProfile().name().equalsIgnoreCase(playerName))
            .toList();
        
        if(ids.isEmpty()) {
            return null;
        }
        
        return ids.getFirst().getProfile();
    }

    private void playerInputFieldChanged(EditBox widget, String text) {
        if(!text.isEmpty()) {
            var check = text.substring(text.length()-1);
            if(" ,|".contains(check)) {
                widget.setValue(text.substring(0, text.length()-1));
                addPlayerButtonPressed(widget, true);
                return;
            }
        }

        widget.setSuggestion(getSuggestion(text));
    }

    private String getSuggestion(String text) {
        if(text.isEmpty()) {
            return "";
        }

        var input = RooHelper.filterPlayerInput(text);

        if(getPlayers().stream().noneMatch((player) -> { return player.getName().equalsIgnoreCase(input); })) {
            if(getOnlinePlayers().contains(input)) {
                return "";
            }
        }

        var filteredPlayers = getOnlinePlayers().stream()
            .filter((player) -> {
                var startsWith = player.toLowerCase().startsWith(input.toLowerCase());
                if(!startsWith) {
                    return false;
                }

                return getPlayers().stream().noneMatch((entry -> entry.getName().equalsIgnoreCase(player)));
            })
            .toList();

        if(filteredPlayers.isEmpty()) {
            return "";
        }

        var playerName = filteredPlayers.getFirst();
        return playerName.substring(input.length());
    }

    private void addPlayerButtonPressed(EditBox text, boolean suggest) {
        var suggestion = ((GetSuggestionAccessor)text).getSuggestion();
        if(suggestion == null) {
            suggestion = "";
        }

        var playerName = RooHelper.filterPlayerInput(text.getValue()) + ((suggest) ? suggestion : "");

        if(playerName.isEmpty()) {
            return;
        }

        if(getPlayers().stream().anyMatch((entry -> entry.getName().equalsIgnoreCase(playerName)))) {
            RooHelper.sendNotification(
                Component.translatable("fireclient.module.coords_chat.add_player.failure.title"),
                Component.translatable("fireclient.module.coords_chat.add_player.already_exists.contents")
            );

            return;
        }

        var profile = getProfile(playerName);
        var name = "";
        UUID id = null;
        
        if(profile == null) {
            name = playerName;
        }
        else {
            name = profile.name();
            id = profile.id();
        }

        getPlayers().add(new PlayerEntry(name, id, true));
        text.setSuggestion("");
        
        reloadScreen();
    }

    private void removeplayer(PlayerEntry entry) {
        getPlayers().remove(entry);
        reloadScreen();
    }

    @Override
    public void drawScreen(Screen base, GuiGraphicsExtractor context, float delta) {
        if(scroll != null) {
            scrollPos = scroll.scrollAmount();
        }

        drawScreenHeader(context, base.width/2, base.height/2 - 70);
    }

    @Override
    public void closeScreen(Screen screen) {
        FireClientside.saveConfig();
    }

    static class PlayerEntry {

        private final String name;

        @Nullable
        private UUID uuid;

        private boolean enabled;

        public PlayerEntry(String name, @Nullable UUID uuid, boolean enabled) {
            this.name = name;
            this.uuid = uuid;

            this.enabled = enabled;
        }

        public String getName() {
            return name;
        }

        @Nullable
        public UUID getUUID() {
            return uuid;
        }

        public void setUUID(@Nullable UUID uuid) {
            this.uuid = uuid;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
