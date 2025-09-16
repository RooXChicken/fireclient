package org.loveroo.fireclient.mixin.modules.kit;

import org.loveroo.fireclient.screen.modules.KitViewScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.world.GameMode;

@Mixin(HandledScreen.class)
abstract class FixKitClicks {

    @Inject(method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;clickSlot(IIILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V"), cancellable = true)
    private void cancelServerClick(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo info) {
        var client = MinecraftClient.getInstance();
        if(!(((HandledScreen<?>)(Object)this) instanceof KitViewScreen screen)) {
            return;
        }

        screen.getScreenHandler().onSlotClick(slotId, button, actionType, client.player);
        info.cancel();
    }

    @Redirect(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;hasCreativeInventory()Z"))
    private boolean allowCloning(ClientPlayerInteractionManager interactionManager) {
        if(!(((HandledScreen<?>)(Object)this) instanceof KitViewScreen screen)) {
            return interactionManager.hasCreativeInventory();
        }

        return true;
    }
}

@Mixin(ScreenHandler.class)
abstract class FixKitHotbarKeys {

    @Shadow
    public abstract ItemStack getCursorStack();

    @Shadow
    public abstract void setCursorStack(ItemStack stack);

    @Redirect(method = "internalOnSlotClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isInCreativeMode()Z"))
    private boolean allowCloning(PlayerEntity player) {
        var client = MinecraftClient.getInstance();
        if(!(client.currentScreen instanceof KitViewScreen)) {
            return player.isInCreativeMode();
        }

        return true;
    }

    @Redirect(method = "shouldQuickCraftContinue", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isInCreativeMode()Z"))
    private static boolean allowFinishDrag(PlayerEntity player) {
        var client = MinecraftClient.getInstance();
        if(!(client.currentScreen instanceof KitViewScreen)) {
            return player.isInCreativeMode();
        }

        return true;
    }

    @Inject(method = "internalOnSlotClick", at = @At("HEAD"), cancellable = true)
    private void dropKitItem(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info) {
        var client = MinecraftClient.getInstance();

        if(!(client.currentScreen instanceof KitViewScreen) || slotIndex != -999 || actionType != SlotActionType.PICKUP) {
            return;
        }

        client.interactionManager.dropCreativeStack(getCursorStack());
        setCursorStack(ItemStack.EMPTY);

        info.cancel();
    }
}

@Mixin(ClientPlayerInteractionManager.class)
abstract class FixDropItems {

    @Shadow @Final
    private MinecraftClient client;

    @Shadow @Final
    private ClientPlayNetworkHandler networkHandler;

    @Shadow
    private GameMode gameMode;

    @Inject(method = "dropCreativeStack", at = @At("HEAD"), cancellable = true)
    private void allowDropInKit(ItemStack stack, CallbackInfo info) {
        if(!(client.currentScreen instanceof KitViewScreen) || !gameMode.isCreative() || stack.isEmpty()) {
            return;
        }

        networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(-1, stack));

        info.cancel();
    }
}
