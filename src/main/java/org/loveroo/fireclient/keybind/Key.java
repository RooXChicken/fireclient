package org.loveroo.fireclient.keybind;

import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

public class Key {

    public final int code;
    public final KeyType type;

    private boolean isPressed = false;

    public Key(int keyCode, KeyType type) {
        this.code = keyCode;
        this.type = type;
    }

    public boolean isPressed() {
        return isPressed;
    }

    public void setPressed(boolean pressed) {
        isPressed = pressed;
    }

    public boolean matches(int keyCode, int scanCode) {
        return switch(type) {
            case KEY_CODE -> isKeyCode(keyCode);
            case SCAN_CODE -> isScanCode(keyCode);
            case MOUSE -> isMouse(keyCode);
        };
    }

    private boolean isKeyCode(int keyCode) {
        return (this.type == KeyType.KEY_CODE && this.code == keyCode);
    }

    private boolean isScanCode(int scanCode) {
        return (this.type == KeyType.SCAN_CODE && this.code == scanCode);
    }

    private boolean isMouse(int keyCode) {
        return (this.type == KeyType.MOUSE && this.code == keyCode);
    }

    public String getName(boolean shortName) {
        if(code >= GLFW.GLFW_KEY_F1 && code <= GLFW.GLFW_KEY_F24) {
            return "F" + (code - GLFW.GLFW_KEY_F1 + 1);
        }

        if(code >= GLFW.GLFW_MOUSE_BUTTON_1 && code <= GLFW.GLFW_MOUSE_BUTTON_8) {
            return ((shortName) ? "\uD83D\uDDB1 " : "Mouse ") + (code - GLFW.GLFW_MOUSE_BUTTON_1);
        }

        var name = keyNames.getOrDefault(code, null);
        if(name == null) {
            var glfwKeyName = GLFW.glfwGetKeyName(code, -1);
            if(glfwKeyName == null || glfwKeyName.equalsIgnoreCase("null")) {
                return "?";
            }

            return glfwKeyName;
        }

        return (shortName) ? name.shortName() : name.name();
    }

    public enum KeyType {
        
        KEY_CODE,
        SCAN_CODE,
        MOUSE,
    }

    private static final HashMap<Integer, KeyName> keyNames = new HashMap<>();

    static {
        keyNames.put(GLFW.GLFW_KEY_LEFT_ALT, new KeyName("Left Alt", "LAlt"));
        keyNames.put(GLFW.GLFW_KEY_RIGHT_ALT, new KeyName("Right Alt", "RAlt"));

        keyNames.put(GLFW.GLFW_KEY_LEFT_CONTROL, new KeyName("Left Ctrl", "LCtrl"));
        keyNames.put(GLFW.GLFW_KEY_RIGHT_CONTROL, new KeyName("Right Ctrl", "RCtrl"));

        keyNames.put(GLFW.GLFW_KEY_LEFT_SHIFT, new KeyName("Left Shift", "LShift"));
        keyNames.put(GLFW.GLFW_KEY_RIGHT_SHIFT, new KeyName("Right Shift", "RShift"));

        keyNames.put(GLFW.GLFW_KEY_LEFT_SUPER, new KeyName("Left Super", "LSuper"));
        keyNames.put(GLFW.GLFW_KEY_RIGHT_SUPER, new KeyName("Right Super", "RSuper"));

        keyNames.put(GLFW.GLFW_KEY_ENTER, new KeyName("Enter", "Enter"));

        keyNames.put(GLFW.GLFW_KEY_PAGE_UP, new KeyName("Page Up", "PgUp"));
        keyNames.put(GLFW.GLFW_KEY_PAGE_DOWN, new KeyName("Page Down", "PgDn"));

        keyNames.put(GLFW.GLFW_KEY_TAB, new KeyName("Tab", "Tab"));
        keyNames.put(GLFW.GLFW_KEY_END, new KeyName("End", "End"));
        keyNames.put(GLFW.GLFW_KEY_HOME, new KeyName("Home", "Home"));
        keyNames.put(GLFW.GLFW_KEY_SCROLL_LOCK, new KeyName("Scroll Lock", "ScrLk"));
        keyNames.put(GLFW.GLFW_KEY_INSERT, new KeyName("Insert", "Ins"));
        keyNames.put(GLFW.GLFW_KEY_PAUSE, new KeyName("Pause", "Pause"));
        keyNames.put(GLFW.GLFW_KEY_PRINT_SCREEN, new KeyName("Print Screen", "PrtScn"));

        keyNames.put(GLFW.GLFW_KEY_BACKSPACE, new KeyName("Backspace", "Bksp"));
        keyNames.put(GLFW.GLFW_KEY_DELETE, new KeyName("Delete", "Del"));

        keyNames.put(GLFW.GLFW_KEY_SPACE, new KeyName("Space", "Space"));

        keyNames.put(GLFW.GLFW_KEY_UP, new KeyName("Up", "↑"));
        keyNames.put(GLFW.GLFW_KEY_DOWN, new KeyName("Down", "↓"));
        keyNames.put(GLFW.GLFW_KEY_LEFT, new KeyName("Left", "←"));
        keyNames.put(GLFW.GLFW_KEY_RIGHT, new KeyName("Right", "→"));

        keyNames.put(GLFW.GLFW_KEY_CAPS_LOCK, new KeyName("Caps Lock", "Caps"));
    }

    record KeyName(String name, String shortName) { }
}
