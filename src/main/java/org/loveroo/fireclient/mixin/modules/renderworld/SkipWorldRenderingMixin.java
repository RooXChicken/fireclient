package org.loveroo.fireclient.mixin.modules.renderworld;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.SectionRenderState;
import net.minecraft.client.render.WorldRenderer;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.RenderWorldModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldRenderer.class)
public abstract class SkipWorldRenderingMixin {

    @Inject(method = "renderBlockLayers", at = @At("HEAD"), cancellable = true)
    private void skip(Matrix4fc matrix4fc, double d, double e, double f, CallbackInfoReturnable<SectionRenderState> info) {
        var module = (RenderWorldModule) FireClientside.getModule("render_world");
        if(module == null || !module.isToggled()) {
            return;
        }

        info.cancel();
    }
}
