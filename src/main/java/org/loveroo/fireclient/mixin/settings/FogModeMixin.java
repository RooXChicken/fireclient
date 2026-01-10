package org.loveroo.fireclient.mixin.settings;

import java.util.Map;

import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.entity.player.SkinTextures;
import org.joml.Vector4f;
import org.loveroo.fireclient.FireClient;
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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.TropicalFishEntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.TropicalFishEntityRenderState;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

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

@Mixin(EntityRenderManager.class)
abstract class FogPlayerModelMixin {

    @Shadow
    private Map<PlayerSkinType, EntityRenderer<? extends PlayerEntity, ?>> playerRenderers;

    @SuppressWarnings("unchecked")
    @Inject(method = "getRenderer(Lnet/minecraft/entity/Entity;)Lnet/minecraft/client/render/entity/EntityRenderer;", at = @At("HEAD"), cancellable = true)
    public <T extends Entity> void getRenderer(T entity, CallbackInfoReturnable<EntityRenderer<? super T, ?>> info) {
        if(FireClientside.getSetting(FireClientOption.FOG_MODE) == 0 || !(entity instanceof AbstractClientPlayerEntity)) {
            return;
        }

        var model = PlayerSkinType.SLIM;
        info.setReturnValue((EntityRenderer<? super T, ?>)playerRenderers.get(model));
    }
}

@Mixin(EntityRenderer.class)
abstract class FogNametagMixin<T extends Entity, S extends EntityRenderState> {

    @Unique
    private final Text fogNametag = Text.of("NeF0Geo");

    @ModifyArg(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitLabel(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Vec3d;ILnet/minecraft/text/Text;ZIDLnet/minecraft/client/render/state/CameraRenderState;)V"), index = 3)
    private Text changeText(Text original, @Local(ordinal = 0, argsOnly = true) S renderState) {
        if(FireClientside.getSetting(FireClientOption.FOG_MODE) == 0 || renderState.displayName != original) {
            return original;
        }

        return fogNametag;
    }
}

@Mixin(TropicalFishEntityRenderer.class)
abstract class FogFishMixin {

    @Inject(method = "updateRenderState*", at = @At("TAIL"))
    public void makeHazeliFish(TropicalFishEntity tropicalFishEntity, TropicalFishEntityRenderState tropicalFishEntityRenderState, float f, CallbackInfo info) {
        if(FireClientside.getSetting(FireClientOption.FOG_MODE) == 0) {
            return;
        }

        tropicalFishEntityRenderState.variety = TropicalFishEntity.Pattern.SNOOPER;
        tropicalFishEntityRenderState.baseColor = DyeColor.GRAY.getEntityColor();
        tropicalFishEntityRenderState.patternColor = DyeColor.RED.getEntityColor();
    }
}

@Mixin(FogRenderer.class)
abstract class FogAmplifierMixin {

    @ModifyVariable(method = "applyFog(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private Vector4f modifyFogColor(Vector4f original) {
        if(!fogModeEnabled()) {
            return original;
        }

        return new Vector4f(1.0f, 0.0f, 0.0f, 0.8f);
    }

    @ModifyVariable(method = "applyFog(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float modifyEnvironmentStart(float original) {
        if(!fogModeEnabled()) {
            return original;
        }

        return 8.0f;
    }

    @ModifyVariable(method = "applyFog(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private float modifyEnvironmentEnd(float original) {
        if(!fogModeEnabled()) {
            return original;
        }

        return 32.0f;
    }

    @ModifyVariable(method = "applyFog(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V", at = @At("HEAD"), ordinal = 2, argsOnly = true)
    private float modifyRenderStart(float original) {
        if(!fogModeEnabled()) {
            return original;
        }

        return 8.0f;
    }

    @ModifyVariable(method = "applyFog(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V", at = @At("HEAD"), ordinal = 3, argsOnly = true)
    private float modifyRenderEnd(float original) {
        if(!fogModeEnabled()) {
            return original;
        }

        return 32.0f;
    }

    @ModifyVariable(method = "applyFog(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V", at = @At("HEAD"), ordinal = 4, argsOnly = true)
    private float modifySkyEnd(float original) {
        if(!fogModeEnabled()) {
            return original;
        }

        return 128f;
    }

    private boolean fogModeEnabled() {
        return (FireClientside.getSetting(FireClientOption.FOG_MODE) == 1 && MinecraftClient.getInstance().world != null);
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

@Mixin(WorldRenderer.class)
abstract class FogSkyColorMixin {

    @ModifyVariable(method = "render", at = @At("HEAD"), ordinal = 0)
    private Vector4f modifyFogColor(Vector4f original) {
        if(FireClientside.getSetting(FireClientOption.FOG_MODE) == 0) {
            return original;
        }

        return new Vector4f(1.0f, 0.0f, 0.0f, 0.8f);
    }
}
