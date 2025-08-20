package org.loveroo.fireclient.mixin.modules.deathinfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.text.*;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.CoordinatesModule;
import org.loveroo.fireclient.modules.DeathInfoModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public abstract class DeathInfoMixin {

    @Unique
    private MutableText positionText;

    @Unique
    private int textWidth = 0;

    @Unique
    private boolean createdText = false;

    @Inject(method = "init", at = @At("TAIL"))
    private void storeLocation(CallbackInfo info) {
        if(createdText) {
            return;
        }

        createdText = true;

        var client = MinecraftClient.getInstance();
        var text = client.textRenderer;

        var xPos = String.format("%.2f ", client.player.getPos().getX());
        var yPos = String.format("%.2f ", client.player.getPos().getY());
        var zPos = String.format("%.2f ", client.player.getPos().getZ());

        var xText = String.format("X: " + xPos);
        var yText = String.format("Y: " + yPos);
        var zText = String.format("Z: " + zPos);

        var x = RooHelper.gradientText(xText, CoordinatesModule.xColor1, CoordinatesModule.xColor2);
        var y = RooHelper.gradientText(yText, CoordinatesModule.yColor1, CoordinatesModule.yColor2);
        var z = RooHelper.gradientText(zText, CoordinatesModule.zColor1, CoordinatesModule.zColor2);

        positionText = x.append(y).append(z);
        textWidth = text.getWidth(positionText) / 2;
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void showLocation(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        var deathInfo = (DeathInfoModule) FireClientside.getModule("death_info");
        if(deathInfo == null || !deathInfo.getData().isEnabled()) {
            return;
        }

        var screen = (DeathScreen)(Object)this;

        var client = MinecraftClient.getInstance();
        var text = client.textRenderer;

        context.drawText(text, positionText, screen.width/2 - textWidth, 114, 0xFFFFFFFF, true);
    }
}
