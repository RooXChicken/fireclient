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
    private final int ping5Color = 0x24AB1A;

    @Unique
    private final int ping4Color = 0xEAE071;

    @Unique
    private final int ping3Color = 0xB8761C;

    @Unique
    private final int ping2Color = 0xE83F3F;

    @Unique
    private final int ping1Color = 0xA61111;

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

        var pingColor = getColor(entry);
        var pingText = MutableText.of(new PlainTextContent.Literal(" " + entry.getLatency() + "ms")).setStyle(Style.EMPTY.withColor(pingColor));

        info.setReturnValue(info.getReturnValue().copy().append(pingText));
    }

    @Unique
    private int getColor(PlayerListEntry entry) {
        if (entry.getLatency() < 0) {
            return unknownColor;
        }
        else if (entry.getLatency() < 150) {
            return ping5Color;
        }
        else if (entry.getLatency() < 300) {
            return ping4Color;
        }
        else if (entry.getLatency() < 600) {
            return ping3Color;
        }
        else if (entry.getLatency() < 1000) {
            return ping2Color;
        }
        else {
            return ping1Color;
        }
    }
}
