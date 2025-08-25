package org.loveroo.fireclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.text.*;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.KitManager;
import org.loveroo.fireclient.modules.KitModule;

public class FKitCommand {

    private static final Text fKitHeader = RooHelper.gradientText("[FKIT]", FireClientside.mainColor1, FireClientside.mainColor2);

    private static final SuggestionProvider<FabricClientCommandSource> kitSuggestion = (context, builder) -> {
        var kits = KitManager.getKits();
        return CommandSource.suggestMatching(kits, builder);
    };

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        var loadSub = ClientCommandManager.literal("load")
                .then(ClientCommandManager.argument("kit_name", StringArgumentType.greedyString())
                        .suggests(kitSuggestion)
                        .executes(this::loadKitCommand)
        );

        var previewSub = ClientCommandManager.literal("preview")
                .then(ClientCommandManager.argument("kit_name", StringArgumentType.greedyString())
                        .suggests(kitSuggestion)
                        .executes(this::previewKitCommand)
        );

        var editSub = ClientCommandManager.literal("edit")
                .then(ClientCommandManager.argument("kit_name", StringArgumentType.greedyString())
                        .suggests(kitSuggestion)
                        .executes(this::editKitCommand)
        );

        var createSub = ClientCommandManager.literal("create")
                .then(ClientCommandManager.argument("kit_name", StringArgumentType.greedyString())
                        .executes(this::createKitCommand)
        );

        var deleteSub = ClientCommandManager.literal("delete")
                .then(ClientCommandManager.argument("kit_name", StringArgumentType.greedyString())
                        .suggests(kitSuggestion)
                        .executes(this::deleteKitCommand)
        );

        var undoSub = ClientCommandManager.literal("undo")
                    .executes(this::undoKitCommand
        );

        var shareSub = ClientCommandManager.literal("share")
                .then(ClientCommandManager.argument("kit_name", StringArgumentType.greedyString())
                        .suggests(kitSuggestion)
                        .executes(this::shareKitCommand)
        );

        dispatcher.register(ClientCommandManager.literal("fkit")
                .then(loadSub)
                .then(createSub)
                .then(deleteSub)
                .then(undoSub)
                .then(previewSub)
                .then(editSub)
                .then(shareSub)
        );
    }

    public static MutableText getResult(String message, int status) {
        var color = (status == 1) ? 0xFFFFFFFF : 0xFFC82909;
        return getResult(Text.literal(message).setStyle(Style.EMPTY).withColor(color));
    }

    public static MutableText getResult(MutableText message) {
        return fKitHeader.copy().append(" ").append(message);
    }

    private int shareKitCommand(CommandContext<FabricClientCommandSource> context) {
        var client = MinecraftClient.getInstance();
        if(client.player == null) {
            return 0;
        }

        var kitName = StringArgumentType.getString(context, "kit_name");
        var kitContents = KitManager.getKitFromName(kitName);

        KitManager.uploadKit(kitName, kitContents, (status) -> {
            var message = "";
            var code = 1;

            switch(status) {
                case SUCCESS -> { }

                case INVALID_KIT -> {
                    message = Text.translatable("fireclient.module.kit.share.failure.generic", kitName)
                                    .append(" ")
                                    .append(Text.translatable("fireclient.module.kit.generic.invalid_kit.contents")).getString();

                    code = 0;
                }

                case TOO_LARGE -> {
                    message = Text.translatable("fireclient.module.kit.share.failure.generic", kitName)
                            .append(" ")
                            .append(Text.translatable("fireclient.module.kit.share.failure.too_large")).getString();

                    code = 0;
                }

                case FAILURE -> {
                    message = Text.translatable("fireclient.module.kit.share.failure.generic", kitName)
                            .append(" ")
                            .append(Text.translatable("fireclient.module.kit.failure.generic_fail")).getString();
                    code = 0;
                }
            }

            if(!message.isEmpty()) {
                context.getSource().sendFeedback(getResult(message, code));
            }
        });

        return 1;
    }

    private int undoKitCommand(CommandContext<FabricClientCommandSource> context) {
        var message = "";
        var status = 1;

        var kitModule = (KitModule) FireClientside.getModule("kit");
        if(kitModule == null) {
            message = "Kit module not found!";
            status = 0;

            context.getSource().sendFeedback(getResult(message, status));
            return status;
        }

        var undoStatus = kitModule.undo(false);

        switch(undoStatus) {
            case SUCCESS -> message = Text.translatable("fireclient.module.kit.undo.success.title").getString();

            case INVALID_PERMS -> {
                message = Text.translatable("fireclient.module.kit.load.failure.invalid_permission.contents").getString();
                status = 0;
            }

            case INVALID_KIT -> {
                message = Text.translatable("fireclient.module.kit.generic.invalid_kit.contents").getString();
                status = 0;
            }

            case INVALID_PLAYER -> {
                message = Text.translatable("fireclient.module.kit.load.generic.invalid_player.contents").getString();
                status = 0;
            }

            case NEEDS_GMC -> {
                message = Text.translatable("fireclient.module.kit.load.waiting_gmc.title", "__previous").append(" ").append(Text.translatable("fireclient.module.kit.load.waiting_gmc.contents")).getString();
            }
        }

        context.getSource().sendFeedback(getResult(message, status));
        return 1;
    }

    private int loadKitCommand(CommandContext<FabricClientCommandSource> context) {
        var kitName = StringArgumentType.getString(context, "kit_name");

        var message = "";
        var status = 1;

        var kitModule = (KitModule) FireClientside.getModule("kit");
        if(kitModule == null) {
            status = 0;

            context.getSource().sendFeedback(getResult(message, status));
            return status;
        }

        var loadStatus = kitModule.loadKit(kitName, false);
        switch(loadStatus) {
            case SUCCESS -> message = Text.translatable("fireclient.module.kit.load.success.title", kitName).getString();

            case INVALID_PERMS -> {
                message = Text.translatable("fireclient.module.kit.load.failure.invalid_permission.contents").getString();
                status = 0;
            }

            case INVALID_KIT -> {
                message = Text.translatable("fireclient.module.kit.generic.invalid_kit.contents").getString();
                status = 0;
            }

            case INVALID_PLAYER -> {
                message = Text.translatable("fireclient.module.kit.load.generic.invalid_player.contents").getString();
                status = 0;
            }

            case NEEDS_GMC -> {
                message = Text.translatable("fireclient.module.kit.load.waiting_gmc.title", kitName).append(" ").append(Text.translatable("fireclient.module.kit.load.waiting_gmc.contents")).getString();
            }
        }

        context.getSource().sendFeedback(getResult(message, status));
        return status;
    }

    private int previewKitCommand(CommandContext<FabricClientCommandSource> context) {
        var kitName = StringArgumentType.getString(context, "kit_name");

        return openKitScreen(context, KitManager.previewKit(kitName, true));
    }

    private int editKitCommand(CommandContext<FabricClientCommandSource> context) {
        var kitName = StringArgumentType.getString(context, "kit_name");

        return openKitScreen(context, KitManager.editKit(kitName, true));
    }

    private int openKitScreen(CommandContext<FabricClientCommandSource> context, KitManager.KitViewStatus viewStatus) {
        var message = "";
        var status = 1;

        switch(viewStatus) {
            case SUCCESS -> { return 1; }

            case INVALID_KIT -> {
                message = Text.translatable("fireclient.module.kit.generic.invalid_kit.contents").getString();
                status = 0;
            }

            case INVALID_PLAYER -> {
                message = Text.translatable("fireclient.module.kit.load.generic.invalid_player.contents").getString();
                status = 0;
            }
        }

        context.getSource().sendFeedback(getResult(message, status));
        return status;
    }

    private int createKitCommand(CommandContext<FabricClientCommandSource> context) {
        var kitName = StringArgumentType.getString(context, "kit_name");

        var message = "";
        var status = 1;

        var createStatus = KitManager.createKit(kitName, KitManager.getPlayerInventoryString());
        switch(createStatus) {
            case SUCCESS -> message = Text.translatable("fireclient.module.kit.generic.create.success", kitName).getString();

            case ALREADY_EXISTS -> {
                message = Text.translatable("fireclient.module.kit.generic.already_exists.contents").getString();
                status = 0;
            }

            case INVALID_KIT -> {
                message = Text.translatable("fireclient.module.kit.generic.invalid_kit.contents").getString();
                status = 0;
            }

            case WRITE_FAIL -> {
                message = Text.translatable("fireclient.module.kit.generic.write_failure.contents").getString();
                status = 0;
            }
        }

        context.getSource().sendFeedback(getResult(message, status));
        return status;
    }

    private int deleteKitCommand(CommandContext<FabricClientCommandSource> context) {
        var kitName = StringArgumentType.getString(context, "kit_name");

        var message = "";
        var status = 1;

        var deleteStatus = KitManager.deleteKit(kitName);
        switch(deleteStatus) {
            case SUCCESS -> message = Text.translatable("fireclient.module.kit.recycle.success.title", kitName).append(" ").append(Text.translatable("fireclient.module.kit.recycle.success.contents")).getString();

            case FAILURE -> {
                message = Text.translatable("fireclient.module.kit.recycle.failure.title", kitName).append(" ").append(Text.translatable("fireclient.module.kit.recycle.failure.contents")).getString();
                status = 0;
            }
        }

        context.getSource().sendFeedback(getResult(message, status));
        return status;
    }
}
