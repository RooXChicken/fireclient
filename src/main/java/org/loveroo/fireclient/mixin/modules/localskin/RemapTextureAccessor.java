package org.loveroo.fireclient.mixin.modules.localskin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SkinTextureDownloader;

@Mixin(SkinTextureDownloader.class)
public interface RemapTextureAccessor {

    @Invoker("processLegacySkin")
    public static NativeImage invokeProcessLegacySkin(NativeImage image, String uri) {
        throw new AssertionError();
    }
}
