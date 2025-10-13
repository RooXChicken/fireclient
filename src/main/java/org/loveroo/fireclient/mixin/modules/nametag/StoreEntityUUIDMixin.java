package org.loveroo.fireclient.mixin.modules.nametag;

import net.minecraft.client.render.entity.EntityRenderManager;
import org.loveroo.fireclient.modules.NametagModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderManager.class)
public abstract class StoreEntityUUIDMixin {

    @Shadow
    public abstract <T extends Entity> EntityRenderer<? super T, ?> getRenderer(T entity);

    @Inject(method = "getAndUpdateRenderState", at = @At("HEAD"))
    private <E extends Entity, S extends EntityRenderState> void setUUID(E entity, float tickProgress, CallbackInfoReturnable<EntityRenderState> info) {
        EntityRenderer<? super E, ?> renderer = getRenderer(entity);

        if(renderer instanceof NametagModule.UUIDStorage store) {
            store.fireclient$setUUID(entity.getUuid());
        }

        if(renderer instanceof NametagModule.NameStorage store) {
            store.fireclient$setName(entity.getName().getString());
        }
    }
    
}
