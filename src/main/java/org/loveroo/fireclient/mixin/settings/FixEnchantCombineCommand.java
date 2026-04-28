package org.loveroo.fireclient.mixin.settings;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Enchantment.class)
public class FixEnchantCombineCommand {

//    @Redirect(method = "canBeCombined", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/entry/RegistryEntry;equals(Ljava/lang/Object;)Z"))
//    private static boolean allowCombining(RegistryEntry instance, Object o) {
//        return false;
//    }
}
