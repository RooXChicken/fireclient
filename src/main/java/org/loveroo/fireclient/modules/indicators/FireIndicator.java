package org.loveroo.fireclient.modules.indicators;

import net.minecraft.client.MinecraftClient;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;

public class FireIndicator extends Indicator {

    public FireIndicator(int index) {
        super("indicator_fire", "\uD83D\uDD25", "Fire", Color.fromRGB(0xFA7B39), true, index);
    }

    @Override
    protected boolean doesDraw(MinecraftClient client) {
        return (client.player.isOnFire());
    }
}
