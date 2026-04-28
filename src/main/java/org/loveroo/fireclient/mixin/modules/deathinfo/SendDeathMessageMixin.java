package org.loveroo.fireclient.mixin.modules.deathinfo;

import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.modules.CoordinatesModule;
import org.loveroo.fireclient.modules.DeathInfoModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;

@Mixin(ClientPacketListener.class)
public class SendDeathMessageMixin {

    @Shadow
    private ClientLevel level;
    
    @Unique
    private long lastDeath = 0;

    @Unique
    private final Color deathColor1 = new Color(171, 12, 12, 255);

    @Unique
    private final Color deathColor2 = new Color(184, 48, 48, 255);

    @Inject(method = "handlePlayerCombatKill", at = @At("HEAD"))
    private void sendMessage(ClientboundPlayerCombatKillPacket packet, CallbackInfo ci) {
        var client = Minecraft.getInstance();
        if(client.player == null || packet.playerId() != client.player.getId()) {
            return;
        }

        var deathInfo = (DeathInfoModule) FireClientside.getModule("death_info");
        if(deathInfo == null || !deathInfo.getData().isEnabled()) {
            return;
        }

        var deathTime = client.player.level().getGameTime();
        var oldDeathTime = lastDeath;

        lastDeath = deathTime;

        if(deathTime - oldDeathTime < 1) {
            return;
        }

        var xPos = String.format("%.2f ", client.player.getX());
        var yPos = String.format("%.2f ", client.player.getY());
        var zPos = String.format("%.2f ", client.player.getZ());

        var xText = String.format("X: " + xPos);
        var yText = String.format("Y: " + yPos);
        var zText = String.format("Z: " + zPos);

        var x = RooHelper.gradientText(xText, CoordinatesModule.xColor1, CoordinatesModule.xColor2);
        var y = RooHelper.gradientText(yText, CoordinatesModule.yColor1, CoordinatesModule.yColor2);
        var z = RooHelper.gradientText(zText, CoordinatesModule.zColor1, CoordinatesModule.zColor2);

        var positionText = x.append(y).append(z);

        var command = "/execute in " + level.dimensionTypeRegistration().getRegisteredName() + " run tp " + xPos + yPos + zPos;
        var click = new ClickEvent.SuggestCommand(command);
        var hover = new HoverEvent.ShowText(Component.nullToEmpty(command));

        var posClickable = positionText.copy().setStyle(Style.EMPTY.withClickEvent(click).withHoverEvent(hover));

        var deathText = RooHelper.gradientText("You died at: ", deathColor1, deathColor2).append(posClickable);
        client.player.sendSystemMessage(deathText);
    }
}
