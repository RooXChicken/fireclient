package org.loveroo.fireclient.mixin.modules.kit;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.GameType;
import org.loveroo.fireclient.screen.modules.KitViewScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
abstract class FixKitClicks {

    @Inject(method = "slotClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;handleContainerInput(IIILnet/minecraft/world/inventory/ContainerInput;Lnet/minecraft/world/entity/player/Player;)V"), cancellable = true)
    private void cancelServerClick(Slot slot, int slotId, int buttonNum, ContainerInput containerInput, CallbackInfo info) {
        var client = Minecraft.getInstance();
        if(!(((AbstractContainerScreen<?>)(Object)this) instanceof KitViewScreen screen)) {
            return;
        }

        screen.getMenu().clicked(slotId, buttonNum, containerInput, client.player);
        info.cancel();
    }

    @WrapOperation(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;hasInfiniteMaterials()Z"))
    private boolean allowCloning(LocalPlayer instance, Operation<Boolean> original) {
        if(!(((AbstractContainerScreen<?>)(Object)this) instanceof KitViewScreen screen)) {
            return original.call(instance);
        }

        return true;
    }
}

@Mixin(AbstractContainerMenu.class)
abstract class FixKitHotbarKeys {

    @Shadow
    public abstract ItemStack getCarried();

    @Shadow
    public abstract void setCarried(ItemStack stack);

    @WrapOperation(method = "doClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;hasInfiniteMaterials()Z"))
    private boolean allowCloning(Player instance, Operation<Boolean> original) {
        var client = Minecraft.getInstance();
        if(!(client.screen instanceof KitViewScreen)) {
            return original.call(instance);
        }

        return true;
    }

    @WrapOperation(method = "isValidQuickcraftType", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;hasInfiniteMaterials()Z"))
    private static boolean allowFinishDrag(Player instance, Operation<Boolean> original) {
        var client = Minecraft.getInstance();
        if(!(client.screen instanceof KitViewScreen)) {
            return original.call(instance);
        }

        return true;
    }

    @Inject(method = "doClick", at = @At("HEAD"), cancellable = true)
    private void dropKitItem(int slotIndex, int buttonNum, ContainerInput containerInput, Player player, CallbackInfo info) {
        var client = Minecraft.getInstance();

        if(!(client.screen instanceof KitViewScreen) || slotIndex != -999 || containerInput != ContainerInput.PICKUP) {
            return;
        }

        client.gameMode.handleCreativeModeItemDrop(getCarried());
        setCarried(ItemStack.EMPTY);

        info.cancel();
    }
}

@Mixin(MultiPlayerGameMode.class)
abstract class FixDropItems {

    @Shadow @Final
    private Minecraft minecraft;

    @Shadow @Final
    private ClientPacketListener connection;

    @Shadow
    private GameType localPlayerMode;

    @Inject(method = "handleCreativeModeItemDrop", at = @At("HEAD"), cancellable = true)
    private void allowDropInKit(ItemStack stack, CallbackInfo info) {
        if(!(minecraft.screen instanceof KitViewScreen) || !localPlayerMode.isCreative() || stack.isEmpty()) {
            return;
        }

        connection.send(new ServerboundSetCreativeModeSlotPacket(-1, stack));
        minecraft.player.getDropSpamThrottler().increment();

        info.cancel();
    }
}
