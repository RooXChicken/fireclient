package org.loveroo.fireclient.settings;

import org.jspecify.annotations.NonNull;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.ShadowModule;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

// i have to do this because i can't mixin to WorldView (it's an interface) :c
public interface FullbrightShadows extends LevelReader {

    @Override
    default int getMaxLocalRawBrightness(@NonNull BlockPos pos) {
        if(!ShadowModule.drawingShadow) {
            return LevelReader.super.getMaxLocalRawBrightness(pos);
        }

        var shadow = (ShadowModule) FireClientside.getModule("shadow");
        if(shadow == null || !shadow.isFullbright()) {
            return LevelReader.super.getMaxLocalRawBrightness(pos);
        }

        return 15;
    }
}
