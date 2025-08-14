package org.loveroo.fireclient.mixin.settings;

import net.minecraft.client.gui.screen.DeathScreen;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public abstract class DontResetDeathButtonsMixin {

    @Shadow
    private int ticksSinceDeath;

    @Shadow protected abstract void setButtonsActive(boolean active);

    @Unique
    private int realTicksSinceDeath = 0;

    @Inject(method = "init", at = @At("HEAD"))
    public void storeCurrentTicks(CallbackInfo ci) {
        realTicksSinceDeath = ticksSinceDeath;
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void restoreTicks(CallbackInfo ci) {
        if(FireClientside.getSetting(FireClientOption.DONT_RESET_DEATH) == 0) {
            return;
        }

        ticksSinceDeath = realTicksSinceDeath;
        if(realTicksSinceDeath >= 20) {
            setButtonsActive(true);
        }
    }
}
