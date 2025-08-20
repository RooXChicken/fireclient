package org.loveroo.fireclient.mixin.modules.perspective;

import net.minecraft.client.option.Perspective;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.PerspectiveModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Perspective.class)
public class SetPerspectiveMixin {

    @Inject(method = "isFirstPerson", at = @At("RETURN"), cancellable = true)
    private void makeThirdPerson(CallbackInfoReturnable<Boolean> info) {
        var perspective = (PerspectiveModule) FireClientside.getModule("perspective");
        if(perspective == null || !perspective.isUsing()) {
            return;
        }

        info.setReturnValue(false);
    }

    @Inject(method = "isFrontView", at = @At("RETURN"), cancellable = true)
    private void makeUnInverted(CallbackInfoReturnable<Boolean> info) {
        var perspective = (PerspectiveModule) FireClientside.getModule("perspective");
        if(perspective == null || !perspective.isUsing()) {
            return;
        }

        info.setReturnValue(false);
    }
}
