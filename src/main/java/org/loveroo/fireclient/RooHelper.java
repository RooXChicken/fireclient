package org.loveroo.fireclient;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.json.JSONObject;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.FireClientOption;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import org.loveroo.fireclient.mixin.DrawEntityAccessor;

public class RooHelper {

    private static final String colorCodeCharacter = "§";

    public static MutableComponent gradientText(String msg, Color color1, Color color2) {
//        if(FireClientside.getSetting(FireClientOption.DISABLE_GRADIENT) == 1) {
//            return Text.literal(msg).withColor(color1.toInt());
//        }

        var text = MutableComponent.create(new PlainTextContents.LiteralContents(""));

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

            text.append(MutableComponent.create(new PlainTextContents.LiteralContents(letter)).setStyle(style));

            i += Character.charCount(codePont);
        }

        return text;
    }

    @Nullable
    public static ClientPacketListener getNetworkHandler() {
        return Minecraft.getInstance().getConnection();
    }

    public static void sendChatCommand(String msg) {
        var handler = getNetworkHandler();
        if(handler == null) {
            return;
        }

        var command = (msg.startsWith("/")) ? msg.substring(1) : msg;
        handler.sendCommand(command);
    }

    public static void sendChatMessage(String msg) {
        var handler = getNetworkHandler();
        if(handler == null) {
            return;
        }

        handler.sendChat(msg);
    }

    public static void sendNotification(Component name, Component description) {
        var client = Minecraft.getInstance();
        client.getToastManager().addToast(new SystemToast(SystemToast.SystemToastId.PACK_LOAD_FAILURE, name, description));
    }

    public static JSONObject jsonFromStringSafe(String message) {
        try {
            return new JSONObject(message);
        }
        catch(Exception e) {
            return new JSONObject();
        }
    }

    public static Overlay getOverlay(Minecraft client) {
        if(FireClientside.getSetting(FireClientOption.NO_RELOAD_OVERLAY) != 0 && client.getOverlay() instanceof LoadingOverlay) {
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
        var client = Minecraft.getInstance();
        if(client.getCurrentServer() != null) {
            return client.getCurrentServer().ip;
        }
        else {
            return "__local";
        }
    }

    public static String getServerBrand() {
        var client = Minecraft.getInstance();
        var handler = getNetworkHandler();
        if(handler == null || client.getSingleplayerServer() != null) {
            return "Integrated Server";
        }
        else {
            return handler.serverBrand();
        }
    }

    /**
     * Modified from {@link net.minecraft.client.gui.screens.inventory.InventoryScreen#extractEntityInInventoryFollowsMouse(GuiGraphicsExtractor, int, int, int, int, int, float, float, float, LivingEntity)}
     */
    public static void drawPlayer(GuiGraphicsExtractor context, int x, int y, float scale, float mouseX, float mouseY) {
        drawPlayer(context, x, y, scale, mouseX, mouseY, 0, 0, (player) -> {});
	}

    public static void drawPlayer(GuiGraphicsExtractor context, int x, int y, float scale, float mouseX, float mouseY, float yawOffset, float pitchOffset) {
        drawPlayer(context, x, y, scale, mouseX, mouseY, yawOffset, pitchOffset, (player) -> {});
	}

    public static void drawPlayer(GuiGraphicsExtractor context, int x, int y, float scale, float mouseX, float mouseY, PlayerRenderModifier mod) {
        drawPlayer(context, x, y, scale, mouseX, mouseY, 0, 0, mod);
	}

    public static void drawPlayer(
            GuiGraphicsExtractor context,
            int x,
            int y,
            float scale,
            float mouseX,
            float mouseY,
            float yawOffset,
            float pitchOffset,
            PlayerRenderModifier mod) {
            
        var client = Minecraft.getInstance();
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
		float j = entity.yBodyRot;
		float k = entity.getYRot();
		float l = entity.getXRot();
		float m = entity.yHeadRotO;
		float n = entity.yHeadRot;
		entity.yBodyRot = 180.0F + h * 20.0F + yawOffset;
		entity.setYRot(180.0F + h * 40.0F + yawOffset);
		entity.setXRot(-i * 20.0F + pitchOffset);
		entity.yHeadRot = entity.getYRot();
		entity.yHeadRotO = entity.getYRot();
		float o = entity.getScale();
		Vector3f vector3f = new Vector3f(0.0F, entity.getBbHeight() / 2.0F + scale * o, 0.0F);
		float p = size / o;

        mod.apply(entity);

//        context.addEntity(en, size, vector3f, quaternionf, quaternionf2, x1, y1, x2, y2);
        var state = DrawEntityAccessor.getEntityRenderState(entity);
        context.entity(state, scale, vector3f, quaternionf, quaternionf2, x1, y1, x2, y2);
//		InventoryScreen.drawEntity(context, x1, y1, x2, y2, size, o, quaternionf, quaternionf2, entity);
		
        entity.yBodyRot = j;
		entity.setYRot(k);
		entity.setXRot(l);
		entity.yHeadRotO = m;
		entity.yHeadRot = n;
		context.disableScissor();

        mod.apply(entity);
    }

    interface PlayerRenderModifier {

        public void apply(Player player);
    }
}
