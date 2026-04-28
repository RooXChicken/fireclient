package org.loveroo.fireclient.mixin.modules.signs;

import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.client.player.LocalPlayer;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.SignModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class DisableSignGuiMixin {

    @Inject(method = "openTextEdit", at = @At("HEAD"), cancellable = true)
    private void cancelSignGui(SignBlockEntity sign, boolean isFrontText, CallbackInfo info) {
        var signs = (SignModule) FireClientside.getModule("sign");
        if(signs == null || !signs.isGuiDisabled()) {
            return;
        }

        info.cancel();
    }
}
