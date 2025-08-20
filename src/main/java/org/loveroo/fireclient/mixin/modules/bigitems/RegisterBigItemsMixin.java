package org.loveroo.fireclient.mixin.modules.bigitems;

import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.state.ItemStackEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.loveroo.fireclient.modules.BigItemsModule;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStackEntityRenderState.class)
public abstract class RegisterBigItemsMixin {

    @Shadow @Final public ItemRenderState itemRenderState;

    @Inject(method = "update", at = @At("TAIL"))
    private void registerBigItems(Entity entity, ItemStack stack, ItemModelManager itemModelManager, CallbackInfo info) {
        if(!(itemRenderState instanceof BigItemsModule.ItemTypeStorage state)) {
            return;
        }

        state.fireclient$setItem(stack.getItem());
    }
}
