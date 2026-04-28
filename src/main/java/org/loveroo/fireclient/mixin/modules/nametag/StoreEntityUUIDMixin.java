package org.loveroo.fireclient.mixin.modules.nametag;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.loveroo.fireclient.modules.NametagModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public abstract class StoreEntityUUIDMixin {

    @Shadow
    public abstract <T extends Entity> EntityRenderer<? super T, ?> getRenderer(T entity);

    @Inject(method = "extractEntity", at = @At("HEAD"))
    private <E extends Entity, S extends EntityRenderState> void setUUID(E entity, float partialTicks, CallbackInfoReturnable<EntityRenderState> info) {
        EntityRenderer<? super E, ?> renderer = getRenderer(entity);

        if(renderer instanceof NametagModule.UUIDStorage store) {
            store.fireclient$setUUID(entity.getUUID());
        }

        if(renderer instanceof NametagModule.NameStorage store) {
            store.fireclient$setName(entity.getName().getString());
        }
    }
    
}
