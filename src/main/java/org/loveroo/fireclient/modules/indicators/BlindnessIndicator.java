package org.loveroo.fireclient.modules.indicators;

import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffects;
import org.loveroo.fireclient.data.Color;

public class BlindnessIndicator extends Indicator {

    public BlindnessIndicator(int index) {
        super("indicator_blindness", "\uD83D\uDC41", Color.fromRGB(0x211C21), false, index);
    }

    @Override
    protected boolean doesDraw(Minecraft client) {
        return (client.player != null && client.player.hasEffect(MobEffects.BLINDNESS));
    }
}
