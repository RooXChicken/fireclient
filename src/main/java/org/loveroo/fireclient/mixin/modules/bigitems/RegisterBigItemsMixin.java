package org.loveroo.fireclient.mixin.modules.bigitems;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.render.entity.ItemEntityRenderer;

@Mixin(ItemEntityRenderer.class)
public abstract class RegisterBigItemsMixin {

    // @Shadow @Final
    // public ItemRenderState itemRenderState;

    // @Inject(method = "update", at = @At("TAIL"))
    // private void registerBigItems(Entity entity, ItemStack stack, ItemModelManager itemModelManager, CallbackInfo info) {
    //     if(!(itemRenderState instanceof BigItemsModule.ItemTypeStorage state)) {
    //         return;
    //     }

    //     state.fireclient$setItem(stack.getItem());
    // }
}
