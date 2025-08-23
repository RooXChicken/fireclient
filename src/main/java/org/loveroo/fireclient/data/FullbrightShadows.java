package org.loveroo.fireclient.data;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.ShadowModule;

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
