package org.loveroo.fireclient.mixin.modules.localskin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.PlayerSkinTextureDownloader;

@Mixin(PlayerSkinTextureDownloader.class)
public interface RemapTextureAccessor {

    @Invoker("remapTexture")
    public static NativeImage invokeRemapTexture(NativeImage image, String uri) { 
        throw new AssertionError();
    }
}
