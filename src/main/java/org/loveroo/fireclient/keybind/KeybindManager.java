package org.loveroo.fireclient.keybind;

import org.jetbrains.annotations.Nullable;
import org.loveroo.fireclient.FireClient;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class KeybindManager {

    private final List<Keybind> keybinds = new ArrayList<>();

    public boolean onKey(int keyCode, int scanCode, int action, int modifiers) {
        for(var keybind : keybinds) {
            var status = keybind.onKey(keyCode, scanCode, action, modifiers);

            if(status == KeyReturnStatus.CANCEL) {
                return false;
            }
        }

        return true;
    }

    public boolean hasKey(String id) {
        return keybinds.stream().anyMatch(bind -> bind.getId().equalsIgnoreCase(id));
    }

    public void registerKeybind(Keybind keybind) {
        if(hasKey(keybind.getId())) {
            return;
        }

        keybinds.add(keybind);
    }

    public void unregisterKeybind(String id) {
        for(var i = 0; i < keybinds.size(); i++) {
            if(keybinds.get(i).getId().equalsIgnoreCase(id)) {
                keybinds.remove(i--);
            }
        }
    }

    @Nullable
    public Keybind getKeybind(String id) {
        for(var keybind : keybinds) {
            if(keybind.getId().equalsIgnoreCase(id)) {
                return keybind;
            }
        }

        return null;
    }

    public List<Keybind> getKeybinds() {
        return keybinds;
    }
}
