package org.loveroo.fireclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
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

        dispatcher.register(ClientCommandManager.literal("fkit")
                .then(loadSub)
                .then(createSub)
                .then(deleteSub)
                .then(undoSub)
        );
    }

    private Text getResult(String message, int status) {
        var color = (status == 1) ? 0xFFFFFFFF : 0xFFC82909;
        var messageText = MutableText.of(new PlainTextContent.Literal(" " + message)).setStyle(Style.EMPTY).withColor(color);

        return fKitHeader.copy().append(messageText);
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
            case SUCCESS -> message = "Success!";

            case INVALID_PERMS -> {
                message = "Invalid permission!";
                status = 0;
            }

            case INVALID_KIT -> {
                message = "Invalid kit!";
                status = 0;
            }

            case INVALID_PLAYER -> {
                message = "Invalid player!";
                status = 0;
            }

            case NEEDS_GMC -> {
                message = "Loading \"__previous\"... Waiting for Creative Mode";
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
            message = "Kit module not found!";
            status = 0;

            context.getSource().sendFeedback(getResult(message, status));
            return status;
        }

        var loadStatus = kitModule.loadKit(kitName, false);
        switch(loadStatus) {
            case SUCCESS -> message = "Successfully loaded \"" + kitName + "\"!";

            case INVALID_PERMS -> {
                message = "Invalid permission!";
                status = 0;
            }

            case INVALID_KIT -> {
                message = "Invalid kit!";
                status = 0;
            }

            case INVALID_PLAYER -> {
                message = "Invalid player!";
                status = 0;
            }

            case NEEDS_GMC -> {
                message = "Loading \"" + kitName + "\"... Waiting for Creative Mode";
            }
        }

        context.getSource().sendFeedback(getResult(message, status));
        return status;
    }

    private int createKitCommand(CommandContext<FabricClientCommandSource> context) {
        var kitName = StringArgumentType.getString(context, "kit_name");

        var message = "";
        var status = 1;

        var createStatus = KitManager.createKit(kitName, KitManager.getKitString());
        switch(createStatus) {
            case SUCCESS -> message = "Successfully created \"" + kitName + "\"!";

            case ALREADY_EXISTS -> {
                message = "\"" + kitName + "\" already exists!";
                status = 0;
            }

            case INVALID_KIT -> {
                message = "Invalid kit!";
                status = 0;
            }

            case WRITE_FAIL -> {
                message = "Failed to write \"" + kitName + "\"!";
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
            case SUCCESS -> message = "Successfully recycled \"" + kitName + "\"! It will be deleted next startup!";

            case FAILURE -> {
                message = "Failed to delete \"" + kitName + "\"! The kit won't be deleted";
                status = 0;
            }
        }

        context.getSource().sendFeedback(getResult(message, status));
        return status;
    }
}
