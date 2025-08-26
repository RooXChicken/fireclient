package org.loveroo.fireclient.mixin.modules.shadows;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.ShadowModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class RenderShadowsOnAllMixin {

    @Inject(method = "isFullCube", at = @At("HEAD"), cancellable = true)
    private void setFullCube(BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        var shadow = (ShadowModule) FireClientside.getModule("shadow");
        if(shadow == null || !ShadowModule.drawingShadow || !shadow.isRenderOnAll()) {
            return;
        }

        info.setReturnValue(true);
    }
}
