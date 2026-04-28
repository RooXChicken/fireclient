package org.loveroo.fireclient.modules.indicators;

import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffects;
import org.loveroo.fireclient.data.Color;

public class PortalIndicator extends Indicator {

    public PortalIndicator(int index) {
        super("indicator_portal", "\uD83C\uDF00", Color.fromRGB(0x780080), true, index);
    }

    @Override
    protected boolean doesDraw(Minecraft client) {
        if(client.player == null || client.player.portalProcess == null) {
            return false;
        }

        return (client.player.portalProcess.isInsidePortalThisTick());
    }
}
