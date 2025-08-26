package org.loveroo.fireclient.mixin.settings;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListHud.class)
public class ShowLatencyMixin {

    @Shadow @Final
    private MinecraftClient client;

    @Unique
    private final int unknownColor = 0x787878;

    @Unique
    private final int goodColor = 0x55D640;

    @Unique
    private final int neutralColor = 0xEAED58;

    @Unique
    private final int badColor = 0xBD2222;

    @Inject(method = "renderLatencyIcon", at = @At("HEAD"), cancellable = true)
    private void renderLatency(DrawContext context, int width, int x, int y, PlayerListEntry entry, CallbackInfo info) {
        if(FireClientside.getSetting(FireClientOption.SHOW_PING_NUMBER) == 0) {
            return;
        }

        info.cancel();
    }

    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    private void getPlayerWithMs(PlayerListEntry entry, CallbackInfoReturnable<Text> info) {
        if(FireClientside.getSetting(FireClientOption.SHOW_PING_NUMBER) == 0) {
            return;
        }

        if(!entry.getProfile().getName().matches("[a-zA-Z0-9_]+")) {
            return;
        }

        var pingColor = getColor(entry);
        var pingText = MutableText.of(new PlainTextContent.Literal(" " + entry.getLatency() + "ms")).setStyle(Style.EMPTY.withColor(pingColor));

        info.setReturnValue(info.getReturnValue().copy().append(pingText));
    }

    @Unique
    private int getColor(PlayerListEntry entry) {
        var ping = entry.getLatency();
        if(ping < 0) {
            return unknownColor;
        }

        if(ping < 150) {
            return Color.fromRGB(goodColor).blend(Color.fromRGB(neutralColor), (ping/150.0)).toInt();
        }
        else {
            return Color.fromRGB(neutralColor).blend(Color.fromRGB(badColor), ((ping-150)/500.0)).toInt();
        }
    }
}
