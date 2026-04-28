package org.loveroo.fireclient.mixin.modules.nametag;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.renderer.entity.EntityRenderer;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.NametagModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.client.renderer.entity.player.AvatarRenderer;

@Mixin(EntityRenderer.class)
public abstract class ShowBelowNameMixin {

    @ModifyExpressionValue(method = "extractRenderState*", at = @At(value = "CONSTANT", args = "doubleValue=100.0"))
    private double makeInfinite(double original) {
        var nametag = (NametagModule) FireClientside.getModule("nametag");
        if(nametag == null || !nametag.isUnlimitBelowName()) {
            return original;
        }

        return Double.MAX_VALUE;
    }
}
