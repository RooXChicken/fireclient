package org.loveroo.fireclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.PlainTextContents;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;

public class FTextCommand {

    private static final Component fTextHeader = RooHelper.gradientText("[FTEXT]", FireClientside.mainColor1, FireClientside.mainColor2);

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
        var smallTextSub = ClientCommands.literal("to_small")
                .then(ClientCommands.argument("text", StringArgumentType.greedyString())
                        .executes(context -> smallTextCommand(context))
        );

        dispatcher.register(ClientCommands.literal("ftext")
                .then(smallTextSub)
        );
    }

    private MutableComponent getResult(String message) {
        var messageText = MutableComponent.create(new PlainTextContents.LiteralContents(" " + message)).setStyle(Style.EMPTY).withColor(0xFFFFFFFF);
        return fTextHeader.copy().append(messageText);
    }

    private int smallTextCommand(CommandContext<FabricClientCommandSource> context) {
        var text = StringArgumentType.getString(context, "text").toLowerCase();
        var small = toSmallText(text);

        var click = new ClickEvent.CopyToClipboard(small);
        var hover = new HoverEvent.ShowText(Component.nullToEmpty(small));

        var smallText = MutableComponent.create(new PlainTextContents.LiteralContents(small)).setStyle(Style.EMPTY.withClickEvent(click).withHoverEvent(hover));
        var feedback = getResult("").copy().append(smallText);
        context.getSource().sendFeedback(feedback);

        return 1;
    }

    private String toSmallText(String text) {
        var smallText = new StringBuilder();
        var array = text.toCharArray();

        for(var character : array) {
            smallText.append(getSmallCharacter(character));
        }

        return smallText.toString();
    }

    private String getSmallCharacter(char character) {
        String small = character + "";

        switch(character) {
            case 'a' -> small = "ᴀ";
            case 'b' -> small = "ʙ";
            case 'c' -> small = "ᴄ";
            case 'd' -> small = "ᴅ";
            case 'e' -> small = "ᴇ";
            case 'f' -> small = "ꜰ";
            case 'g' -> small = "ɢ";
            case 'h' -> small = "ʜ";
            case 'i' -> small = "ɪ";
            case 'j' -> small = "ᴊ";
            case 'k' -> small = "ᴋ";
            case 'l' -> small = "ʟ";
            case 'm' -> small = "ᴍ";
            case 'n' -> small = "ɴ";
            case 'o' -> small = "ᴏ";
            case 'p' -> small = "ᴘ";
            case 'q' -> small = "ǫ";
            case 'r' -> small = "ʀ";
            case 's' -> small = "ѕ";
            case 't' -> small = "ᴛ";
            case 'u' -> small = "ᴜ";
            case 'v' -> small = "ᴠ";
            case 'w' -> small = "ᴡ";
            case 'x' -> small = "х";
            case 'y' -> small = "ʏ";
            case 'z' -> small = "ᴢ";
            case '1' -> small = "₁";
            case '2' -> small = "₂";
            case '3' -> small = "₃";
            case '4' -> small = "₄";
            case '5' -> small = "₅";
            case '6' -> small = "₆";
            case '7' -> small = "₇";
            case '8' -> small = "₈";
            case '9' -> small = "₉";
            case '0' -> small = "₀";
        }

        return small;
    }
}
