// TODO(Ravel): Failed to fully resolve file: null cannot be cast to non-null type com.intellij.psi.PsiJavaCodeReferenceElement
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

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.core.ClientAsset;

@Mixin(PlayerSkin.class)
abstract class LocalSkinMixin {

    // for a stackoverflow crash fix with essential
    @Unique
    private boolean checkingSkin = false;

    @Unique
    private boolean checkingCape = false;

    @Unique
    private boolean verifying = false;

    @Inject(method = "body", at = @At("HEAD"), cancellable = true)
    public void getTexture(CallbackInfoReturnable<ClientAsset.Texture> info) {
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
    public void getCape(CallbackInfoReturnable<ClientAsset.Texture> info) {
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
    private ClientAsset.Texture verifySelf(TextureType type) {
        if(verifying) {
            return null;
        }

        verifying = true;

        var localSkin = (LocalSkinModule) FireClientside.getModule("local_skin");
        if(localSkin == null || !localSkin.getData().isEnabled()) {
            verifying = false;
            return null;
        }

        var client = Minecraft.getInstance();
        if(client.player == null || client.player.getSkin() != (Object) this) {
            verifying = false;
            return null;
        }

        verifying = false;

        var asset = localSkin.getAsset(type);
        return asset.orElse(null);

    }
}

@Mixin(EntityRenderDispatcher.class)
abstract class LocalSkinModelMixin {

    @Inject(method = "getAvatarRenderer(Ljava/util/Map;Lnet/minecraft/world/entity/Avatar;)Lnet/minecraft/client/renderer/entity/player/AvatarRenderer;", at = @At("HEAD"), cancellable = true)
    public <T extends Avatar & ClientAvatarEntity> void fireclient$modifyPlayerModel(Map<PlayerModelType, AvatarRenderer<T>> skinTypeToRenderer, T player, CallbackInfoReturnable<AvatarRenderer<T>> info) {
        var client = Minecraft.getInstance();
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

        var model = PlayerModelType.valueOf(modelType);
        info.setReturnValue(skinTypeToRenderer.get(model));
    }
}