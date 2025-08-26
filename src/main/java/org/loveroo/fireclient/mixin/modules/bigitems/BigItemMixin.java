package org.loveroo.fireclient.mixin.modules.bigitems;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.state.ItemStackEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.BigItemsModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
public class BigItemMixin {

    @Inject(method = "renderStack", at = @At("HEAD"))
    private static void testScale(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStackEntityRenderState state, Random random, Box box, CallbackInfo ci) {
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
