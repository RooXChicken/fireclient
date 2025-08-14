package org.loveroo.fireclient.modules.indicators;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffects;
import org.loveroo.fireclient.data.Color;

public class PortalIndicator extends Indicator {

    public PortalIndicator(int index) {
        super("indicator_portal", "\uD83C\uDF00", "Portal", Color.fromRGB(0x780080), true, index);
    }

    @Override
    protected boolean doesDraw(MinecraftClient client) {
        if(client.player.portalManager == null) {
            return false;
        }

        return (client.player.portalManager.isInPortal());
    }
}
