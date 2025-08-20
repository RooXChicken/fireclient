package org.loveroo.fireclient.mixin.modules.subtitles;

import net.minecraft.client.gui.hud.SubtitlesHud;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SubtitlesHud.class)
public class SubtitlesMixin {

//    @Shadow @Final
//    private MinecraftClient client;
//
//    @Inject(method = "render", at = @At("HEAD"))
//    private void changeTransform(DrawContext context, CallbackInfo ci) {
//        var subtitles = (SubtitlesModule) FireClientside.getModule("subtitles");
//        if(subtitles == null) {
//            return;
//        }
//
//        var matrix = context.getMatrices();
//        matrix.push();
//
//        matrix.translate(subtitles.getData().getPosX(), subtitles.getData().getPosY(), 0);
//        matrix.scale((float)subtitles.getData().getScale(), (float)subtitles.getData().getScale(), 1.0f);
//
//        matrix.translate(-client.getWindow().getScaledWidth() + subtitles.getData().getWidth() + 2, -client.getWindow().getScaledHeight() + 39, 0);
//    }
//
//    @Inject(method = "render", at = @At("TAIL"))
//    private void revertTransform(DrawContext context, CallbackInfo ci) {
//        var subtitles = (SubtitlesModule) FireClientside.getModule("subtitles");
//        if(subtitles == null) {
//            return;
//        }
//
//        var matrix = context.getMatrices();
//        matrix.pop();
//    }
//
//    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I", shift = At.Shift.AFTER))
//    private void scaleWidget(DrawContext context, CallbackInfo info, @Local(ordinal = 0) int yIndex, @Local(ordinal = 3) int width, @Local(ordinal = 5) int height) {
//        var subtitles = (SubtitlesModule) FireClientside.getModule("subtitles");
//        if(subtitles == null) {
//            return;
//        }
//
//        var widgetWidth = width*2 - 2;
//        subtitles.getData().setWidth(widgetWidth);
//
//        var widgetHeight = 10 * (yIndex+1) - 2;
//        subtitles.getData().setHeight(widgetHeight);
//    }
}
