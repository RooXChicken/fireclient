package org.loveroo.fireclient.modules.indicators;

import net.minecraft.client.MinecraftClient;
import org.loveroo.fireclient.data.Color;

public class FrostIndicator extends Indicator {

    public FrostIndicator(int index) {
        super("indicator_frost", "❄", "Frost", Color.fromRGB(0xB6E8FA), true, index);
    }

    @Override
    protected boolean doesDraw(MinecraftClient client) {
        return (client.player.getFrozenTicks() > 0);
    }
}
