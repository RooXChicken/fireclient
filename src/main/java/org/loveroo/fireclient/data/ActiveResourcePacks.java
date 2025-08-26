package org.loveroo.fireclient.data;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class ActiveResourcePacks {

    @Nullable
    private static Collection<String> enabledPacks;

    public static void setEnabledPacks(@Nullable Collection<String> enabledPacks) {
        ActiveResourcePacks.enabledPacks = enabledPacks;
    }

    @Nullable
    public static Collection<String> getEnabledPacks() {
        return enabledPacks;
    }
}
