package org.loveroo.fireclient.mixin.modules.nametag;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntityRenderer.class)
public class ShowOwnNametagMixin<T extends LivingEntity, M extends EntityModel<?>> {

    // @Inject(method = "hasLabel*", at = @At("HEAD"), cancellable = true)
    // private void showOwnLabel(T entity, double squaredDistanceToCamera, CallbackInfoReturnable<Boolean> info) {
    //     var nametag = (NametagModule) FireClientside.getModule("nametag");
    //     if(nametag == null || !nametag.isShowOwn()) {
    //         return;
    //     }

    //     var client = MinecraftClient.getInstance();
    //     if(client.player == null || client.player.getUuid() != entity.getUuid()) {
    //         return;
    //     }

    //     info.setReturnValue(true);
    // }
}
