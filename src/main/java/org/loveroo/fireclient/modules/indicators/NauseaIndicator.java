package org.loveroo.fireclient.modules.indicators;

import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffects;
import org.loveroo.fireclient.data.Color;

public class NauseaIndicator extends Indicator {

    public NauseaIndicator(int index) {
        super("indicator_nausea", "\uD83C\uDF64", Color.fromRGB(0xBCEB65), true, index);
    }

    @Override
    protected boolean doesDraw(Minecraft client) {
        return (client.player != null && client.player.hasEffect(MobEffects.NAUSEA));
    }
}
