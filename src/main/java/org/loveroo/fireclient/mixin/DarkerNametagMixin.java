package org.loveroo.fireclient.mixin;

import net.minecraft.client.render.entity.EntityRenderer;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRenderer.class)
public class DarkerNametagMixin {

    @ModifyConstant(method = "renderLabelIfPresent", constant = @Constant(intValue = -2130706433))
    private int changeColor(int original) {
        if(FireClientside.getSetting(FireClientOption.DARKER_NAMETAGS) == 0) {
            return original;
        }

        return 0xFFFFFFFF;
    }

    @ModifyVariable(method = "renderLabelIfPresent", at = @At("STORE"), ordinal = 2)
    private int changeBackgroundColor(int original) {
        if(FireClientside.getSetting(FireClientOption.DARKER_NAMETAGS) == 0) {
            return original;
        }

        return (128 << 24);
    }
}
