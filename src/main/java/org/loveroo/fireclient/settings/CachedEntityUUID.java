package org.loveroo.fireclient.settings;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CachedEntityUUID {

    @Nullable
    private static UUID cachedUUID;

    public static void setCachedUUID(@Nullable UUID cachedUUID) {
        CachedEntityUUID.cachedUUID = cachedUUID;
    }

    @Nullable
    public static UUID getCachedUUID() {
        return cachedUUID;
    }
}
