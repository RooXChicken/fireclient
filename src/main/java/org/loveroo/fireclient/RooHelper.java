package org.loveroo.fireclient;

import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.loveroo.fireclient.data.Color;

import java.util.ArrayList;
import java.util.List;

public class RooHelper {

    public static MutableText gradientText(String msg, Color color1, Color color2) {
        var text = MutableText.of(new PlainTextContent.Literal(""));

        for(var i = 0; i < msg.length(); i++) {
            var progress = ((i+0.0) / msg.length());
            var style = Style.EMPTY.withColor(color1.blend(color2, progress).toInt());

            text.append(MutableText.of(new PlainTextContent.Literal(msg.charAt(i) + "")).setStyle(style));
        }

        return text;
    }

//    public static String colorToHex
}
