package org.loveroo.fireclient.mixin.modules.bigitems;

import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.BigItemsModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;

@Mixin(ItemEntityRenderer.class)
public class BigItemMixin {

    @Inject(method = "submitMultipleFromCount(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/ItemClusterRenderState;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/phys/AABB;)V", at = @At("HEAD"))
    private static void testScale(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, ItemClusterRenderState state, RandomSource random, AABB modelBoundingBox, CallbackInfo info) {
        var bigItems = (BigItemsModule) FireClientside.getModule("big_items");
        if(bigItems == null || !bigItems.getData().isEnabled()) {
            return;
        }

        var getItem = (BigItemsModule.ItemTypeStorage)state.item;
        if(!bigItems.isBig(getItem.fireclient$getItem())) {
            return;
        }

        poseStack.scale(2.0f, 2.0f, 2.0f);
    }
}
