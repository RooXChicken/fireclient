package org.loveroo.fireclient.mixin.modules.nametag;

import java.util.HashSet;
import java.util.UUID;

import net.minecraft.client.renderer.feature.NameTagFeatureRenderer;
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

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.Component;

@Mixin(EntityRenderer.class)
public abstract class ModifyNametagMixin<T extends Entity, S extends EntityRenderState> implements NametagModule.UUIDStorage, NametagModule.NameStorage {

    @Unique
    private UUID uuid = UUID.randomUUID();

    @Unique
    private String name = "";

    @Override
    public UUID fireclient$getUUID() {
        return uuid;
    }

    @Override
    public void fireclient$setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String fireclient$getName() {
        return name;
    }

    @Override
    public void fireclient$setName(String name) {
        this.name = name;
    }

    @Shadow
    public abstract Font getFont();

//    @ModifyArg(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitLabel(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Vec3d;ILnet/minecraft/text/Text;ZIDLnet/minecraft/client/render/state/CameraRenderState;)V"), index = 0)
//    private MatrixStack changeAffiliateText(MatrixStack original, @Local(ordinal = 0, argsOnly = true) S state) {
//        if(getNametagState() != NametagState.TEXT_COLOR) {
//            return original;
//        }
//
//        // copy the original to prevent a concurrent modification exception
//        var originalCopy = original.copy();
//        var finalText = MutableText.of(PlainTextContent.of(""));
//
//        var textList = new HashSet<Text>();
//        if(originalCopy.getSiblings().size() <= 0) {
//            textList.add(originalCopy);
//        }
//        else {
//            textList.addAll(originalCopy.getSiblings());
//        }
//
//        for(var text : textList) {
//            var string = text.getString();
//
//            if(string.equals(name)) {
//                finalText.append(RooHelper.gradientText(string, FireClientside.mainColor1, FireClientside.mainColor2));
//            }
//            else {
//                finalText.append(text);
//            }
//        }
//
//        return finalText;
//    }
//
//    @ModifyArg(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)V"), index = 3)
//    private int changeAffiliateColor(int original, @Local(ordinal = 0, argsOnly = true) S state) {
//        if(getNametagState() != NametagState.TEXT_COLOR) {
//            return original;
//        }
//
//        return 0xFFFFFFFF;
//    }
//
//    @ModifyVariable(method = "renderLabelIfPresent", at = @At("STORE"), ordinal = 2)
//    private int changeBackgroundColor(int original, @Local(ordinal = 0, argsOnly = true) S state) {
//        var nametag = (NametagModule) FireClientside.getModule("nametag");
//        if(nametag == null || !nametag.isDarkerBackground()) {
//            return original;
//        }
//
//        if(getNametagState() == NametagState.BACKGROUND_COLOR) {
//            return 0;
//        }
//
//        return (128 << 24);
//    }

    @ModifyArg(method = "submitNameDisplay(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitNameTag(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/phys/Vec3;ILnet/minecraft/network/chat/Component;ZIDLnet/minecraft/client/renderer/state/level/CameraRenderState;)V"), index = 4)
    private boolean showShadow(boolean shadow) {
        var nametag = (NametagModule) FireClientside.getModule("nametag");
        if(nametag == null || !nametag.isTextShadow()) {
            return shadow;
        }

        return true;
    }

//    @Inject(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitLabel(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Vec3d;ILnet/minecraft/text/Text;ZIDLnet/minecraft/client/render/state/CameraRenderState;)V", shift = At.Shift.AFTER))
//    private void renderGradient(S state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
//        if(getNametagState() != NametagState.BACKGROUND_COLOR) {
//            return;
//        }
//
//        TextRenderer textRenderer = getTextRenderer();
//        float textWidth = (-textRenderer.getWidth(text) / 2.0f) - 1;
//
//        var color1 = (FireClientside.mainColor1.toInt() & 0xA0FFFFFF);
//        var color2 = (FireClientside.mainColor2.toInt() & 0xA0FFFFFF);
//
//        var matrix = matrices.peek().getPositionMatrix();
//        var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getTextBackgroundSeeThrough());
//
//        // z value is super small but non-zero as to fix a depth issue but not cause it to be off-center
//		vertexConsumer.vertex(matrix, textWidth, -1, 0.000001f).color(color1).light(15); // TL
//		vertexConsumer.vertex(matrix, textWidth, 9, 0.000001f).color(color1).light(15); // BL
//		vertexConsumer.vertex(matrix, textWidth*-1, 9, 0.000001f).color(color2).light(15); // BR
//		vertexConsumer.vertex(matrix, textWidth*-1, -1, 0.000001f).color(color2).light(15); // TR
//    }

    @Unique
    private NametagState getNametagState() {
        return FireClientside.getAffiliates().getNametagState(fireclient$getUUID());
    }
}

@Mixin(NameTagFeatureRenderer.Storage.class)
class ChangeNametagColor {

    @ModifyConstant(method = "add", constant = @Constant(intValue = -2130706433))
    private int changeColor(int original) {
//        if(getNametagState() == NametagState.TEXT_COLOR) {
//            return 0xFFFFFFFF;
//        }

        var nametag = (NametagModule) FireClientside.getModule("nametag");
        if(nametag == null || !nametag.isDarkerBackground()) {
            return original;
        }


        return 0xFFFFFFFF;
    }
}
