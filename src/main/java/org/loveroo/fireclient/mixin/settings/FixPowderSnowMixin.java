package org.loveroo.fireclient.mixin.settings;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PowderSnowBlock.class)
public abstract class FixPowderSnowMixin {

//    @Shadow @Final
//    private static VoxelShape FALLING_SHAPE;
//
//    @Inject(method = "getCollisionShape", at = @At("RETURN"), cancellable = true)
//    private void fixCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> info) {
//        if(info.getReturnValue() == FALLING_SHAPE) {
//            info.setReturnValue(state.getOutlineShape(world, pos));
//        }
//    }
}
