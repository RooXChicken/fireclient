package org.loveroo.fireclient.mixin.settings;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.loveroo.fireclient.screen.base.ConfigScreenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class RenderBrandingMixin {

    @Unique
    private final MutableText brandingText = RooHelper.gradientText("FireClient", FireClientside.mainColor1, FireClientside.mainColor2);

    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V", at = @At("TAIL"), cancellable = true)
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if(FireClientside.getSetting(FireClientOption.BRANDING) == 0) {
            return;
        }

        var client = MinecraftClient.getInstance();
        if(client.player == null || client.currentScreen == null || client.currentScreen instanceof ChatScreen || client.currentScreen instanceof ConfigScreenBase) {
            return;
        }

        var text = client.textRenderer;
        context.drawText(text, brandingText, 4, client.getWindow().getScaledHeight() - 12, 0xFFFFFFFF, true);
    }
}
