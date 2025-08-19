package org.loveroo.fireclient.mixin.settings;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.BrewingStandScreenHandler;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/screen/BrewingStandScreenHandler$FuelSlot")
public abstract class BrewingStandFuelMixin {

    @Inject(method = "matches", at = @At("HEAD"), cancellable = true)
    private static void preventFuelFill(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
        if(FireClientside.getSetting(FireClientOption.BLAZE_POWDER_FILL) == 0) {
            return;
        }

        info.setReturnValue(false);
    }
}
