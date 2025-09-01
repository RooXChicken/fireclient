package org.loveroo.fireclient.mixin.settings;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import org.jetbrains.annotations.Nullable;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.loveroo.fireclient.settings.ActiveResourcePacks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftClient.class)
public abstract class DisableResourceClearMixin {

    @Shadow @Final
    private ResourcePackManager resourcePackManager;

    @Redirect(method = "onResourceReloadFailure", at = @At(value = "INVOKE", target = "Ljava/util/List;clear()V"))
    private void disableClear(List<?> instance) {
        if(FireClientside.getSetting(FireClientOption.PREVENT_PACK_CLEAR) != 0) {
            return;
        }

        instance.clear();
    }

    @Redirect(method = "onResourceReloadFailure", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourcePackManager;setEnabledProfiles(Ljava/util/Collection;)V"))
    private void disableClear(ResourcePackManager instance, Collection<String> enabled) {
        ActiveResourcePacks.setEnabledPacks(instance.getEnabledIds());

        instance.setEnabledProfiles(enabled);
    }

    @Inject(method = "onFinishedLoading", at = @At("HEAD"))
    private void disableClear(MinecraftClient.LoadingContext loadingContext, CallbackInfo info) {
        if(FireClientside.getSetting(FireClientOption.PREVENT_PACK_CLEAR) == 0 || ActiveResourcePacks.getEnabledPacks() == null) {
            return;
        }

        resourcePackManager.setEnabledProfiles(ActiveResourcePacks.getEnabledPacks());
        ActiveResourcePacks.setEnabledPacks(null);
    }
}
