package org.loveroo.fireclient.mixin.settings;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.LoadingOverlay;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
abstract class AllowReloadInputsClientMixin {

    @WrapOperation(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;overlay:Lnet/minecraft/client/gui/screens/Overlay;", opcode = Opcodes.GETFIELD))
    private Overlay returnNoOverlay(Minecraft instance, Operation<Overlay> original) {
        return RooHelper.getOverlay(instance);
    }

    @WrapOperation(method = "handleKeybinds", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;overlay:Lnet/minecraft/client/gui/screens/Overlay;", opcode = Opcodes.GETFIELD))
    private Overlay returnNoOverlayInput(Minecraft instance, Operation<Overlay> original) {
        return RooHelper.getOverlay(instance);
    }
}

@Mixin(MouseHandler.class)
abstract class AllowReloadInputsMouseHandlerMixin {

    @WrapOperation(method = "handleAccumulatedMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getOverlay()Lnet/minecraft/client/gui/screens/Overlay;"))
    private Overlay returnNoOverlay(Minecraft instance, Operation<Overlay> original) {
        return RooHelper.getOverlay(instance);
    }

    @WrapOperation(method = "onButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getOverlay()Lnet/minecraft/client/gui/screens/Overlay;"))
    private Overlay returnNoOverlayInput(Minecraft instance, Operation<Overlay> original) {
        return RooHelper.getOverlay(instance);
    }
}

@Mixin(KeyboardHandler.class)
abstract class AllowReloadInputsKeyboardHandlerMixin {

    @WrapOperation(method = "charTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getOverlay()Lnet/minecraft/client/gui/screens/Overlay;"))
    private Overlay returnNoOverlay(Minecraft instance, Operation<Overlay> original) {
        return RooHelper.getOverlay(instance);
    }
}
