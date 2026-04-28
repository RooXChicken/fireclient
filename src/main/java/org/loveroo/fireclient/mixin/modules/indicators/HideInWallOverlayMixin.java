package org.loveroo.fireclient.mixin.modules.indicators;

import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.mojang.blaze3d.vertex.PoseStack;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.indicators.InWallIndicator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public abstract class HideInWallOverlayMixin {

    @Inject(method = "renderTex", at = @At("HEAD"), cancellable = true)
    private static void hideInWall(TextureAtlasSprite sprite, PoseStack poseStack, MultiBufferSource bufferSource, CallbackInfo info) {
        var inWallIndicator = (InWallIndicator) FireClientside.getModule("indicator_in_wall");
        if(inWallIndicator == null) {
            return;
        }

        inWallIndicator.isInWall = true;

        if(inWallIndicator.doesShowOverlay()) {
            return;
        }

        info.cancel();
    }
}