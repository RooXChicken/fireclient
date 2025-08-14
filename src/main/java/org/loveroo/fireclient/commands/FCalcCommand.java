package org.loveroo.fireclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;

public class FCalcCommand {

    private static final Text fCalcHeader = RooHelper.gradientText("[FCALC]", FireClientside.mainColor1, FireClientside.mainColor2);

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        var stackSub = ClientCommandManager.literal("stack")
                .then(ClientCommandManager.argument("item_count", IntegerArgumentType.integer())
                        .executes(context -> stackSubCommand(context, 64))

                .then(ClientCommandManager.argument("stack_size", IntegerArgumentType.integer())
                        .executes(context -> stackSubCommand(context)))
        );

        var itemCountSub = ClientCommandManager.literal("item").
                then(ClientCommandManager.argument("stack_count", IntegerArgumentType.integer())
                    .executes(context -> itemSubCommand(context, 64))

                .then(ClientCommandManager.argument("stack_size", IntegerArgumentType.integer())
                        .executes(context -> itemSubCommand(context)))
        );

        dispatcher.register(ClientCommandManager.literal("fcalc")
                .then(stackSub)
                .then(itemCountSub)
        );
    }

    private Text getResult(String message) {
        var messageText = MutableText.of(new PlainTextContent.Literal(" " + message)).setStyle(Style.EMPTY).withColor(0xFFFFFFFF);
        return fCalcHeader.copy().append(messageText);
    }

    private int stackSubCommand(CommandContext<FabricClientCommandSource> context) {
        var stackSize = IntegerArgumentType.getInteger(context, "stack_size");
        return stackSubCommand(context, stackSize);
    }

    private int stackSubCommand(CommandContext<FabricClientCommandSource> context, int maxStackSize) {
        var itemCount = IntegerArgumentType.getInteger(context, "item_count");

        var stackCount = (itemCount/maxStackSize);
        var leftoverCount = (itemCount % maxStackSize);

        var result = stackCount + ((stackCount != 1) ? " stacks " : " stack ") + leftoverCount + ((leftoverCount != 1) ? " items" : " item");
        context.getSource().sendFeedback(getResult(result));

        return 1;
    }

    private int itemSubCommand(CommandContext<FabricClientCommandSource> context) {
        var stackSize = IntegerArgumentType.getInteger(context, "stack_size");
        return itemSubCommand(context, stackSize);
    }

    private int itemSubCommand(CommandContext<FabricClientCommandSource> context, int maxStackSize) {
        var stackCount = IntegerArgumentType.getInteger(context, "stack_count");

        var itemCount = (stackCount * maxStackSize);

        var result = itemCount + ((itemCount != 1) ? " items" : " item");
        context.getSource().sendFeedback(getResult(result));

        return 1;
    }
}
