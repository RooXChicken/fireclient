package org.loveroo.fireclient.mixin.settings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
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

@Mixin(PlayerTabOverlay.class)
public class ShowLatencyMixin {

    @Shadow @Final
    private Minecraft minecraft;

    @Unique
    private final int unknownColor = 0x787878;

    @Unique
    private final int goodColor = 0x55D640;

    @Unique
    private final int neutralColor = 0xEAED58;

    @Unique
    private final int badColor = 0xBD2222;

    @Inject(method = "extractPingIcon", at = @At("HEAD"), cancellable = true)
    private void renderLatency(GuiGraphicsExtractor graphics, int slotWidth, int xo, int yo, PlayerInfo entry, CallbackInfo info) {
        if(FireClientside.getSetting(FireClientOption.SHOW_PING_NUMBER) == 0) {
            return;
        }

        info.cancel();
    }

    @Inject(method = "getNameForDisplay", at = @At("RETURN"), cancellable = true)
    private void getPlayerWithMs(PlayerInfo entry, CallbackInfoReturnable<Component> info) {
        if(FireClientside.getSetting(FireClientOption.SHOW_PING_NUMBER) == 0) {
            return;
        }

        var pingColor = getColor(entry);
        if(pingColor == 0) {
            return;
        }

        var pingText = MutableComponent.create(new PlainTextContents.LiteralContents(" " + entry.getLatency() + "ms")).setStyle(Style.EMPTY.withColor(pingColor));
        info.setReturnValue(info.getReturnValue().copy().append(pingText));
    }

    @Unique
    private int getColor(PlayerInfo entry) {
        var ping = entry.getLatency();
        if(ping < 0 || ping > 9999) {
            return 0;
        }

        if(ping < 150) {
            return Color.fromRGB(goodColor).blend(Color.fromRGB(neutralColor), (ping/150.0)).toInt();
        }
        else {
            return Color.fromRGB(neutralColor).blend(Color.fromRGB(badColor), ((ping-150)/500.0)).toInt();
        }
    }
}
