package org.loveroo.fireclient.mixin.modules.kit;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.commands.FKitCommand;
import org.loveroo.fireclient.data.KitManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Screen.class)
public class OverrideKitCommandMixin {

    @Redirect(method = "handleTextClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendCommand(Ljava/lang/String;)Z"))
    private boolean downloadKit(ClientPlayNetworkHandler network, String command) {
        final var prefix = "fkit download_kit ";
        if(!command.startsWith(prefix)) {
            return network.sendCommand(command);
        }

        var data = command.split(prefix)[1];
        var json = RooHelper.jsonFromStringSafe(data);

        var kitName = json.optString("name", "");
        var kitId = json.optString("id", "");

        KitManager.downloadKit(kitName, kitId, (status) -> {
            var client = MinecraftClient.getInstance();
            if(client.player == null) {
                return;
            }

            var message = "";
            var code = 1;

            switch(status) {
                case SUCCESS -> {
                    message = Text.translatable("fireclient.module.kit.download.success", kitName).getString();
                }

                case NO_KIT -> {
                    message = Text.translatable("fireclient.module.kit.download.failure.generic", kitName)
                            .append(" ")
                            .append(Text.translatable("fireclient.module.kit.download.failure.no_kit")).getString();

                    code = 0;
                }

                case INVALID_KIT -> {
                    message = Text.translatable("fireclient.module.kit.download.failure.generic", kitName)
                            .append(" ")
                            .append(Text.translatable("fireclient.module.kit.generic.invalid_kit.contents")).getString();

                    code = 0;
                }

                case ALREADY_EXISTS -> {
                    message = Text.translatable("fireclient.module.kit.download.failure.generic", kitName)
                            .append(" ")
                            .append(Text.translatable("fireclient.module.kit.generic.already_exists.contents")).getString();

                    code = 0;
                }

                case FAILURE -> {
                    message = Text.translatable("fireclient.module.kit.download.failure.generic", kitName)
                            .append(" ")
                            .append(Text.translatable("fireclient.module.kit.failure.generic_fail")).getString();

                    code = 0;
                }

                case RATE_LIMITED -> {
                    message = Text.translatable("fireclient.module.kit.download.failure.generic", kitName)
                            .append(" ")
                            .append(Text.translatable("fireclient.module.kit.server.fail.rate_limit")).getString();

                    code = 0;


                }
            }

            client.player.sendMessage(FKitCommand.getResult(message, code), false);
        });

        return true;
    }
}
