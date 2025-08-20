package org.loveroo.fireclient.mixin.modules;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.SubtitlesHud;
import net.minecraft.client.util.math.MatrixStack;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.SubtitlesModule;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
