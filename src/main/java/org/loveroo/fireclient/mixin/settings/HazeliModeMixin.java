package org.loveroo.fireclient.mixin.settings;

import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.Identifier;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public abstract class HazeliModeMixin {

    @Unique
    private final Identifier hazeli = Identifier.of(FireClient.MOD_ID, "textures/skin/hazeli.png");

//    @Unique
//    private PlayerEntityModel oldModel;
//
////    @ModifyVariable(method = "<init>(Lnet/minecraft/client/render/entity/EntityRendererFactory$Context;Z)V", at = @At("HEAD"), ordinal = 0)
////    private static boolean setSlim(boolean slim) {
////        if(FireClientside.getSetting(FireClientOption.HAZELI_MODE) == 0) {
////            return slim;
////        }
////
////        return true;
////    }

    @Inject(method = "getTexture(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;)Lnet/minecraft/util/Identifier;", at = @At("HEAD"), cancellable = true)
    public void getTexture(PlayerEntityRenderState playerEntityRenderState, CallbackInfoReturnable<Identifier> info) {
        if(FireClientside.getSetting(FireClientOption.HAZELI_MODE) == 0) {
            return;
        }

        info.setReturnValue(hazeli);
    }
}
