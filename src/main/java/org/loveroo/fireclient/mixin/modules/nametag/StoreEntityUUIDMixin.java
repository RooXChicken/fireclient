package org.loveroo.fireclient.mixin.modules.nametag;

import org.loveroo.fireclient.modules.NametagModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

@Mixin(EntityRenderDispatcher.class)
public class StoreEntityUUIDMixin {

    @Inject(method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V", at = @At("HEAD"))
    private <E extends Entity, S extends EntityRenderState> void setUUID(E entity, double x, double y, double z, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, EntityRenderer<? super E, S> renderer, CallbackInfo info) {
        if(renderer instanceof NametagModule.UUIDStorage store) {
            store.fireclient$setUUID(entity.getUuid());
        }

        if(renderer instanceof NametagModule.NameStorage store) {
            store.fireclient$setName(entity.getName().getString());
        }
    }
    
}
