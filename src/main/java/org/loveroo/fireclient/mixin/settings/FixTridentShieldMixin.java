package org.loveroo.fireclient.mixin.settings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.ItemInHandRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class FixTridentShieldMixin {

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    public void renderItem(AbstractClientPlayer player, float frameInterp, float xRot, InteractionHand hand, float attack, ItemStack itemStack, float inverseArmHeight, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo info) {
        if(FireClientside.getSetting(FireClientOption.FIX_TRIDENT_RIPTIDE) == 0) {
            return;
        }

        var client = Minecraft.getInstance();
        if(!client.player.isAutoSpinAttack() || !player.getItemBySlot(hand.asEquipmentSlot()).is(Items.SHIELD)) {
            return;
        }

        info.cancel();
    }
}
