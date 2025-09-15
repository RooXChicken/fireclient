package org.loveroo.fireclient.mixin.settings;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import java.util.Map;

@Mixin(SkinTextures.class)
abstract class HazeliModeMixin {

    @Unique
    private final Identifier hazeli = Identifier.of(FireClient.MOD_ID, "textures/skin/hazeli.png");

    @Inject(method = "texture", at = @At("HEAD"), cancellable = true)
    public void getTexture(CallbackInfoReturnable<Identifier> info) {
        if(FireClientside.getSetting(FireClientOption.HAZELI_MODE) == 0) {
            return;
        }

        info.setReturnValue(hazeli);
    }
}

@Mixin(EntityRenderDispatcher.class)
abstract class HazeliPlayerModelMixin {

    @Shadow
    private Map<SkinTextures.Model, EntityRenderer<? extends PlayerEntity, ?>> modelRenderers;

    @SuppressWarnings("unchecked")
    @Inject(method = "getRenderer(Lnet/minecraft/entity/Entity;)Lnet/minecraft/client/render/entity/EntityRenderer;", at = @At("HEAD"), cancellable = true)
    public <T extends Entity> void getRenderer(T entity, CallbackInfoReturnable<EntityRenderer<? super T, ?>> info) {
        if(FireClientside.getSetting(FireClientOption.HAZELI_MODE) == 0 || !(entity instanceof AbstractClientPlayerEntity)) {
            return;
        }

        var model = SkinTextures.Model.SLIM;
        info.setReturnValue((EntityRenderer<? super T, ?>)modelRenderers.get(model));
    }
}

@Mixin(EntityRenderer.class)
abstract class HazeliNametagMixin<T extends Entity, S extends EntityRenderState> {

    @Unique
    private final Text hazeliNametag = Text.of("Hazeli");

    @ModifyVariable(method = "renderLabelIfPresent", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private Text changeText(Text original, @Local(ordinal = 0) S renderState) {
        if(FireClientside.getSetting(FireClientOption.HAZELI_MODE) == 0 || renderState.displayName != original) {
            return original;
        }

        return hazeliNametag;
    }
}