package org.loveroo.fireclient.modules.indicators;

import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffects;
import org.loveroo.fireclient.data.Color;

public class InWallIndicator extends Indicator {

    public boolean isInWall = false;

    public InWallIndicator(int index) {
        super("indicator_in_wall", "\uD83E\uDDF1", Color.fromRGB(0x3D3D3D), true, index);
    }

    @Override
    protected boolean doesDraw(Minecraft client) {
        var inWall = isInWall;

        if(inWall) {
            isInWall = false;
        }

        return inWall;
    }
}
