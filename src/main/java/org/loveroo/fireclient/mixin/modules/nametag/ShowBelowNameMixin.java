package org.loveroo.fireclient.mixin.modules.nametag;

import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.NametagModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.client.render.entity.PlayerEntityRenderer;

@Mixin(PlayerEntityRenderer.class)
public abstract class ShowBelowNameMixin {

    @ModifyConstant(method = "updateRenderState*", constant = @Constant(doubleValue = 100.0))
    private double makeInfinite(double original) {
        var nametag = (NametagModule) FireClientside.getModule("nametag");
        if(nametag == null || !nametag.isUnlimitBelowName()) {
            return original;
        }

        return Double.MAX_VALUE;
    }
}
