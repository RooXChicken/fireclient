package org.loveroo.fireclient.settings;

import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.ShadowModule;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

// i have to do this because i can't mixin to WorldView (it's an interface) :c
public interface FullbrightShadows extends WorldView {

    @Override
    default int getLightLevel(BlockPos pos) {
        if(!ShadowModule.drawingShadow) {
            return WorldView.super.getLightLevel(pos);
        }

        var shadow = (ShadowModule) FireClientside.getModule("shadow");
        if(shadow == null || !shadow.isFullbright()) {
            return WorldView.super.getLightLevel(pos);
        }

        return 15;
    }
}
