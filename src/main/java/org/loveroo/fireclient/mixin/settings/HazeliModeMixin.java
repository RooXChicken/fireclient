package org.loveroo.fireclient.mixin.settings;

import java.util.Map;

import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.entity.player.SkinTextures;
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

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.TropicalFishEntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.TropicalFishEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

@Mixin(SkinTextures.class)
abstract class HazeliModeMixin {

    @Unique
    private final Identifier hazeli = Identifier.of(FireClient.MOD_ID, "textures/skin/hazeli.png");

    @Inject(method = "body", at = @At("HEAD"), cancellable = true)
    public void getTexture(CallbackInfoReturnable<Identifier> info) {
        if(FireClientside.getSetting(FireClientOption.HAZELI_MODE) == 0) {
            return;
        }

        info.setReturnValue(hazeli);
    }
}

@Mixin(EntityRenderManager.class)
abstract class HazeliPlayerModelMixin {

    @Shadow
    private Map<PlayerSkinType, EntityRenderer<? extends PlayerEntity, ?>> playerRenderers;

    @SuppressWarnings("unchecked")
    @Inject(method = "getRenderer(Lnet/minecraft/entity/Entity;)Lnet/minecraft/client/render/entity/EntityRenderer;", at = @At("HEAD"), cancellable = true)
    public <T extends Entity> void getRenderer(T entity, CallbackInfoReturnable<EntityRenderer<? super T, ?>> info) {
        if(FireClientside.getSetting(FireClientOption.HAZELI_MODE) == 0 || !(entity instanceof AbstractClientPlayerEntity)) {
            return;
        }

        var model = PlayerSkinType.SLIM;
        info.setReturnValue((EntityRenderer<? super T, ?>)playerRenderers.get(model));
    }
}

@Mixin(EntityRenderer.class)
abstract class HazeliNametagMixin<T extends Entity, S extends EntityRenderState> {

    @Unique
    private final Text hazeliNametag = Text.of("Hazeli");

    @ModifyArg(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitLabel(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Vec3d;ILnet/minecraft/text/Text;ZIDLnet/minecraft/client/render/state/CameraRenderState;)V"), index = 3)
    private Text changeText(Text original, @Local(ordinal = 0, argsOnly = true) S renderState) {
        if(FireClientside.getSetting(FireClientOption.HAZELI_MODE) == 0 || renderState.displayName != original) {
            return original;
        }

        return hazeliNametag;
    }
}

@Mixin(TropicalFishEntityRenderer.class)
abstract class HazeliFishMixin {

    @Inject(method = "updateRenderState*", at = @At("TAIL"))
    public void makeHazeliFish(TropicalFishEntity tropicalFishEntity, TropicalFishEntityRenderState tropicalFishEntityRenderState, float f, CallbackInfo info) {
        if(FireClientside.getSetting(FireClientOption.HAZELI_MODE) == 0) {
            return;
        }

        tropicalFishEntityRenderState.variety = TropicalFishEntity.Pattern.DASHER;
        tropicalFishEntityRenderState.baseColor = DyeColor.CYAN.getEntityColor();
        tropicalFishEntityRenderState.patternColor = DyeColor.BLUE.getEntityColor();
    }

}