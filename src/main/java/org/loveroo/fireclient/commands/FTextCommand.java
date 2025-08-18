package org.loveroo.fireclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.*;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;

public class FTextCommand {

    private static final Text fTextHeader = RooHelper.gradientText("[FTEXT]", FireClientside.mainColor1, FireClientside.mainColor2);

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        var smallTextSub = ClientCommandManager.literal("to_small")
                .then(ClientCommandManager.argument("text", StringArgumentType.greedyString())
                        .executes(context -> smallTextSub(context))
        );

        dispatcher.register(ClientCommandManager.literal("ftext")
                .then(smallTextSub)
        );
    }

    private MutableText getResult(String message) {
        var messageText = MutableText.of(new PlainTextContent.Literal(" " + message)).setStyle(Style.EMPTY).withColor(0xFFFFFFFF);
        return fTextHeader.copy().append(messageText);
    }

    private int smallTextSub(CommandContext<FabricClientCommandSource> context) {
        var text = StringArgumentType.getString(context, "text").toLowerCase();
        var small = RooHelper.toSmallText(text);

        var click = new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, small);
        var hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of(small));

        var smallText = MutableText.of(new PlainTextContent.Literal(small)).setStyle(Style.EMPTY.withClickEvent(click).withHoverEvent(hover));
        var feedback = getResult("").copy().append(smallText);
        context.getSource().sendFeedback(feedback);

        return 1;
    }
}
