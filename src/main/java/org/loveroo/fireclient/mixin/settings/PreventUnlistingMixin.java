package org.loveroo.fireclient.mixin.settings;

import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;

@Mixin(ClientPlayNetworkHandler.class)
public class PreventUnlistingMixin {

    @ModifyVariable(method = "handlePlayerListAction", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private PlayerListS2CPacket.Entry preventRemoving(PlayerListS2CPacket.Entry entry) {
        if(FireClientside.getSetting(FireClientOption.PREVENT_HIDING_ENTRIES) == 0) {
            return entry;
        }

        return new PlayerListS2CPacket.Entry(entry.profileId(), entry.profile(), true, entry.latency(), entry.gameMode(), entry.displayName(), entry.chatSession());
    }
}
