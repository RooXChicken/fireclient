package org.loveroo.fireclient.mixin.modules.bigitems;

import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.BigItemsModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.state.ItemStackEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;

@Mixin(ItemEntityRenderer.class)
public class BigItemMixin {

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/ItemStackEntityRenderState;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/util/math/Box;)V", at = @At("HEAD"))
    private static void testScale(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, ItemStackEntityRenderState state, Random random, Box boundingBox, CallbackInfo info) {
        var bigItems = (BigItemsModule) FireClientside.getModule("big_items");
        if(bigItems == null || !bigItems.getData().isEnabled()) {
            return;
        }

        var getItem = (BigItemsModule.ItemTypeStorage)state.itemRenderState;
        if(!bigItems.isBig(getItem.fireclient$getItem())) {
            return;
        }

        matrices.scale(2.0f, 2.0f, 2.0f);
    }
}
