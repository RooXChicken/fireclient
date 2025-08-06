package org.loveroo.fireclient.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.loveroo.fireclient.modules.NametagModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class DarkerNametagMixin<T extends Entity, S extends EntityRenderState> {

    @ModifyConstant(method = "renderLabelIfPresent", constant = @Constant(intValue = -2130706433))
    private int changeColor(int original) {
        var nametag = (NametagModule) FireClientside.getModule("nametag");
        if(nametag == null || !nametag.isDarkerBackground()) {
            return original;
        }

        return 0xFFFFFFFF;
    }

    @ModifyVariable(method = "renderLabelIfPresent", at = @At("STORE"), ordinal = 2)
    private int changeBackgroundColor(int original) {
        var nametag = (NametagModule) FireClientside.getModule("nametag");
        if(nametag == null || !nametag.isDarkerBackground()) {
            return original;
        }

        return (128 << 24);
    }
}
