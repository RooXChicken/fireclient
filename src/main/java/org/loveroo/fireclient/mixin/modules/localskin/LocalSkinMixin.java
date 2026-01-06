package org.loveroo.fireclient.mixin.modules.localskin;

import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.LocalSkinModule;
import org.loveroo.fireclient.modules.LocalSkinModule.TextureType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerLikeEntity;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.AssetInfo;

@Mixin(SkinTextures.class)
abstract class LocalSkinMixin {

    // for a stackoverflow crash fix with essential
    @Unique
    private boolean checkingSkin = false;

    @Unique
    private boolean checkingCape = false;

    @Unique
    private boolean verifying = false;

    @Inject(method = "body", at = @At("HEAD"), cancellable = true)
    public void getTexture(CallbackInfoReturnable<AssetInfo.TextureAsset> info) {
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

    @Inject(method = "cape", at = @At("HEAD"), cancellable = true)
    public void getCape(CallbackInfoReturnable<AssetInfo.TextureAsset> info) {
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
    private AssetInfo.TextureAsset verifySelf(TextureType type) {
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
        if(client.player == null || client.player.getSkin() != (Object) this) {
            verifying = false;
            return null;
        }

        verifying = false;

        var asset = localSkin.getAsset(type);
        return asset.orElse(null);

    }
}

@Mixin(EntityRenderManager.class)
abstract class LocalSkinModelMixin {

    @Inject(method = "getPlayerRenderer(Ljava/util/Map;Lnet/minecraft/entity/PlayerLikeEntity;)Lnet/minecraft/client/render/entity/PlayerEntityRenderer;", at = @At("HEAD"), cancellable = true)
    public <T extends PlayerLikeEntity & ClientPlayerLikeEntity> void fireclient$modifyPlayerModel(Map<PlayerSkinType, PlayerEntityRenderer<T>> skinTypeToRenderer, T player, CallbackInfoReturnable<PlayerEntityRenderer<T>> info) {
        var client = MinecraftClient.getInstance();
        if(client.player != player) {
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

        var model = PlayerSkinType.valueOf(modelType);
        info.setReturnValue(skinTypeToRenderer.get(model));
    }
}