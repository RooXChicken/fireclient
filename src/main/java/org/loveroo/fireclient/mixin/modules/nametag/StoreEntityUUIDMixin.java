package org.loveroo.fireclient.mixin.modules.nametag;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.render.entity.EntityRenderDispatcher;

@Mixin(EntityRenderDispatcher.class)
public class StoreEntityUUIDMixin {

    // @Inject(method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V", at = @At("HEAD"))
    // private <E extends Entity> void setUUID(E entity, double x, double y, double z, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, EntityRenderer<? super E> renderer, CallbackInfo info) {
    //     if(renderer instanceof NametagModule.UUIDStorage store) {
    //         store.fireclient$setUUID(entity.getUuid());
    //     }

    //     if(renderer instanceof NametagModule.NameStorage store) {
    //         store.fireclient$setName(entity.getName().getString());
    //     }
    // }
    
}
