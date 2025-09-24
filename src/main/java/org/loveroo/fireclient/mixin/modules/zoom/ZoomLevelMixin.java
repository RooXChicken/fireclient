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

import net.minecraft.client.option.SimpleOption;

@Mixin(SimpleOption.class)
public abstract class ZoomLevelMixin<T> {

    @Shadow
    private T value;

    @Unique
    private String key;

    @Inject(method = "<init>(Ljava/lang/String;Lnet/minecraft/client/option/SimpleOption$TooltipFactory;Lnet/minecraft/client/option/SimpleOption$ValueTextGetter;Lnet/minecraft/client/option/SimpleOption$Callbacks;Lcom/mojang/serialization/Codec;Ljava/lang/Object;Ljava/util/function/Consumer;)V", at = @At("TAIL"))
    private void storeKey(String key, SimpleOption.TooltipFactory<T> tooltipFactory, SimpleOption.ValueTextGetter<T> valueTextGetter, SimpleOption.Callbacks<T> callbacks, Codec<T> codec, Object defaultValue, Consumer<T> changeCallback, CallbackInfo info) {
        this.key = key;
    }

    @Inject(method = "getValue", at = @At("HEAD"), cancellable = true)
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
