package org.loveroo.fireclient.mixin.settings;

import java.util.Map;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.player.PlayerModelType;
import org.joml.Vector4f;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.TropicalFishRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.TropicalFishRenderState;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.fish.TropicalFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;

//@Mixin(SkinTextures.class)
//abstract class FogModeMixin {
//
//    @Unique
//    private final Identifier fogSkin = Identifier.of(FireClient.MOD_ID, "textures/skin/fog.png");
//
//    @Inject(method = "body", at = @At("HEAD"), cancellable = true)
//    public void getTexture(CallbackInfoReturnable<Identifier> info) {
//        if(FireClientside.getSetting(FireClientOption.FOG_MODE) == 0) {
//            return;
//        }
//
//        info.setReturnValue(fogSkin);
//    }
//}

@Mixin(EntityRenderDispatcher.class)
abstract class FogPlayerModelMixin {

    @Shadow
    private Map<PlayerModelType, EntityRenderer<? extends Player, ?>> playerRenderers;

    @SuppressWarnings("unchecked")
    @Inject(method = "getRenderer(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/client/renderer/entity/EntityRenderer;", at = @At("HEAD"), cancellable = true)
    public <T extends Entity> void getRenderer(T entity, CallbackInfoReturnable<EntityRenderer<? super T, ?>> info) {
        if(FireClientside.getSetting(FireClientOption.FOG_MODE) == 0 || !(entity instanceof AbstractClientPlayer)) {
            return;
        }

        var model = PlayerModelType.SLIM;
        info.setReturnValue((EntityRenderer<? super T, ?>)playerRenderers.get(model));
    }
}

@Mixin(EntityRenderer.class)
abstract class FogNametagMixin<T extends Entity, S extends EntityRenderState> {

    @Unique
    private final Component fogNametag = Component.nullToEmpty("NeF0Geo");

    @ModifyArg(method = "submitNameDisplay(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitNameTag(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/phys/Vec3;ILnet/minecraft/network/chat/Component;ZIDLnet/minecraft/client/renderer/state/level/CameraRenderState;)V"), index = 3)
    private Component changeText(Component original, @Local(argsOnly = true, name = "state") S state) {
        if(FireClientside.getSetting(FireClientOption.FOG_MODE) == 0 || state.nameTag != original) {
            return original;
        }

        return fogNametag;
    }
}

@Mixin(TropicalFishRenderer.class)
abstract class FogFishMixin {

    // TODO(Ravel): wildcard and regex target are not supported
// TODO(Ravel): wildcard and regex target are not supported
    @Inject(method = "extractRenderState*", at = @At("TAIL"))
    public void makeHazeliFish(TropicalFish entity, TropicalFishRenderState state, float partialTicks, CallbackInfo info) {
        if(FireClientside.getSetting(FireClientOption.FOG_MODE) == 0) {
            return;
        }

        state.pattern = TropicalFish.Pattern.SNOOPER;
        state.baseColor = DyeColor.GRAY.getTextureDiffuseColor();
        state.patternColor = DyeColor.RED.getTextureDiffuseColor();
    }
}

@Mixin(FogRenderer.class)
abstract class FogAmplifierMixin {

    @ModifyVariable(method = "updateBuffer(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V", at = @At("HEAD"), argsOnly = true, name = "fogColor")
    private Vector4f modifyFogColor(Vector4f fogColor) {
        if(!fogModeEnabled()) {
            return fogColor;
        }

        return new Vector4f(1.0f, 0.0f, 0.0f, 0.8f);
    }

    @ModifyVariable(method = "updateBuffer(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V", at = @At("HEAD"), argsOnly = true, name = "environmentalStart")
    private float modifyEnvironmentStart(float environmentalStart) {
        if(!fogModeEnabled()) {
            return environmentalStart;
        }

        return 8.0f;
    }

    @ModifyVariable(method = "updateBuffer(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V", at = @At("HEAD"), argsOnly = true, name = "environmentalEnd")
    private float modifyEnvironmentEnd(float environmentalEnd) {
        if(!fogModeEnabled()) {
            return environmentalEnd;
        }

        return 32.0f;
    }

    @ModifyVariable(method = "updateBuffer(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V", at = @At("HEAD"), argsOnly = true, name = "renderDistanceStart")
    private float modifyRenderStart(float renderDistanceStart) {
        if(!fogModeEnabled()) {
            return renderDistanceStart;
        }

        return 8.0f;
    }

    @ModifyVariable(method = "updateBuffer(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V", at = @At("HEAD"), argsOnly = true, name = "renderDistanceEnd")
    private float modifyRenderEnd(float renderDistanceEnd) {
        if(!fogModeEnabled()) {
            return renderDistanceEnd;
        }

        return 32.0f;
    }

    @ModifyVariable(method = "updateBuffer(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V", at = @At("HEAD"), argsOnly = true, name = "skyEnd")
    private float modifySkyEnd(float skyEnd) {
        if(!fogModeEnabled()) {
            return skyEnd;
        }

        return 128f;
    }

    @Unique
    private boolean fogModeEnabled() {
        return (FireClientside.getSetting(FireClientOption.FOG_MODE) == 1 && Minecraft.getInstance().level != null);
    }
}

//@Mixin(ClientWorld.class)
//abstract class FogCloudColorMixin {
//
//    @Inject(method = "", at = @At("HEAD"), cancellable = true)
//    private void modifySkyColor(Vec3d cameraPos, float tickProgress, CallbackInfoReturnable<Integer> info) {
//        if(FireClientside.getSetting(FireClientOption.FOG_MODE) == 0) {
//            return;
//        }
//
//        info.setReturnValue(0xFFFF0000);
//    }
//}

@Mixin(LevelRenderer.class)
abstract class FogSkyColorMixin {

    @ModifyVariable(method = "renderLevel", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private Vector4f modifyFogColor(Vector4f fogColor) {
        if(FireClientside.getSetting(FireClientOption.FOG_MODE) == 0) {
            return fogColor;
        }

        return new Vector4f(1.0f, 0.0f, 0.0f, 0.8f);
    }
}
