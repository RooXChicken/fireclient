package org.loveroo.fireclient;

import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.json.JSONObject;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.FireClientOption;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.entity.player.PlayerEntity;
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

    /**
     * Modified from {@link net.minecraft.client.gui.screen.ingame.InventoryScreen#drawEntity(DrawContext, int, int, int, int, int, float, float, float, net.minecraft.entity.LivingEntity)}
     */
    public static void drawPlayer(DrawContext context, int x, int y, float scale, float mouseX, float mouseY) {
        drawPlayer(context, x, y, scale, mouseX, mouseY, 0, 0, (player) -> {});
	}

    public static void drawPlayer(DrawContext context, int x, int y, float scale, float mouseX, float mouseY, float yawOffset, float pitchOffset) {
        drawPlayer(context, x, y, scale, mouseX, mouseY, yawOffset, pitchOffset, (player) -> {});
	}

    public static void drawPlayer(DrawContext context, int x, int y, float scale, float mouseX, float mouseY, PlayerRenderModifier mod) {
        drawPlayer(context, x, y, scale, mouseX, mouseY, 0, 0, mod);
	}

    public static void drawPlayer(
        DrawContext context, 
        int x, 
        int y, 
        float scale, 
        float mouseX, 
        float mouseY, 
        float yawOffset, 
        float pitchOffset,
        PlayerRenderModifier mod) {
            
        var client = MinecraftClient.getInstance();
        var entity = client.player;

        if(entity == null) {
            return;
        }

        final var off = 50;

        var x1 = (x+26-off)*2;
        var x2 = (x+75-off)*2;

        var y1 = (y-8-off)*2;
        var y2 = (y+78-off)*2;

        final var size = 30;

		float f = (x1 + x2) / 2.0F;
		float g = (y1 + y2) / 2.0F;
		context.enableScissor(x1, y1, x2, y2);
		float h = (float)Math.atan((f - mouseX) / 40.0F);
		float i = (float)Math.atan((g - mouseY) / 40.0F);
		Quaternionf quaternionf = new Quaternionf().rotateZ((float) Math.PI);
		Quaternionf quaternionf2 = new Quaternionf().rotateX(i * 20.0F * (float) (Math.PI / 180.0));
		quaternionf.mul(quaternionf2);
		float j = entity.bodyYaw;
		float k = entity.getYaw();
		float l = entity.getPitch();
		float m = entity.prevHeadYaw;
		float n = entity.headYaw;
		entity.bodyYaw = 180.0F + h * 20.0F + yawOffset;
		entity.setYaw(180.0F + h * 40.0F + yawOffset);
		entity.setPitch(-i * 20.0F + pitchOffset);
		entity.headYaw = entity.getYaw();
		entity.prevHeadYaw = entity.getYaw();
		float o = entity.getScale();
		Vector3f vector3f = new Vector3f(0.0F, entity.getHeight() / 2.0F + scale * o, 0.0F);
		float p = size / o;

        mod.apply(entity);

		InventoryScreen.drawEntity(context, f, g, p, vector3f, quaternionf, quaternionf2, entity);
		
        entity.bodyYaw = j;
		entity.setYaw(k);
		entity.setPitch(l);
		entity.prevHeadYaw = m;
		entity.headYaw = n;
		context.disableScissor();

        mod.apply(entity);
    }

    interface PlayerRenderModifier {

        public void apply(PlayerEntity player);
    }
}
