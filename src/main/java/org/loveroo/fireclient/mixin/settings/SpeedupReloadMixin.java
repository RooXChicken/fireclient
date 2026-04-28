package org.loveroo.fireclient.mixin.settings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.util.Util;
import net.minecraft.util.Mth;
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

@Mixin(LoadingOverlay.class)
public abstract class SpeedupReloadMixin {

    @Shadow
    private long fadeOutStart;

    @Shadow @Final
    private Minecraft minecraft;

    @Shadow @Final
    private ReloadInstance reload;

    @Shadow @Final
    private Consumer<Optional<Throwable>> onFinish;

    @Shadow @Final
    private boolean fadeIn;

    @Shadow
    private long fadeInStart;

    @Shadow private float currentProgress;

    @Shadow
    protected abstract void extractProgressBar(GuiGraphicsExtractor graphics, int x0, int y0, int x1, int y1, float fade);

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void closeIfFinished(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo info) {
        if(FireClientside.getSetting(FireClientOption.NO_RELOAD_OVERLAY) == 0) {
            return;
        }

        long l = Util.getMillis();
        float f = this.fadeOutStart > -1L ? (float)(l - fadeOutStart) / 1000.0F : -1.0F;
        float g = fadeInStart > -1L ? (float)(l - fadeInStart) / 500.0F : -1.0F;

        if (f >= 1.0F) {
            if (this.minecraft.screen != null) {
                this.minecraft.screen.extractRenderState(graphics, 0, 0, a);
            }
        }
        if (f < 1.0F) {
            int i = graphics.guiWidth();
            int j = graphics.guiHeight();

            double d = Math.min(graphics.guiWidth() * 0.75, graphics.guiHeight()) * 0.25;
            int q = (int)(d * 0.5);
            double e = d * 4.0;
            int r = (int)(e * 0.5);
            int t = (int)(graphics.guiHeight() * 0.8325);

            float u = this.reload.getActualProgress();
            currentProgress = Mth.clamp(currentProgress * 0.95F + u * 0.050000012F, 0.0F, 1.0F);

            extractProgressBar(graphics, i / 2 - r, t - 5, i / 2 + r, t + 5, 1.0F - Mth.clamp(f, 0.0F, 1.0F));
        }

        if (this.fadeOutStart == -1L && reload.isDone()) {
            minecraft.setOverlay(null);

            try {
                reload.checkExceptions();
                onFinish.accept(Optional.empty());
            } catch (Throwable var24) {
                onFinish.accept(Optional.of(var24));
            }

            this.fadeOutStart = Util.getMillis();
            if (this.minecraft.screen != null) {
                this.minecraft.screen.init(graphics.guiWidth(), graphics.guiHeight());
            }
        }

        info.cancel();
    }

    @Inject(method = "isPauseScreen", at = @At("HEAD"), cancellable = true)
    private void removePause(CallbackInfoReturnable<Boolean> info) {
        if(FireClientside.getSetting(FireClientOption.NO_RELOAD_OVERLAY) == 0) {
            return;
        }

        info.setReturnValue(false);
    }
}
