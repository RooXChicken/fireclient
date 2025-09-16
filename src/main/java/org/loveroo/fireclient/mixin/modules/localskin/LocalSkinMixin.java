package org.loveroo.fireclient.mixin.modules.localskin;

import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.LocalSkinModule;
import org.loveroo.fireclient.modules.LocalSkinModule.TextureType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

@Mixin(SkinTextures.class)
abstract class LocalSkinMixin {

    // for a stackoverflow crash fix with essential
    @Unique
    private boolean checkingSkin = false;

    @Unique
    private boolean checkingCape = false;

    @Inject(method = "texture", at = @At("HEAD"), cancellable = true)
    public void getTexture(CallbackInfoReturnable<Identifier> info) {
        if(checkingSkin) {
            return;
        }

        checkingSkin = true;
        var skin = verifySelf(TextureType.SKIN);
        checkingSkin = false;

        if(skin == null) {
            return;
        }

        info.setReturnValue(skin);
    }

    @Inject(method = "capeTexture", at = @At("HEAD"), cancellable = true)
    public void getCape(CallbackInfoReturnable<Identifier> info) {
        if(checkingCape) {
            return;
        }

        checkingCape = true;
        var cape = verifySelf(TextureType.CAPE);
        checkingCape = false;

        if(cape == null) {
            return;
        }

        info.setReturnValue(cape);
    }

    @Nullable
    private Identifier verifySelf(TextureType type) {
        var localSkin = (LocalSkinModule) FireClientside.getModule("local_skin");
        if(localSkin == null || !localSkin.getData().isEnabled()) {
            return null;
        }

        var client = MinecraftClient.getInstance();
        if(client.player == null || client.player.getSkinTextures() != (Object)this) {
            return null;
        }

        return localSkin.getTexture(type);
    }
}

@Mixin(EntityRenderDispatcher.class)
abstract class LocalSkinModelMixin {

    @Shadow
    private Map<SkinTextures.Model, EntityRenderer<? extends PlayerEntity>> modelRenderers;

    @SuppressWarnings("unchecked")
    @Inject(method = "getRenderer(Lnet/minecraft/entity/Entity;)Lnet/minecraft/client/render/entity/EntityRenderer;", at = @At("HEAD"), cancellable = true)
    public <T extends Entity> void getRenderer(T entity, CallbackInfoReturnable<EntityRenderer<? super T>> info) {
        var client = MinecraftClient.getInstance();
        if(client.player != entity) {
            return;
        }
        
        var localSkin = (LocalSkinModule) FireClientside.getModule("local_skin");
        if(localSkin == null || !localSkin.getData().isEnabled()) {
            return;
        }

        var modelType = localSkin.getModel();
        if(modelType == null) {
            return;
        }

        var model = SkinTextures.Model.valueOf(modelType);
        info.setReturnValue((EntityRenderer<? super T>)modelRenderers.get(model));
    }
}