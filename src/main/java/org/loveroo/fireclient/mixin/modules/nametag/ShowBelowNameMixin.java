package org.loveroo.fireclient.mixin.modules.nametag;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.render.entity.PlayerEntityRenderer;

@Mixin(PlayerEntityRenderer.class)
public abstract class ShowBelowNameMixin {

    // @ModifyConstant(method = "updateRenderState*", constant = @Constant(doubleValue = 100.0))
    // private double makeInfinite(double original) {
    //     var nametag = (NametagModule) FireClientside.getModule("nametag");
    //     if(nametag == null || !nametag.isUnlimitBelowName()) {
    //         return original;
    //     }

    //     return Double.MAX_VALUE;
    // }
}
