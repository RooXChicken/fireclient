package org.loveroo.fireclient.mixin.modules.mutesounds;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.lwjgl.openal.AL10;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.sounds.SoundEngine;
import com.mojang.blaze3d.audio.Channel;

@Mixin(SoundEngine.class)
public abstract class RemoveVolumeCapMixin {

    @ModifyExpressionValue(method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)Lnet/minecraft/client/sounds/SoundEngine$PlayResult;", at = @At(value = "CONSTANT", args = "floatValue=1.0f"))
    private float removeCap(float original) {
        return 2.0f;
    }
    
    @ModifyExpressionValue(method = "calculateVolume(FLnet/minecraft/sounds/SoundSource;)F", at = @At(value = "CONSTANT", args = "floatValue=1.0f"))
    private float removeCapAdjusted(float original) {
        return 2.0f;
    }
}

@Mixin(Channel.class)
abstract class ChangeMaxGain {

    // changes OpenAL's max volume (normally 1.0f)
    @Inject(method = "<init>", at = @At("TAIL"))
    private void changeMax(int src, CallbackInfo info) {
        AL10.alSourcef(src, AL10.AL_MAX_GAIN, 2.0f);
    }
}