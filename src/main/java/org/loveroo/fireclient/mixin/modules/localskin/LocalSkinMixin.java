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

    @Unique
    private boolean verifying = false;

    @Inject(method = "texture", at = @At("HEAD"), cancellable = true)
    public void getTexture(CallbackInfoReturnable<Identifier> info) {
        if(checkingSkin) {
            return;
        }

        checkingSkin = true;
        var skin = verifySelf(TextureType.SKIN);
        
        if(skin == null) {
            checkingSkin = false;
            return;
        }

        info.setReturnValue(skin);
        checkingSkin = false;
    }

    @Inject(method = "capeTexture", at = @At("HEAD"), cancellable = true)
    public void getCape(CallbackInfoReturnable<Identifier> info) {
        if(checkingCape) {
            return;
        }

        checkingCape = true;
        var cape = verifySelf(TextureType.CAPE);
        
        if(cape == null) {
            checkingCape = false;
            return;
        }

        info.setReturnValue(cape);
        checkingCape = false;
    }

    @Nullable
    private Identifier verifySelf(TextureType type) {
        if(verifying) {
            return null;
        }

        verifying = true;

        var localSkin = (LocalSkinModule) FireClientside.getModule("local_skin");
        if(localSkin == null || !localSkin.getData().isEnabled()) {
            verifying = false;
            return null;
        }

        var client = MinecraftClient.getInstance();
        if(client.player == null || client.player.getSkinTextures() != (Object)this) {
            verifying = false;
            return null;
        }

        verifying = false;
        return localSkin.getTexture(type);
    }
}

@Mixin(EntityRenderDispatcher.class)
abstract class LocalSkinModelMixin {

    @Shadow
    private Map<SkinTextures.Model, EntityRenderer<? extends PlayerEntity, ?>> modelRenderers;

    @SuppressWarnings("unchecked")
    @Inject(method = "getRenderer(Lnet/minecraft/entity/Entity;)Lnet/minecraft/client/render/entity/EntityRenderer;", at = @At("HEAD"), cancellable = true)
    public <T extends Entity> void getRenderer(T entity, CallbackInfoReturnable<EntityRenderer<? super T, ?>> info) {
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
        info.setReturnValue((EntityRenderer<? super T, ?>)modelRenderers.get(model));
    }
}