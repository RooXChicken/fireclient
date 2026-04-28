package org.loveroo.fireclient.mixin.modules.kit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.commands.FKitCommand;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.KitManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public class ReplaceSharedKitsMixin {

    @Unique
    private final Color downloadColor = Color.fromRGB(0x56F051);

    @Inject(method = "addMessage", at = @At("HEAD"), cancellable = true)
    private void replaceWithKit(Component message, MessageSignature signature, GuiMessageSource source, GuiMessageTag tag, CallbackInfo info) {
        var contents = message.getString();

        if(!KitManager.isSharedKit(contents)) {
            return;
        }

        var senderName = KitManager.getSharedKitSender(contents).replaceAll("[^A-Za-z0-9_]", "");
        var kitName = KitManager.getSharedKitName(contents);

        var click = new ClickEvent.RunCommand("/fkit download_kit " + KitManager.getSharedKitJson(contents));
        var hover = new HoverEvent.ShowText(Component.translatable("fireclient.module.kit.share.download.tooltip", kitName));

        var chatShare = Component.translatable("fireclient.module.kit.share.download.name").setStyle(
                Style.EMPTY.withClickEvent(click).withHoverEvent(hover).withColor(downloadColor.toInt()));

        var shareText = FKitCommand.getResult(Component.translatable("fireclient.module.kit.share.message", senderName, kitName, chatShare));

        var client = Minecraft.getInstance();
        if(client.player == null) {
            return;
        }

        client.player.sendSystemMessage(shareText);
        info.cancel();
    }
}
