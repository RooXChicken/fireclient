package org.loveroo.fireclient.mixin.modules.mutesounds;

import org.lwjgl.openal.AL10;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.Source;

@Mixin(SoundSystem.class)
public abstract class RemoveVolumeCapMixin {

    @ModifyConstant(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", constant = @Constant(floatValue = 1.0f))
    private float removeCap(float original) {
        return 2.0f;
    }
    
    @ModifyConstant(method = "getAdjustedVolume(FLnet/minecraft/sound/SoundCategory;)F", constant = @Constant(floatValue = 1.0f))
    private float removeCapAdjusted(float original) {
        return 2.0f;
    }
}

@Mixin(Source.class)
abstract class ChangeMaxGain {

    // changes OpenAL's max volume (normally 1.0f)
    @Inject(method = "<init>", at = @At("TAIL"))
    private void changeMax(int pointer, CallbackInfo info) {
        AL10.alSourcef(pointer, AL10.AL_MAX_GAIN, 2.0f);
    }
}