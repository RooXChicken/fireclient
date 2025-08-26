package org.loveroo.fireclient.mixin.settings;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.resource.ResourceReload;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(SplashOverlay.class)
public abstract class SpeedupReloadMixin {

    @Shadow
    private long reloadCompleteTime;

    @Shadow @Final
    private MinecraftClient client;

    @Shadow
    protected abstract void renderProgressBar(DrawContext context, int minX, int minY, int maxX, int maxY, float opacity);

    @Shadow @Final
    private ResourceReload reload;

    @Shadow @Final
    private Consumer<Optional<Throwable>> exceptionHandler;

    @Shadow @Final
    private boolean reloading;

    @Shadow
    private long reloadStartTime;

    @Shadow private float progress;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void closeIfFinished(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if(FireClientside.getSetting(FireClientOption.NO_RELOAD_OVERLAY) == 0) {
            return;
        }

        long l = Util.getMeasuringTimeMs();
        float f = this.reloadCompleteTime > -1L ? (float)(l - reloadCompleteTime) / 1000.0F : -1.0F;
        float g = reloadStartTime > -1L ? (float)(l - reloadStartTime) / 500.0F : -1.0F;

        if (f >= 1.0F) {
            if (this.client.currentScreen != null) {
                this.client.currentScreen.render(context, 0, 0, delta);
            }
        }
        if (f < 1.0F) {
            int i = context.getScaledWindowWidth();
            int j = context.getScaledWindowHeight();

            double d = Math.min(context.getScaledWindowWidth() * 0.75, context.getScaledWindowHeight()) * 0.25;
            int q = (int)(d * 0.5);
            double e = d * 4.0;
            int r = (int)(e * 0.5);
            int t = (int)(context.getScaledWindowHeight() * 0.8325);

            float u = this.reload.getProgress();
            progress = MathHelper.clamp(progress * 0.95F + u * 0.050000012F, 0.0F, 1.0F);

            renderProgressBar(context, i / 2 - r, t - 5, i / 2 + r, t + 5, 1.0F - MathHelper.clamp(f, 0.0F, 1.0F));
        }

        if (this.reloadCompleteTime == -1L && reload.isComplete()) {
            client.setOverlay(null);

            try {
                reload.throwException();
                exceptionHandler.accept(Optional.empty());
            } catch (Throwable var24) {
                exceptionHandler.accept(Optional.of(var24));
            }

            this.reloadCompleteTime = Util.getMeasuringTimeMs();
            if (this.client.currentScreen != null) {
                this.client.currentScreen.init(this.client, context.getScaledWindowWidth(), context.getScaledWindowHeight());
            }
        }

        info.cancel();
    }

    @Inject(method = "pausesGame", at = @At("HEAD"), cancellable = true)
    private void removePause(CallbackInfoReturnable<Boolean> info) {
        if(FireClientside.getSetting(FireClientOption.NO_RELOAD_OVERLAY) == 0) {
            return;
        }

        info.setReturnValue(false);
    }
}
