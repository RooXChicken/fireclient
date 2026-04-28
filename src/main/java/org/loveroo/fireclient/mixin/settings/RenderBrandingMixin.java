package org.loveroo.fireclient.mixin.settings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.MutableComponent;
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
    private final MutableComponent brandingText = RooHelper.gradientText("FireClient", FireClientside.mainColor1, FireClientside.mainColor2);

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo info) {
        if(FireClientside.getSetting(FireClientOption.BRANDING) == 0) {
            return;
        }

        var client = Minecraft.getInstance();
        if(client.player == null || client.screen == null || client.screen instanceof ChatScreen || client.screen instanceof ConfigScreenBase) {
            return;
        }

        var text = client.font;
        graphics.text(text, brandingText, 4, client.getWindow().getGuiScaledHeight() - 12, 0xFFFFFFFF, true);
    }
}
