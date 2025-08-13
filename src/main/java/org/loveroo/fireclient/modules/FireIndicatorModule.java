package org.loveroo.fireclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.*;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;

import java.util.ArrayList;
import java.util.List;

public class FireIndicatorModule extends ModuleBase {

    private final MutableText emoji = MutableText.of(new PlainTextContent.Literal("\uD83D\uDD25")).setStyle(Style.EMPTY.withColor(0xFA765C));

    public FireIndicatorModule() {
        super(new ModuleData("fire_indicator", "\uD83D\uDD25 Fire Indicator", "Shows an indicator when you're on fire"));
        getData().setShownName(generateDisplayName(0xF7A22A));

        getData().setWidth(6);
        getData().setHeight(6);

        getData().setPosX(2);
        getData().setPosY(26);

        FireClientside.getKeybindManager().registerKeybind(
                new Keybind("toggle_fire_indicator", Text.of("Toggle"), Text.of("Toggle ").copy().append(getData().getShownName()).append("'s visibility"), true, null,
                        () -> getData().setVisible(!getData().isVisible()), null)
        );
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("toggle_fire_indicator").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleVisibleButton(base.width/2 - 60, base.height/2 - 10));

        return widgets;
    }

    @Override
    public void draw(DrawContext context, RenderTickCounter ticks) {
        if(!getData().isVisible()) {
            return;
        }

        var client = MinecraftClient.getInstance();
        if(client.player == null || !client.player.isOnFire()) {
            return;
        }

        transform(context.getMatrices());

        var text = client.textRenderer;

        context.drawText(text, emoji, 0, 0, 0xFFFFFFFF, true);

        endTransform(context.getMatrices());
    }
}
