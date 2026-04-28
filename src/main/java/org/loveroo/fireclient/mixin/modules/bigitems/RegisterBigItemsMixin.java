package org.loveroo.fireclient.mixin.modules.bigitems;

import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.loveroo.fireclient.modules.BigItemsModule;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemClusterRenderState.class)
public abstract class RegisterBigItemsMixin {

    @Shadow @Final
    public ItemStackRenderState item;

    @Inject(method = "extractItemGroupRenderState", at = @At("TAIL"))
    private void registerBigItems(Entity entity, ItemStack stack, ItemModelResolver itemModelResolver, CallbackInfo info) {
        if(!(item instanceof BigItemsModule.ItemTypeStorage state)) {
            return;
        }

        state.fireclient$setItem(stack.getItem());
    }
}
