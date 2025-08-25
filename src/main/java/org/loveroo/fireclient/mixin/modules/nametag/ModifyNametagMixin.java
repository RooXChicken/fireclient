package org.loveroo.fireclient.mixin.modules.nametag;

import net.minecraft.client.render.entity.EntityRenderer;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.NametagModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(EntityRenderer.class)
public abstract class ModifyNametagMixin {

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

    @ModifyArg(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)V"), index = 4)
    private boolean showShadow(boolean shadow) {
        var nametag = (NametagModule) FireClientside.getModule("nametag");
        if(nametag == null || !nametag.isTextShadow()) {
            return shadow;
        }

        return true;
    }

}
