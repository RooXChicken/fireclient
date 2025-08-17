package org.loveroo.fireclient.mixin.settings;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(PlayerEntityRenderer.class)
abstract class HazeliModeMixin {

    @Unique
    private final Identifier hazeli = Identifier.of(FireClient.MOD_ID, "textures/skin/hazeli.png");

    @Inject(method = "getTexture(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;)Lnet/minecraft/util/Identifier;", at = @At("HEAD"), cancellable = true)
    public void getTexture(PlayerEntityRenderState playerEntityRenderState, CallbackInfoReturnable<Identifier> info) {
        if(FireClientside.getSetting(FireClientOption.HAZELI_MODE) == 0) {
            return;
        }

        info.setReturnValue(hazeli);
    }
}

@Mixin(EntityRenderDispatcher.class)
abstract class HazeliPlayerModelMixin {

    @Shadow private Map<SkinTextures.Model, EntityRenderer<? extends PlayerEntity, ?>> modelRenderers;

    @Inject(method = "getRenderer", at = @At("HEAD"), cancellable = true)
    public <T extends Entity> void getRenderer(T entity, CallbackInfoReturnable<EntityRenderer<? super T, ?>> info) {
        if(FireClientside.getSetting(FireClientOption.HAZELI_MODE) == 0 || !(entity instanceof AbstractClientPlayerEntity)) {
            return;
        }

        info.setReturnValue((EntityRenderer)modelRenderers.get(SkinTextures.Model.SLIM));
    }
}