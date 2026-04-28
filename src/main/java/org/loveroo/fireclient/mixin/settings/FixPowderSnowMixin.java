package org.loveroo.fireclient.mixin.settings;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
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
