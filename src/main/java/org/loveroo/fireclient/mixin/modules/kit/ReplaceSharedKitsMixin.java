package org.loveroo.fireclient.mixin.modules.kit;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.commands.FKitCommand;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.KitManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ReplaceSharedKitsMixin {

    @Unique
    private final Color downloadColor = Color.fromRGB(0x56F051);

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("HEAD"), cancellable = true)
    private void replaceWithKit(Text message, MessageSignatureData signatureData, MessageIndicator indicator, CallbackInfo info) {
        var contents = message.getString();

        if(!KitManager.isSharedKit(contents)) {
            return;
        }

        var senderName = KitManager.getSharedKitSender(contents).replaceAll("[^A-Za-z0-9_]", "");
        var kitName = KitManager.getSharedKitName(contents);

        var click = new ClickEvent.RunCommand("/fkit download_kit " + KitManager.getSharedKitJson(contents));
        var hover = new HoverEvent.ShowText(Text.translatable("fireclient.module.kit.share.download.tooltip", kitName));

        var chatShare = Text.translatable("fireclient.module.kit.share.download.name").setStyle(
                Style.EMPTY.withClickEvent(click).withHoverEvent(hover).withColor(downloadColor.toInt()));

        var shareText = FKitCommand.getResult(Text.translatable("fireclient.module.kit.share.message", senderName, kitName, chatShare));

        var client = MinecraftClient.getInstance();
        if(client.player == null) {
            return;
        }

        client.player.sendMessage(shareText, false);
        info.cancel();
    }
}
