package org.loveroo.fireclient;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.*;
import org.jetbrains.annotations.Nullable;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.FireClientOption;

public class RooHelper {

    public static MutableText gradientText(String msg, Color color1, Color color2) {
//        if(FireClientside.getSetting(FireClientOption.DISABLE_GRADIENT) == 1) {
//            return Text.literal(msg).withColor(color1.toInt());
//        }

        var text = MutableText.of(new PlainTextContent.Literal(""));

        for(var i = 0; i < msg.length(); i++) {
            var progress = ((double)i / msg.length());
            var style = Style.EMPTY.withColor(color1.blend(color2, progress).toInt());

            text.append(MutableText.of(new PlainTextContent.Literal(msg.charAt(i) + "")).setStyle(style));
        }

        return text;
    }

    @Nullable
    public static ClientPlayNetworkHandler getNetworkHandler() {
        return MinecraftClient.getInstance().getNetworkHandler();
    }

    public static void sendChatCommand(String msg) {
        var handler = getNetworkHandler();
        if(handler == null) {
            return;
        }

        handler.sendChatCommand(msg);
    }

    public static void sendChatMessage(String msg) {
        var handler = getNetworkHandler();
        if(handler == null) {
            return;
        }

        handler.sendChatMessage(msg);
    }

    public static void sendNotification(Text name, Text description) {
        var client = MinecraftClient.getInstance();
        client.getToastManager().add(new SystemToast(SystemToast.Type.PACK_LOAD_FAILURE, name, description));
    }
}
