package org.loveroo.fireclient.mixin.settings;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.loveroo.fireclient.settings.CachedEntityUUID;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Collections;

@Mixin(ClientCommandSource.class)
public abstract class AddCachedEntityMixin {

    @Inject(method = "getEntitySuggestions", at = @At("HEAD"), cancellable = true)
    private void returnCachedSuggestion(CallbackInfoReturnable<Collection<String>> info) {
        if(FireClientside.getSetting(FireClientOption.CACHE_UUID) == 0) {
            return;
        }

        if(CachedEntityUUID.getCachedUUID() == null) {
            info.setReturnValue(Collections.emptyList());
            return;
        }

        info.setReturnValue(Collections.singleton(CachedEntityUUID.getCachedUUID().toString()));
    }
}
