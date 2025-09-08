package org.loveroo.fireclient.mixin.modules.nametag;

import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.NametagModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntityRenderer.class)
public class ShowOwnNametagMixin<T extends LivingEntity, S extends EntityRenderState, M extends EntityModel<? super S>> {

    @Inject(method = "hasLabel*", at = @At("HEAD"), cancellable = true)
    private void showOwnLabel(T entity, double squaredDistanceToCamera, CallbackInfoReturnable<Boolean> info) {
        var nametag = (NametagModule) FireClientside.getModule("nametag");
        if(nametag == null || !nametag.isShowOwn()) {
            return;
        }

        var client = MinecraftClient.getInstance();
        if(client.player == null || client.player.getUuid() != entity.getUuid()) {
            return;
        }

        info.setReturnValue(true);
    }
}
