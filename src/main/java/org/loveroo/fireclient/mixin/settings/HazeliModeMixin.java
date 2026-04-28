package org.loveroo.fireclient.mixin.settings;

import java.util.Map;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.player.PlayerModelType;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.TropicalFishRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.TropicalFishRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.fish.TropicalFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;

//@Mixin(SkinTextures.class)
//abstract class HazeliModeMixin {
//
//    @Unique
//    private final Identifier hazeli = Identifier.of(FireClient.MOD_ID, "textures/skin/hazeli.png");
//
//    @Inject(method = "body", at = @At("HEAD"), cancellable = true)
//    public void getTexture(CallbackInfoReturnable<Identifier> info) {
//        if(FireClientside.getSetting(FireClientOption.HAZELI_MODE) == 0) {
//            return;
//        }
//
//        info.setReturnValue(hazeli);
//    }
//}

@Mixin(EntityRenderDispatcher.class)
abstract class HazeliPlayerModelMixin {

    @Shadow
    private Map<PlayerModelType, EntityRenderer<? extends Player, ?>> playerRenderers;

    @SuppressWarnings("unchecked")
    @Inject(method = "getRenderer(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/client/renderer/entity/EntityRenderer;", at = @At("HEAD"), cancellable = true)
    public <T extends Entity> void getRenderer(T entity, CallbackInfoReturnable<EntityRenderer<? super T, ?>> info) {
        if(FireClientside.getSetting(FireClientOption.HAZELI_MODE) == 0 || !(entity instanceof AbstractClientPlayer)) {
            return;
        }

        var model = PlayerModelType.SLIM;
        info.setReturnValue((EntityRenderer<? super T, ?>)playerRenderers.get(model));
    }
}

@Mixin(EntityRenderer.class)
abstract class HazeliNametagMixin<T extends Entity, S extends EntityRenderState> {

    @Unique
    private final Component hazeliNametag = Component.nullToEmpty("Hazeli");

    @ModifyArg(method = "submitNameDisplay(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitNameTag(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/phys/Vec3;ILnet/minecraft/network/chat/Component;ZIDLnet/minecraft/client/renderer/state/level/CameraRenderState;)V"), index = 3)
    private Component changeText(Component original, @Local(argsOnly = true, name = "state") S state) {
        if(FireClientside.getSetting(FireClientOption.HAZELI_MODE) == 0 || state.nameTag != original) {
            return original;
        }

        return hazeliNametag;
    }
}

@Mixin(TropicalFishRenderer.class)
abstract class HazeliFishMixin {

    // TODO(Ravel): wildcard and regex target are not supported
// TODO(Ravel): wildcard and regex target are not supported
    @Inject(method = "extractRenderState*", at = @At("TAIL"))
    public void makeHazeliFish(TropicalFish entity, TropicalFishRenderState state, float partialTicks, CallbackInfo info) {
        if(FireClientside.getSetting(FireClientOption.HAZELI_MODE) == 0) {
            return;
        }

        state.pattern = TropicalFish.Pattern.DASHER;
        state.baseColor = DyeColor.CYAN.getTextureDiffuseColor();
        state.patternColor = DyeColor.BLUE.getTextureDiffuseColor();
    }

}