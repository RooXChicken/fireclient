package org.loveroo.fireclient.mixin.modules.perspective;

import net.minecraft.client.Camera;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.PerspectiveModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Camera.class)
public class OffsetCameraMixin {

    @ModifyVariable(method = "setRotation", at = @At("HEAD"), argsOnly = true, name = "yRot")
    private float offsetYaw(float yRot) {
        var perspective = (PerspectiveModule) FireClientside.getModule("perspective");
        if(perspective == null || !perspective.isUsing()) {
            return yRot;
        }

        return yRot + perspective.getYawOffset();
    }

    @ModifyVariable(method = "setRotation", at = @At("HEAD"), argsOnly = true, name = "xRot")
    private float offsetPitch(float xRot) {
        var perspective = (PerspectiveModule) FireClientside.getModule("perspective");
        if(perspective == null || !perspective.isUsing()) {
            return xRot;
        }

        perspective.clampPitch(xRot);
        return xRot + perspective.getPitchOffset();
    }

    @ModifyVariable(method = "alignWithEntity", at = @At("STORE"), name = "cameraDistance")
    private float increaseOffset(float cameraDistance) {
        var perspective = (PerspectiveModule) FireClientside.getModule("perspective");
        if(perspective == null || !perspective.isUsing()) {
            return cameraDistance;
        }

        perspective.clampPosition(cameraDistance);
        return cameraDistance + perspective.getPositionOffset();
    }
}
