package org.loveroo.fireclient.keybind;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.keybind.Key.KeyType;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class Keybind {

    private final String id;
    private Component name;
    private Component description;

    private List<Key> keys;
    private final boolean inGame;

    private boolean isPressed = false;
    private boolean cancelOnUse = false;

    protected KeyEvent onKeyPress;
    protected KeyEvent onKeyRelease;

    @Nullable
    private Button activeRebindButton;

    private boolean rebinding = false;
    private boolean shortName = false;

    private boolean initialClickReleased = false;

    @Nullable
    private ArrayList<Key> reboundKeys;

    public Keybind(String id, Component name, Component description, boolean inGame, List<Key> keys) {
        this.id = id;
        this.name = name;
        this.description = description;

        this.keys = keys;
        this.inGame = inGame;
    }

    public Keybind(String id, Component name, Component description, boolean inGame, List<Key> keys, KeyEvent onKeyPress, KeyEvent onKeyRelease) {
        this(id, name, description, inGame, keys);

        this.onKeyPress = onKeyPress;
        this.onKeyRelease = onKeyRelease;
    }

    public KeyReturnStatus onKey(KeyType type, int keyCode, int scanCode, int action, int modifiers) {
        if(rebinding) {
            if(reboundKeys == null) {
                reboundKeys = new ArrayList<>();
            }

            if(keyCode == GLFW.GLFW_MOUSE_BUTTON_1 && !initialClickReleased) {
                initialClickReleased = true;
                return KeyReturnStatus.CANCEL;
            }

            if(keyCode == GLFW.GLFW_KEY_ESCAPE) {
                reboundKeys.clear();
                completeRebind();

                return KeyReturnStatus.CANCEL;
            }

            if(activeRebindButton != null && !activeRebindButton.isHoveredOrFocused()) {
                completeRebind();
            }

            switch(action) {
                case GLFW.GLFW_PRESS -> {
                    var newKey = new Key(keyCode, type);
                    newKey.setPressed(true);

                    reboundKeys.add(newKey);
                    updateRebind();
                }

                case GLFW.GLFW_RELEASE -> {
                    completeRebind();
                }
            }

            return KeyReturnStatus.CANCEL;
        }

        if(keys == null || keys.isEmpty()) {
            return KeyReturnStatus.ALLOW;
        }

        for(var key : keys) {
            if(key.matches(keyCode, scanCode)) {
                switch(action) {
                    case GLFW.GLFW_PRESS -> key.setPressed(true);
                    case GLFW.GLFW_RELEASE -> key.setPressed(false);
                }
            }
        }

        if(!isPressed) {
            if(allKeysPressed(keys)) {
                isPressed = true;

                if(onKeyPress != null && inGameCheck()) {
                    onKeyPress.action();
                }

                if(cancelOnUse) {
                    return KeyReturnStatus.CANCEL;
                }
            }
        }
        else {
            if(!allKeysPressed(keys)) {
                isPressed = false;

                if(onKeyRelease != null) {
                    onKeyRelease.action();
                }
            }
        }

        return KeyReturnStatus.ALLOW;
    }

    private boolean allKeysPressed(List<Key> keyList) {
        return keyList.stream().allMatch(Key::isPressed);
    }

    private boolean inGameCheck() {
        if(inGame) {
            var client = Minecraft.getInstance();

            if(client.screen != null) {
                return false;
            }
        }

        return true;
    }

    public Button getRebindButton(int x, int y, int width, int height) {
        return Button.builder(getKeysCombo(keys), this::rebindPressed)
            .bounds(x, y, width, height)
            .tooltip(Tooltip.create(description))
            .build();
    }

    public Component getKeysCombo(List<Key> keyList) {
        var builder = new StringBuilder();
        if(keyList == null || keyList.isEmpty()) {
            builder.append((shortName) ? Component.translatable("fireclient.keybind.generic.unbound_key.short").getString() : Component.translatable("fireclient.keybind.generic.unbound_key.long").getString());

            return name.copy().append(": " + builder);
        }

        for(int i = 0; i < keyList.size(); i++) {
            var key = keyList.get(i);
            var name = key.getName(shortName);

            builder.append(name);

            if(i < keyList.size() - 1) {
                builder.append(" + ");
            }
        }

        return name.copy().append(": " + builder);
    }

    private void rebindPressed(Button button) {
        if(rebinding) {
            rebinding = false;
            button.setMessage(getKeysCombo(keys));

            return;
        }

        beginRebind();

        activeRebindButton = button;
        button.setMessage(name.copy().append(" ..."));
    }

    private void beginRebind() {
        rebinding = true;
        initialClickReleased = false;
        reboundKeys = new ArrayList<>();
    }

    private void updateRebind() {
        if(activeRebindButton == null) {
            return;
        }

        activeRebindButton.setMessage(getKeysCombo(reboundKeys).copy().append(" ..."));
    }

    private void completeRebind() {
        rebinding = false;
        keys = reboundKeys;

        if(activeRebindButton == null) {
            return;
        }

        activeRebindButton.setMessage(getKeysCombo(keys));
        FireClientside.saveConfig();

        for(var key : keys) {
            key.setPressed(false);
        }
    }

    public void loadJson(JSONArray array) throws JSONException {
        reboundKeys = new ArrayList<>();

        for(var i = 0; i < array.length(); i++) {
            var keyJson = array.getJSONObject(i);

            var code = keyJson.optInt("code", GLFW.GLFW_KEY_UNKNOWN);
            var type = Key.KeyType.values()[keyJson.optInt("type", 0)];

            var key = new Key(code, type);
            reboundKeys.add(key);
        }

        keys = reboundKeys;
    }

    public JSONArray saveJson() throws JSONException {
        var json = new JSONArray();

        if(keys == null) {
            return json;
        }

        for(var key : keys) {
            var keyJson = new JSONObject();

            keyJson.put("code", key.code);
            keyJson.put("type", key.type.ordinal());

            json.put(keyJson);
        }

        return json;
    }

    public String getId() {
        return id;
    }

    public List<Key> getKeys() {
        return keys;
    }

    public void setKeyPressCallback(KeyEvent onKeyPress) {
        this.onKeyPress = onKeyPress;
    }

    public void setKeyReleaseCallback(KeyEvent onKeyRelease) {
        this.onKeyRelease = onKeyRelease;
    }

    public boolean isCancelOnUse() {
        return cancelOnUse;
    }

    public void setCancelOnUse(boolean cancelOnUse) {
        this.cancelOnUse = cancelOnUse;
    }

    public boolean isShortName() {
        return shortName;
    }

    public void setShortName(boolean shortName) {
        this.shortName = shortName;
    }

    public Component getName() {
        return name;
    }

    public Component getDescription() {
        return description;
    }

    public void setName(Component name) {
        this.name = name;
    }

    public void setDescription(Component description) {
        this.description = description;
    }

    public interface KeyEvent {
        void action();
    }

    public enum KeyReturnStatus {
        ALLOW,
        CANCEL,
    }
}
