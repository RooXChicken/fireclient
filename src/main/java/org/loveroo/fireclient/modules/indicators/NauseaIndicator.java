package org.loveroo.fireclient.modules.indicators;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffects;
import org.loveroo.fireclient.data.Color;

public class NauseaIndicator extends Indicator {

    public NauseaIndicator(int index) {
        super("indicator_nausea", "\uD83C\uDF64", "Nausea", Color.fromRGB(0xBCEB65), true, index);
    }

    @Override
    protected boolean doesDraw(MinecraftClient client) {
        return (client.player.hasStatusEffect(StatusEffects.NAUSEA));
    }
}
