package org.loveroo.fireclient.mixin.modules.kit;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.loveroo.fireclient.commands.FKitCommand;
import org.loveroo.fireclient.data.KitManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class OverrideKitCommandMixin {

    @Inject(method = "handleRunCommand", at = @At("HEAD"), cancellable = true)
    private static void downloadKit(ClientPlayerEntity player, String command, Screen screenAfterRun, CallbackInfo info) {
        if(!command.startsWith("fkit download_kit ")) {
            return;
        }

        var values = command.substring(18);
        var kitId = values.substring(0, 36);
        var kitName = values.substring(37);

        var status = KitManager.downloadKit(kitName, kitId);

        var client = MinecraftClient.getInstance();

        var message = "";
        var code = 1;

        switch(status.status()) {
            case SUCCESS -> {
                message = Text.translatable("fireclient.module.kit.download.success", kitName).getString();
            }

            case NO_KIT -> {
                message = Text.translatable("fireclient.module.kit.download.failure.no_kit",
                        Text.translatable("fireclient.module.kit.download.failure.generic", kitName)).getString();

                code = 0;
            }

            case INVALID_KIT -> {
                message = Text.translatable("fireclient.module.kit.download.failure.invalid_kit",
                        Text.translatable("fireclient.module.kit.download.failure.generic", kitName)).getString();

                code = 0;
            }

            case ALREADY_EXISTS -> {
                message = Text.translatable("fireclient.module.kit.download.failure.already_exists",
                        Text.translatable("fireclient.module.kit.download.failure.generic", kitName)).getString();

                code = 0;
            }

            case FAILURE -> {
                message = Text.translatable("fireclient.module.kit.download.failure.generic", kitName).getString();
                code = 0;
            }
        }

        client.player.sendMessage(FKitCommand.getResult(message, code), false);
        info.cancel();
    }
}
