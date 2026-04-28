package org.loveroo.fireclient.mixin.modules.kit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.commands.FKitCommand;
import org.loveroo.fireclient.data.KitManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class OverrideKitCommandMixin {

    @Inject(method = "clickCommandAction", at = @At("HEAD"), cancellable = true)
    private static void downloadKit(LocalPlayer player, String command, Screen screenAfterRun, CallbackInfo info) {
        final var prefix = "/fkit download_kit ";
        if(!command.startsWith(prefix)) {
            return;
        }

        var data = command.split(prefix)[1];
        var json = RooHelper.jsonFromStringSafe(data);

        var kitName = json.optString("name", "");
        var kitId = json.optString("id", "");

        KitManager.downloadKit(kitName, kitId, (status) -> {
            var client = Minecraft.getInstance();
            if(client.player == null) {
                return;
            }

            var message = "";
            var code = 1;

            switch(status) {
                case SUCCESS -> {
                    message = Component.translatable("fireclient.module.kit.download.success", kitName).getString();
                }

                case NO_KIT -> {
                    message = Component.translatable("fireclient.module.kit.download.failure.generic", kitName)
                                    .append(" ")
                                    .append(Component.translatable("fireclient.module.kit.download.failure.no_kit")).getString();

                    code = 0;
                }

                case INVALID_KIT -> {
                    message = Component.translatable("fireclient.module.kit.download.failure.generic", kitName)
                            .append(" ")
                            .append(Component.translatable("fireclient.module.kit.generic.invalid_kit.contents")).getString();

                    code = 0;
                }

                case ALREADY_EXISTS -> {
                    message = Component.translatable("fireclient.module.kit.download.failure.generic", kitName)
                            .append(" ")
                            .append(Component.translatable("fireclient.module.kit.generic.already_exists.contents")).getString();

                    code = 0;
                }

                case FAILURE -> {
                    message = Component.translatable("fireclient.module.kit.download.failure.generic", kitName)
                            .append(" ")
                            .append(Component.translatable("fireclient.module.kit.failure.generic_fail")).getString();

                    code = 0;
                }

                case RATE_LIMITED -> {
                    message = Component.translatable("fireclient.module.kit.download.failure.generic", kitName)
                            .append(" ")
                            .append(Component.translatable("fireclient.module.kit.server.fail.rate_limit")).getString();

                    code = 0;
                }
            }

            client.player.sendSystemMessage(FKitCommand.getResult(message, code));
        });

        info.cancel();
    }
}
