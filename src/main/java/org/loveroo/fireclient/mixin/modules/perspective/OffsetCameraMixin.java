package org.loveroo.fireclient.mixin.modules.perspective;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.PerspectiveModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class OffsetCameraMixin {

    @ModifyVariable(method = "setRotation", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float offsetYaw(float original) {
        var perspective = (PerspectiveModule) FireClientside.getModule("perspective");
        if(perspective == null || !perspective.isUsing()) {
            return original;
        }

        return original + perspective.getYawOffset();
    }

    @ModifyVariable(method = "setRotation", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private float offsetPitch(float original) {
        var perspective = (PerspectiveModule) FireClientside.getModule("perspective");
        if(perspective == null || !perspective.isUsing()) {
            return original;
        }

        perspective.clampPitch(original);
        return original + perspective.getPitchOffset();
    }

    @ModifyVariable(method = "update", at = @At("STORE"), ordinal = 1)
    private float increaseOffset(float original) {
        var perspective = (PerspectiveModule) FireClientside.getModule("perspective");
        if(perspective == null || !perspective.isUsing()) {
            return original;
        }

        perspective.clampPosition(original);
        return original + perspective.getPositionOffset();
    }
}
