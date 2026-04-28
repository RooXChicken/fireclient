package org.loveroo.fireclient.mixin.settings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import org.loveroo.fireclient.settings.CachedEntityUUID;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public abstract class EntityUUIDCacheMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void cacheUUID(String initial, boolean isDraft, CallbackInfo ci) {
        var client = Minecraft.getInstance();

        if(client.crosshairPickEntity != null) {
            CachedEntityUUID.setCachedUUID(client.crosshairPickEntity.getUUID());
        }
        else {
            CachedEntityUUID.setCachedUUID(null);
        }
    }
}
