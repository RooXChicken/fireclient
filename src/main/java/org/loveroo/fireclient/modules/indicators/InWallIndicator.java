package org.loveroo.fireclient.modules.indicators;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffects;
import org.loveroo.fireclient.data.Color;

public class InWallIndicator extends Indicator {

    public boolean isInWall = false;

    public InWallIndicator(int index) {
        super("indicator_in_wall", "\uD83E\uDDF1", "In Wall", Color.fromRGB(0x3D3D3D), true, index);
    }

    @Override
    protected boolean doesDraw(MinecraftClient client) {
        var inWall = isInWall;

        if(inWall) {
            isInWall = false;
        }

        return inWall;
    }
}
