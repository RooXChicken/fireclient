package org.loveroo.fireclient.mixin.modules.perspective;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.PerspectiveModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class PreventPlayerRotationMixin {

    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void preventLook(double cursorDeltaX, double cursorDeltaY, CallbackInfo info) {
        var client = MinecraftClient.getInstance();
        if(client.player != (Object)this) {
            return;
        }

        var perspective = (PerspectiveModule) FireClientside.getModule("perspective");
        if(perspective == null || !perspective.isUsing()) {
            return;
        }

        float pitchDelta = (float)cursorDeltaY * 0.15F;
        float yawDelta = (float)cursorDeltaX * 0.15F;

        perspective.addDelta(yawDelta, pitchDelta);

        info.cancel();
    }
}
