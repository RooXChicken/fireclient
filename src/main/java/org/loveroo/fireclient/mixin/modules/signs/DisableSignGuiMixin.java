package org.loveroo.fireclient.mixin.modules.signs;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.SignModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class DisableSignGuiMixin {

    @Inject(method = "openEditSignScreen", at = @At("HEAD"), cancellable = true)
    private void cancelSignGui(SignBlockEntity sign, boolean front, CallbackInfo info) {
        var signs = (SignModule) FireClientside.getModule("sign");
        if(signs == null || !signs.isGuiDisabled()) {
            return;
        }

        info.cancel();
    }
}
