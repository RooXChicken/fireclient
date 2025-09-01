package org.loveroo.fireclient.settings;

import net.minecraft.client.MinecraftClient;

public class SprintSwimFix {
    
    private static SprintState state = SprintState.NONE;

    public void update(MinecraftClient client) {
        switch(state) {
            case NONE -> { }

            case PREVENT_SPRINT -> {
                state = SprintState.NONE;
            }
        }
    }

    public static void preventSprint() {
        state = SprintState.PREVENT_SPRINT;
    }

    public static boolean canSprint() {
        return (state != SprintState.PREVENT_SPRINT);
    }

    public enum SprintState {

        NONE,
        PREVENT_SPRINT,
    }
}
