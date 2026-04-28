package org.loveroo.fireclient.mixin.modules.zoom;

import java.util.function.Consumer;

import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.ZoomModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.serialization.Codec;

import net.minecraft.client.OptionInstance;

@Mixin(OptionInstance.class)
public abstract class ZoomLevelMixin<T> {

    @Shadow
    private T value;

    @Unique
    private String key;

    @Inject(method = "<init>(Ljava/lang/String;Lnet/minecraft/client/OptionInstance$TooltipSupplier;Lnet/minecraft/client/OptionInstance$CaptionBasedToString;Lnet/minecraft/client/OptionInstance$ValueSet;Lcom/mojang/serialization/Codec;Ljava/lang/Object;Ljava/util/function/Consumer;)V", at = @At("TAIL"))
    private void storeKey(String captionId, OptionInstance.TooltipSupplier<T> tooltip, OptionInstance.CaptionBasedToString<T> toString, OptionInstance.ValueSet<T> values, Codec<T> codec, Object initialValue, Consumer<T> onValueUpdate, CallbackInfo info) {
        this.key = captionId;
    }

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private void modifyFov(CallbackInfoReturnable<Object> info) {
        if(!key.equals("options.fov")) {
            return;
        }
        
        var zoom = (ZoomModule) FireClientside.getModule("zoom");
        if(zoom == null || !zoom.isZooming()) {
            return;
        }

        info.setReturnValue(Math.clamp(zoom.getZoomLevel(), 3, (Integer)value));
    }
}
