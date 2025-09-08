package org.loveroo.fireclient.mixin.modules.nametag;

import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Affiliates.NametagState;
import org.loveroo.fireclient.modules.NametagModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;

@Mixin(EntityRenderer.class)
public abstract class ModifyNametagMixin<T extends Entity, S extends EntityRenderState> {

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @ModifyConstant(method = "renderLabelIfPresent", constant = @Constant(intValue = -2130706433))
    private int changeColor(int original, @Local(ordinal = 0) S state) {
        if(getNametagState(state) == NametagState.TEXT_COLOR) {
            return 0xFFFFFFFF;
        }

        var nametag = (NametagModule) FireClientside.getModule("nametag");
        if(nametag == null || !nametag.isDarkerBackground()) {
            return original;
        }


        return 0xFFFFFFFF;
    }

    @ModifyArg(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I"), index = 0)
    private Text changeAffiliateText(Text original, @Local(ordinal = 0) S state) {
        if(getNametagState(state) != NametagState.TEXT_COLOR) {
            return original;
        }
        
        return RooHelper.gradientText(original.getString(), FireClientside.mainColor1, FireClientside.mainColor2);
    }

    @ModifyArg(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I"), index = 3)
    private int changeAffiliateColor(int original, @Local(ordinal = 0) S state) {
        if(getNametagState(state) != NametagState.TEXT_COLOR) {
            return original;
        }
        
        return 0xFFFFFFFF;
    }

    @ModifyVariable(method = "renderLabelIfPresent", at = @At("STORE"), ordinal = 2)
    private int changeBackgroundColor(int original, @Local(ordinal = 0, argsOnly = true) S state) {
        var nametag = (NametagModule) FireClientside.getModule("nametag");
        if(nametag == null || !nametag.isDarkerBackground()) {
            return original;
        }

        if(getNametagState(state) == NametagState.BACKGROUND_COLOR) {
            return 0;
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

    @Inject(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getTextBackgroundOpacity(F)F", shift = At.Shift.AFTER))
    private void renderGradient(S state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        if(getNametagState(state) != NametagState.BACKGROUND_COLOR) {
            return;
        }

        TextRenderer textRenderer = getTextRenderer();
        float textWidth = (-textRenderer.getWidth(text) / 2.0f) - 1;

        var color1 = (FireClientside.mainColor1.toInt() & 0xA0FFFFFF);
        var color2 = (FireClientside.mainColor2.toInt() & 0xA0FFFFFF);

        var matrix = matrices.peek().getPositionMatrix();
        var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getTextBackgroundSeeThrough());

		vertexConsumer.vertex(matrix, textWidth, -1, 0.000001f).color(color1).light(15); // TL
		vertexConsumer.vertex(matrix, textWidth, 9, 0.000001f).color(color1).light(15); // BL
		vertexConsumer.vertex(matrix, textWidth*-1, 9, 0.000001f).color(color2).light(15); // BR
		vertexConsumer.vertex(matrix, textWidth*-1, -1, 0.000001f).color(color2).light(15); // TR
    }

    @Unique
    private NametagState getNametagState(S state) {
        var store = (NametagModule.UUIDStorage)state;
        return FireClientside.getAffiliates().getNametagState(store.fireclient$getUUID());
    }
}
