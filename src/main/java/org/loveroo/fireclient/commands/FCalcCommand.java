package org.loveroo.fireclient.commands;

import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.MathCalculator;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class FCalcCommand {

    private static final Text fCalcHeader = RooHelper.gradientText("[FCALC]", FireClientside.mainColor1, FireClientside.mainColor2);

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        var stackSub = ClientCommandManager.literal("stack")
            .then(ClientCommandManager.argument("item_count", IntegerArgumentType.integer())
                .executes(context -> stackSubCommand(context, 64))

            .then(ClientCommandManager.argument("stack_size", IntegerArgumentType.integer())
                .executes(context -> stackSubCommand(context)))
        );

        var itemCountSub = ClientCommandManager.literal("item")
            .then(ClientCommandManager.argument("stack_count", IntegerArgumentType.integer())
                .executes(context -> itemSubCommand(context, 64))

            .then(ClientCommandManager.argument("stack_size", IntegerArgumentType.integer())
                .executes(context -> itemSubCommand(context)))
        );

        var mathSub = ClientCommandManager.literal("math")
            .then(ClientCommandManager.argument("equation", StringArgumentType.greedyString())
                .executes(this::mathSubCommand)
        );

        var coordsSub = ClientCommandManager.literal("coords")
            .then(ClientCommandManager.literal("to_nether")
                .then(ClientCommandManager.argument("x", DoubleArgumentType.doubleArg())
                .then(ClientCommandManager.argument("z", DoubleArgumentType.doubleArg())
                    .executes(this::toNetherCommand))))

            .then(ClientCommandManager.literal("to_overworld")
                .then(ClientCommandManager.argument("x", DoubleArgumentType.doubleArg())
                .then(ClientCommandManager.argument("z", DoubleArgumentType.doubleArg())
                    .executes(this::toOverworldCommand)))
        );

        dispatcher.register(ClientCommandManager.literal("fcalc")
            .then(stackSub)
            .then(itemCountSub)
            .then(mathSub)
            .then(coordsSub)
        );
    }

    private int mathSubCommand(CommandContext<FabricClientCommandSource> context) {
        var equation = StringArgumentType.getString(context, "equation");

        context.getSource().sendFeedback(getResult(MathCalculator.calculate(equation) + ""));
        return 1;
    }

    private int toNetherCommand(CommandContext<FabricClientCommandSource> context) {
        var x = DoubleArgumentType.getDouble(context, "x");
        var z = DoubleArgumentType.getDouble(context, "z");
        
        context.getSource().sendFeedback(getResult(getCoords(x/8, z/8)));
        return 1;
    }

    private int toOverworldCommand(CommandContext<FabricClientCommandSource> context) {
        var x = DoubleArgumentType.getDouble(context, "x");
        var z = DoubleArgumentType.getDouble(context, "z");
        
        context.getSource().sendFeedback(getResult(getCoords(x*8, z*8)));
        return 1;
    }

    private String getCoords(double x, double z) {
        var coords = new StringBuilder();
        coords.append("(");
        coords.append(x);
        coords.append(", ");
        coords.append(z);
        coords.append(")");

        return coords.toString();
    }

    private MutableText getResult(String message) {
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
