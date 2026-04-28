package org.loveroo.fireclient.modules;

import java.util.ArrayList;
import java.util.List;

import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.DeltaTracker;
import net.minecraft.network.chat.Component;

public class FPSDisplayModule extends ModuleBase {

    private static final Color color1 = Color.fromRGB(0xD3FFBF);
    private static final Color color2 = Color.fromRGB(0xE8EBE6);

    public FPSDisplayModule() {
        super(new ModuleData("fps_display", "\uD83D\uDCCA", color1));

        getData().setHeight(8);
        getData().setWidth(40);

        getData().setDefaultPosX(2, 640);
        getData().setDefaultPosY(2, 360);

        var toggleBind = new Keybind("toggle_fps_display",
            Component.translatable("fireclient.keybind.generic.toggle.name"),
            Component.translatable("fireclient.keybind.generic.toggle_visibility.description", getData().getShownName()),
            true, null,
            () -> getData().setVisible(!getData().isVisible()), null);

        FireClientside.getKeybindManager().registerKeybind(toggleBind);
    }

    @Override
    public List<AbstractWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<AbstractWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("toggle_fps_display").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleVisibleButton(base.width/2 - 60, base.height/2 - 10));

        return widgets;
    }

    @Override
    public void draw(GuiGraphicsExtractor graphics, DeltaTracker ticks) {
        if(!canDraw()) {
            return;
        }

        transform(graphics.pose());

        var client = Minecraft.getInstance();
        var text = client.font;

        var msg = client.getFps() + " FPS";
        var fpsText = RooHelper.gradientText(msg, color1, color2);

        getData().setWidth(text.width(fpsText));

        graphics.text(text, fpsText, 0, 0, 0xFFFFFFFF, true);

        endTransform(graphics.pose());
    }
}
