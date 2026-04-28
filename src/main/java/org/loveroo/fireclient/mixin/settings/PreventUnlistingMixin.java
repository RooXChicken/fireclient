package org.loveroo.fireclient.mixin.settings;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(ClientPacketListener.class)
public class PreventUnlistingMixin {

    @ModifyVariable(method = "applyPlayerInfoUpdate", at = @At("HEAD"), argsOnly = true, name = "entry")
    private ClientboundPlayerInfoUpdatePacket.Entry preventRemoving(ClientboundPlayerInfoUpdatePacket.Entry entry) {
        if(FireClientside.getSetting(FireClientOption.PREVENT_HIDING_ENTRIES) == 0) {
            return entry;
        }

        return new ClientboundPlayerInfoUpdatePacket.Entry(entry.profileId(), entry.profile(), true, entry.latency(), entry.gameMode(), entry.displayName(), entry.showHat(), entry.listOrder(), entry.chatSession());
    }
}
