package org.loveroo.fireclient.mixin.modules.bigitems;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.render.entity.ItemEntityRenderer;

@Mixin(ItemEntityRenderer.class)
public class BigItemMixin {

    // @Inject(method = "renderStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderState;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V"))
    // private static void testScale(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStackEntityRenderState state, Random random, CallbackInfo ci) {
    //     var bigItems = (BigItemsModule) FireClientside.getModule("big_items");
    //     if(bigItems == null || !bigItems.getData().isEnabled()) {
    //         return;
    //     }

    //     var getItem = (BigItemsModule.ItemTypeStorage)state.itemRenderState;
    //     if(!bigItems.isBig(getItem.fireclient$getItem())) {
    //         return;
    //     }

    //     matrices.scale(2.0f, 2.0f, 2.0f);
    // }
}
