package org.loveroo.fireclient;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.FireClientOption;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class RooHelper {

    private static final String colorCodeCharacter = "§";

    public static MutableText gradientText(String msg, Color color1, Color color2) {
//        if(FireClientside.getSetting(FireClientOption.DISABLE_GRADIENT) == 1) {
//            return Text.literal(msg).withColor(color1.toInt());
//        }

        var text = MutableText.of(new PlainTextContent.Literal(""));

        for(var i = 0; i < msg.length();) {
            var progress = ((double)i / msg.length());
            var style = Style.EMPTY.withColor(color1.blend(color2, progress).toInt());

            var codePont = msg.codePointAt(i);
            var letter = Character.toString(codePont);

            // skip color codes for now
            // TODO: somehow make color codes work (or just end it all)
            if(letter.equals(colorCodeCharacter)) {
                // var type = msg.codePointAt(i+1);
                // text.append(MutableText.of(new PlainTextContent.Literal(letter + type)));

                i += 2;
                continue;
            }

            text.append(MutableText.of(new PlainTextContent.Literal(letter)).setStyle(style));

            i += Character.charCount(codePont);
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

        var command = (msg.startsWith("/")) ? msg.substring(1) : msg;
        handler.sendChatCommand(command);
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

    public static JSONObject jsonFromStringSafe(String message) {
        try {
            return new JSONObject(message);
        }
        catch(Exception e) {
            return new JSONObject();
        }
    }

    public static Overlay getOverlay(MinecraftClient client) {
        if(FireClientside.getSetting(FireClientOption.NO_RELOAD_OVERLAY) != 0 && client.getOverlay() instanceof SplashOverlay) {
            return null;
        }

        return client.getOverlay();
    }

    public static String filterIdInput(String input) {
        return input.replaceAll("[^a-z0-9/._-]", "");
    }

    public static String filterPlayerInput(String input) {
        return input.replaceAll("[^a-zA-Z0-9_]", "");
    }

    public static String getIp() {
        var client = MinecraftClient.getInstance();
        if(client.getCurrentServerEntry() != null) {
            return client.getCurrentServerEntry().address;
        }
        else {
            return "__local";
        }
    }

    public static String getServerBrand() {
        var client = MinecraftClient.getInstance();
        var handler = getNetworkHandler();
        if(handler == null || client.getServer() != null) {
            return "Integrated Server";
        }
        else {
            return handler.getBrand();
        }
    }
}
