package org.loveroo.fireclient.mixin.modules.shadows;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.ShadowModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class RenderShadowsOnAllMixin {

    @Inject(method = "isCollisionShapeFullBlock", at = @At("HEAD"), cancellable = true)
    private void setFullCube(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        var shadow = (ShadowModule) FireClientside.getModule("shadow");
        if(shadow == null || !ShadowModule.drawingShadow || !shadow.isRenderOnAll()) {
            return;
        }

        info.setReturnValue(true);
    }
}
