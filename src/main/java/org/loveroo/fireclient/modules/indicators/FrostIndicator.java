package org.loveroo.fireclient.modules.indicators;

import net.minecraft.client.Minecraft;
import org.loveroo.fireclient.data.Color;

public class FrostIndicator extends Indicator {

    public FrostIndicator(int index) {
        super("indicator_frost", "❄", Color.fromRGB(0xB6E8FA), true, index);
    }

    @Override
    protected boolean doesDraw(Minecraft client) {
        return (client.player != null && client.player.getTicksFrozen() > 0);
    }
}
