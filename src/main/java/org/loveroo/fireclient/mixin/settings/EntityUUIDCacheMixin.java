package org.loveroo.fireclient.mixin.settings;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import org.jetbrains.annotations.Nullable;
import org.loveroo.fireclient.settings.CachedEntityUUID;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ChatScreen.class)
public abstract class EntityUUIDCacheMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void cacheUUID(String originalChatText, CallbackInfo info) {
        var client = MinecraftClient.getInstance();

        if(client.targetedEntity != null) {
            CachedEntityUUID.setCachedUUID(client.targetedEntity.getUuid());
        }
        else {
            CachedEntityUUID.setCachedUUID(null);
        }
    }
}
