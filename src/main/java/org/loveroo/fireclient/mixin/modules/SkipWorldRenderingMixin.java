package org.loveroo.fireclient.mixin.modules;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;
import org.joml.Matrix4f;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.RenderWorldModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class SkipWorldRenderingMixin {

    @Inject(method = "renderLayer", at = @At("HEAD"), cancellable = true)
    private void skip(RenderLayer renderLayer, double x, double y, double z, Matrix4f viewMatrix, Matrix4f positionMatrix, CallbackInfo info) {
        var module = (RenderWorldModule) FireClientside.getModule("render_world");
        if(module == null || !module.isToggled()) {
            return;
        }

        info.cancel();
    }
}
