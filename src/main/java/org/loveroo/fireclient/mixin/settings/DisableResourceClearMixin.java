package org.loveroo.fireclient.mixin.settings;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.PackRepository;
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

import java.util.Collection;
import java.util.List;

@Mixin(Minecraft.class)
public abstract class DisableResourceClearMixin {

    @Shadow @Final
    private PackRepository resourcePackRepository;

    @Redirect(method = "clearResourcePacksOnError", at = @At(value = "INVOKE", target = "Ljava/util/List;clear()V"))
    private void disableClear(List<?> instance) {
        if(FireClientside.getSetting(FireClientOption.PREVENT_PACK_CLEAR) != 0) {
            return;
        }

        instance.clear();
    }

    @Redirect(method = "clearResourcePacksOnError", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;setSelected(Ljava/util/Collection;)V"))
    private void disableClear(PackRepository instance, Collection<String> packs) {
        ActiveResourcePacks.setEnabledPacks(instance.getSelectedIds());

        instance.setSelected(packs);
    }

    @Inject(method = "onResourceLoadFinished", at = @At("HEAD"))
    private void disableClear(Minecraft.GameLoadCookie loadCookie, CallbackInfo info) {
        if(FireClientside.getSetting(FireClientOption.PREVENT_PACK_CLEAR) == 0 || ActiveResourcePacks.getEnabledPacks() == null) {
            return;
        }

        resourcePackRepository.setSelected(ActiveResourcePacks.getEnabledPacks());
        ActiveResourcePacks.setEnabledPacks(null);
    }
}
