package org.loveroo.fireclient;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.*;
import org.loveroo.fireclient.data.Color;

public class RooHelper {

    public static MutableText gradientText(String msg, Color color1, Color color2) {
        var text = MutableText.of(new PlainTextContent.Literal(""));

        for(var i = 0; i < msg.length(); i++) {
            var progress = ((i+0.0) / msg.length());
            var style = Style.EMPTY.withColor(color1.blend(color2, progress).toInt());

            text.append(MutableText.of(new PlainTextContent.Literal(msg.charAt(i) + "")).setStyle(style));
        }

        return text;
    }

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

    public static void sendNotification(String name, String description) {
        sendNotification(Text.of(name), Text.of(description));
    }

    public static void sendNotification(Text name, Text description) {
        var client = MinecraftClient.getInstance();
        client.getToastManager().add(new SystemToast(SystemToast.Type.PACK_LOAD_FAILURE, name, description));
    }

    public static String toSmallText(String text) {
        var smallText = new StringBuilder();
        var array = text.toCharArray();

        for(var character : array) {
            smallText.append(getSmallCharacter(character));
        }

        return smallText.toString();
    }

    private static String getSmallCharacter(char character) {
        String small = character + "";

        switch(character) {
            case 'a' -> small = "ᴀ";
            case 'b' -> small = "ʙ";
            case 'c' -> small = "ᴄ";
            case 'd' -> small = "ᴅ";
            case 'e' -> small = "ᴇ";
            case 'f' -> small = "ꜰ";
            case 'g' -> small = "ɢ";
            case 'h' -> small = "ʜ";
            case 'i' -> small = "ɪ";
            case 'j' -> small = "ᴊ";
            case 'k' -> small = "ᴋ";
            case 'l' -> small = "ʟ";
            case 'm' -> small = "ᴍ";
            case 'n' -> small = "ɴ";
            case 'o' -> small = "ᴏ";
            case 'p' -> small = "ᴘ";
            case 'q' -> small = "ǫ";
            case 'r' -> small = "ʀ";
            case 's' -> small = "ѕ";
            case 't' -> small = "ᴛ";
            case 'u' -> small = "ᴜ";
            case 'v' -> small = "ᴠ";
            case 'w' -> small = "ᴡ";
            case 'x' -> small = "х";
            case 'y' -> small = "ʏ";
            case 'z' -> small = "ᴢ";
            case '1' -> small = "₁";
            case '2' -> small = "₂";
            case '3' -> small = "₃";
            case '4' -> small = "₄";
            case '5' -> small = "₅";
            case '6' -> small = "₆";
            case '7' -> small = "₇";
            case '8' -> small = "₈";
            case '9' -> small = "₉";
            case '0' -> small = "₀";
        }

        return small;
    }
}
