package org.loveroo.fireclient.keybind;

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
        return type == KeyType.KEY_CODE ? isKeyCode(keyCode) : isScanCode(scanCode);
    }

    private boolean isKeyCode(int keyCode) {
        return this.type == KeyType.KEY_CODE && this.code == keyCode;
    }

    private boolean isScanCode(int scanCode) {
        return this.type == KeyType.SCAN_CODE && this.code == scanCode;
    }

    public enum KeyType {
        KEY_CODE,
        SCAN_CODE,
    }
}
