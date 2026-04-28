package org.loveroo.fireclient.mixin.modules.indicators;

import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.mojang.blaze3d.vertex.PoseStack;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.indicators.FireIndicator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public abstract class HideFireOverlayMixin {

    @Inject(method = "renderFire", at = @At("HEAD"), cancellable = true)
    private static void hideFire(PoseStack poseStack, MultiBufferSource bufferSource, TextureAtlasSprite sprite, CallbackInfo info) {
        var fireIndicator = (FireIndicator) FireClientside.getModule("indicator_fire");
        if(fireIndicator == null || fireIndicator.doesShowOverlay()) {
            return;
        }

        info.cancel();
    }
}
