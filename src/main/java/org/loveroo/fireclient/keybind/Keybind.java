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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class Keybind {

    private final String id;
    private Text name;
    private Text description;

    private List<Key> keys;
    private final boolean inGame;

    private boolean isPressed = false;
    private boolean cancelOnUse = false;

    protected KeyEvent onKeyPress;
    protected KeyEvent onKeyRelease;

    @Nullable
    private ButtonWidget activeRebindButton;

    private boolean rebinding = false;
    private boolean shortName = false;

    private boolean initialClickReleased = false;

    @Nullable
    private ArrayList<Key> reboundKeys;

    public Keybind(String id, Text name, Text description, boolean inGame, List<Key> keys) {
        this.id = id;
        this.name = name;
        this.description = description;

        this.keys = keys;
        this.inGame = inGame;
    }

    public Keybind(String id, Text name, Text description, boolean inGame, List<Key> keys, KeyEvent onKeyPress, KeyEvent onKeyRelease) {
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

            if(activeRebindButton != null && !activeRebindButton.isSelected()) {
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
            var client = MinecraftClient.getInstance();

            if(client.currentScreen != null) {
                return false;
            }
        }

        return true;
    }

    public ButtonWidget getRebindButton(int x, int y, int width, int height) {
        return ButtonWidget.builder(getKeysCombo(keys), this::rebindPressed)
                .dimensions(x, y, width, height)
                .tooltip(Tooltip.of(description))
                .build();
    }

    public Text getKeysCombo(List<Key> keyList) {
        var builder = new StringBuilder();
        if(keyList == null || keyList.isEmpty()) {
            builder.append((shortName) ? Text.translatable("fireclient.keybind.generic.unbound_key.short").getString() : Text.translatable("fireclient.keybind.generic.unbound_key.long").getString());

            return name.copy().append(": " + builder);
        }

        for(int i = 0; i < keyList.size(); i++) {
            var key = keyList.get(i);

            var name = switch(key.type) {
                case KEY_CODE -> getKeyCodeName(key.code);
                case SCAN_CODE -> getScanCodeName(key.code);
                case MOUSE -> getMouseName(key.code);
            };

            builder.append(name);

            if(i < keyList.size() - 1) {
                builder.append(" + ");
            }
        }

        return name.copy().append(": " + builder);
    }

    private String getKeyCodeName(int key) {
        switch(key) {
            case GLFW.GLFW_KEY_LEFT_ALT -> { return (shortName) ? "LAlt" : "Left Alt"; }
            case GLFW.GLFW_KEY_RIGHT_ALT -> { return (shortName) ? "RAlt" : "Right Alt"; }

            case GLFW.GLFW_KEY_LEFT_CONTROL -> { return (shortName) ? "LCtrl" : "Left Ctrl"; }
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> { return (shortName) ? "RCtrl" : "Right Ctrl"; }

            case GLFW.GLFW_KEY_LEFT_SHIFT -> { return (shortName) ? "LShift" : "Left Shift"; }
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> { return (shortName) ? "RShift" : "Right Shift"; }

            case GLFW.GLFW_KEY_LEFT_SUPER -> { return (shortName) ? "LSuper" : "Left Super"; }
            case GLFW.GLFW_KEY_RIGHT_SUPER -> { return (shortName) ? "RSuper" : "Right Super"; }

            case GLFW.GLFW_KEY_ENTER -> { return "Enter"; }

            case GLFW.GLFW_KEY_PAGE_UP -> { return (shortName) ? "PgUp" : "Page Up"; }
            case GLFW.GLFW_KEY_PAGE_DOWN -> { return (shortName) ? "PgDn" : "Page Down"; }

            case GLFW.GLFW_KEY_TAB -> { return "Tab"; }
            case GLFW.GLFW_KEY_END -> { return "End"; }
            case GLFW.GLFW_KEY_HOME -> { return "Home"; }
            case GLFW.GLFW_KEY_SCROLL_LOCK -> { return (shortName) ? "ScrLk" : "Scroll Lock"; }
            case GLFW.GLFW_KEY_INSERT -> { return (shortName) ? "Ins" : "Insert"; }
            case GLFW.GLFW_KEY_PAUSE -> { return "Pause"; }
            case GLFW.GLFW_KEY_PRINT_SCREEN -> { return (shortName) ? "PrtScn" : "Print Screen"; }

            case GLFW.GLFW_KEY_BACKSPACE -> { return (shortName) ? "Bksp" : "Backspace"; }
            case GLFW.GLFW_KEY_DELETE -> { return (shortName) ? "Del" : "Delete"; }

            case GLFW.GLFW_KEY_SPACE -> { return "Space"; }

            case GLFW.GLFW_KEY_UP -> { return (shortName) ? "↑" : "Up"; }
            case GLFW.GLFW_KEY_DOWN -> { return (shortName) ? "↓" : "Down"; }
            case GLFW.GLFW_KEY_LEFT -> { return (shortName) ? "←" : "Left"; }
            case GLFW.GLFW_KEY_RIGHT -> { return (shortName) ? "→" : "Right"; }
        }

        if(key >= GLFW.GLFW_KEY_F1 && key <= GLFW.GLFW_KEY_F24) {
            return "F" + (key - GLFW.GLFW_KEY_F1 + 1);
        }

        return GLFW.glfwGetKeyName(key, -1);
    }

    private String getMouseName(int key) {
        if(key >= GLFW.GLFW_MOUSE_BUTTON_1 && key <= GLFW.GLFW_MOUSE_BUTTON_8) {
            return ((shortName) ? "\uD83D\uDDB1 " : "Mouse ") + (key - GLFW.GLFW_MOUSE_BUTTON_1);
        }

        return GLFW.glfwGetKeyName(key, -1);
    }

    private String getScanCodeName(int key) {
        switch(key) {
            default -> { return GLFW.glfwGetKeyName(-1, key); }
        }
    }

    private void rebindPressed(ButtonWidget button) {
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

    public Text getName() {
        return name;
    }

    public Text getDescription() {
        return description;
    }

    public void setName(Text name) {
        this.name = name;
    }

    public void setDescription(Text description) {
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
