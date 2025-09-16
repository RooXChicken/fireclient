package org.loveroo.fireclient.mixin.modules.nametag;

import org.loveroo.fireclient.modules.NametagModule;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;

@Mixin(EntityRenderer.class)
public abstract class ModifyNametagMixin<T extends Entity> implements NametagModule.UUIDStorage, NametagModule.NameStorage {

    // @Unique
    // private UUID uuid = UUID.randomUUID();

    // @Unique
    // private String name = "";

    // @Override
    // public UUID fireclient$getUUID() {
    //     return uuid;
    // }

    // @Override
    // public void fireclient$setUUID(UUID uuid) {
    //     this.uuid = uuid;
    // }

    // @Override
    // public String fireclient$getName() {
    //     return name;
    // }

    // @Override
    // public void fireclient$setName(String name) {
    //     this.name = name;
    // }

    // @Shadow
    // public abstract TextRenderer getTextRenderer();

    // @ModifyConstant(method = "renderLabelIfPresent", constant = @Constant(intValue = -2130706433))
    // private int changeColor(int original) {
    //     if(getNametagState() == NametagState.TEXT_COLOR) {
    //         return 0xFFFFFFFF;
    //     }

    //     var nametag = (NametagModule) FireClientside.getModule("nametag");
    //     if(nametag == null || !nametag.isDarkerBackground()) {
    //         return original;
    //     }


    //     return 0xFFFFFFFF;
    // }

    // @ModifyArg(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I"), index = 0)
    // private Text changeAffiliateText(Text original) {
    //     if(getNametagState() != NametagState.TEXT_COLOR) {
    //         return original;
    //     }

    //     var finalText = MutableText.of(PlainTextContent.of(""));

    //     var textList = new HashSet<Text>();
    //     if(original.getSiblings().size() <= 0) {
    //         textList.add(original);
    //     }
    //     else {
    //         textList.addAll(original.getSiblings());
    //     }

    //     for(var text : textList) {
    //         var string = text.getString();

    //         if(string.equals(name)) {
    //             finalText.append(RooHelper.gradientText(string, FireClientside.mainColor1, FireClientside.mainColor2));
    //         }
    //         else {
    //             finalText.append(text);
    //         }
    //     }
        
    //     return finalText;
    // }

    // @ModifyArg(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I"), index = 3)
    // private int changeAffiliateColor(int original) {
    //     if(getNametagState() != NametagState.TEXT_COLOR) {
    //         return original;
    //     }
        
    //     return 0xFFFFFFFF;
    // }

    // @ModifyVariable(method = "renderLabelIfPresent", at = @At("STORE"), ordinal = 2)
    // private int changeBackgroundColor(int original) {
    //     var nametag = (NametagModule) FireClientside.getModule("nametag");
    //     if(nametag == null || !nametag.isDarkerBackground()) {
    //         return original;
    //     }

    //     if(getNametagState() == NametagState.BACKGROUND_COLOR) {
    //         return 0;
    //     }

    //     return (128 << 24);
    // }

    // @ModifyArg(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I"), index = 4)
    // private boolean showShadow(boolean shadow) {
    //     var nametag = (NametagModule) FireClientside.getModule("nametag");
    //     if(nametag == null || !nametag.isTextShadow()) {
    //         return shadow;
    //     }

    //     return true;
    // }

    // @Inject(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getTextBackgroundOpacity(F)F", shift = At.Shift.AFTER))
    // private void renderGradient(Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
    //     if(getNametagState() != NametagState.BACKGROUND_COLOR) {
    //         return;
    //     }

    //     TextRenderer textRenderer = getTextRenderer();
    //     float textWidth = (-textRenderer.getWidth(text) / 2.0f) - 1;

    //     var color1 = (FireClientside.mainColor1.toInt() & 0xA0FFFFFF);
    //     var color2 = (FireClientside.mainColor2.toInt() & 0xA0FFFFFF);

    //     var matrix = matrices.peek().getPositionMatrix();
    //     var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getTextBackgroundSeeThrough());

    //     // z value is super small but non-zero as to fix a depth issue but not cause it to be off-center
	// 	vertexConsumer.vertex(matrix, textWidth, -1, 0.000001f).color(color1).light(15); // TL
	// 	vertexConsumer.vertex(matrix, textWidth, 9, 0.000001f).color(color1).light(15); // BL
	// 	vertexConsumer.vertex(matrix, textWidth*-1, 9, 0.000001f).color(color2).light(15); // BR
	// 	vertexConsumer.vertex(matrix, textWidth*-1, -1, 0.000001f).color(color2).light(15); // TR
    // }

    // @Unique
    // private NametagState getNametagState() {
    //     return FireClientside.getAffiliates().getNametagState(fireclient$getUUID());
    // }
}
