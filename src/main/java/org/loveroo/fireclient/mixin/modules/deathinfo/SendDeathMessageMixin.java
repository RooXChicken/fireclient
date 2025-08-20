package org.loveroo.fireclient.mixin.modules.deathinfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.modules.CoordinatesModule;
import org.loveroo.fireclient.modules.DeathInfoModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class SendDeathMessageMixin {

    @Unique
    private long lastDeath = 0;

    @Unique
    private final Color deathColor1 = new Color(171, 12, 12, 255);

    @Unique
    private final Color deathColor2 = new Color(184, 48, 48, 255);

    @Inject(method = "onDeathMessage", at = @At("HEAD"))
    private void sendMessage(DeathMessageS2CPacket packet, CallbackInfo ci) {
        var client = MinecraftClient.getInstance();
        if(client.player == null || packet.playerId() != client.player.getId()) {
            return;
        }

        var deathInfo = (DeathInfoModule) FireClientside.getModule("death_info");
        if(deathInfo == null || !deathInfo.getData().isEnabled()) {
            return;
        }

        var deathTime = client.player.clientWorld.getTime();
        var oldDeathTime = lastDeath;

        lastDeath = deathTime;

        if(deathTime - oldDeathTime < 1) {
            return;
        }

        var xPos = String.format("%.2f ", client.player.getPos().getX());
        var yPos = String.format("%.2f ", client.player.getPos().getY());
        var zPos = String.format("%.2f ", client.player.getPos().getZ());

        var xText = String.format("X: " + xPos);
        var yText = String.format("Y: " + yPos);
        var zText = String.format("Z: " + zPos);

        var x = RooHelper.gradientText(xText, CoordinatesModule.xColor1, CoordinatesModule.xColor2);
        var y = RooHelper.gradientText(yText, CoordinatesModule.yColor1, CoordinatesModule.yColor2);
        var z = RooHelper.gradientText(zText, CoordinatesModule.zColor1, CoordinatesModule.zColor2);

        var positionText = x.append(y).append(z);

        var command = "/tp " + xPos + yPos + zPos;
        var click = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command);
        var hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of(command));

        var posClickable = positionText.copy().setStyle(Style.EMPTY.withClickEvent(click).withHoverEvent(hover));

        var deathText = RooHelper.gradientText("You died at: ", deathColor1, deathColor2).append(posClickable);
        client.player.sendMessage(deathText, false);
    }
}
