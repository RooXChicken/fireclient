package org.loveroo.fireclient.mixin.modules.kit;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import org.loveroo.fireclient.commands.FKitCommand;
import org.loveroo.fireclient.data.KitManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Screen.class)
public class OverrideKitCommandMixin {

    @Redirect(method = "handleTextClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendCommand(Ljava/lang/String;)Z"))
    private boolean downloadKit(ClientPlayNetworkHandler network, String command) {
        if(!command.startsWith("fkit download_kit ")) {
            return network.sendCommand(command);
        }

        var values = command.substring(18);
        var kitId = values.substring(0, 36);
        var kitName = values.substring(37);

        var status = KitManager.downloadKit(kitName, kitId);

        var client = MinecraftClient.getInstance();
        Text text = null;

        switch(status.status()) {
            case SUCCESS -> {
                text = FKitCommand.getResult(Text.translatable("fireclient.module.kit.download.success", kitName).getString(), 1);
            }

            case NO_KIT -> {
                text = FKitCommand.getResult(
                        Text.translatable("fireclient.module.kit.download.failure.no_kit",
                                Text.translatable("fireclient.module.kit.download.failure.generic", kitName).getString()).getString(), 0);
            }

            case INVALID_KIT -> {
                text = FKitCommand.getResult(
                        Text.translatable("fireclient.module.kit.download.failure.invalid_kit",
                                Text.translatable("fireclient.module.kit.download.failure.generic", kitName).getString()).getString(), 0);
            }

            case ALREADY_EXISTS -> {
                text = FKitCommand.getResult(
                        Text.translatable("fireclient.module.kit.download.failure.already_exists",
                                Text.translatable("fireclient.module.kit.download.failure.generic", kitName).getString()).getString(), 0);
            }

            case FAILURE -> {
                text = FKitCommand.getResult(
                        Text.translatable("fireclient.module.kit.download.failure.generic", kitName).getString(), 0);
            }
        }

        client.player.sendMessage(text, false);
        return true;
    }
}
