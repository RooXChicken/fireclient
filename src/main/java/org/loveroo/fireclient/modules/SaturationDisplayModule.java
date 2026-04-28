package org.loveroo.fireclient.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.DeltaTracker;
import net.minecraft.network.chat.Component;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;

import java.util.ArrayList;
import java.util.List;

public class SaturationDisplayModule extends ModuleBase {

    private static final Color color1 = Color.fromRGB(0xD6B569);
    private static final Color color2 = Color.fromRGB(0xB8B435);

    public SaturationDisplayModule() {
        super(new ModuleData("saturation_display", "🍖", color1));

        getData().setHeight(8);
        getData().setWidth(30);

        getData().setDefaultPosX(2, 640);
        getData().setDefaultPosY(79, 360);

        getData().setVisible(false);

        var toggleBind = new Keybind("toggle_saturation_display",
                Component.translatable("fireclient.keybind.generic.toggle.name"),
                Component.translatable("fireclient.keybind.generic.toggle_visibility.description", getData().getShownName()),
                true, null,
                () -> getData().setVisible(!getData().isVisible()), null);

        FireClientside.getKeybindManager().registerKeybind(toggleBind);
    }

    @Override
    public List<AbstractWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<AbstractWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("toggle_saturation_display").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleVisibleButton(base.width/2 - 60, base.height/2 - 10));

        return widgets;
    }

    @Override
    public void draw(GuiGraphicsExtractor graphics, DeltaTracker ticks) {
        if(!canDraw()) {
            return;
        }

        var client = Minecraft.getInstance();
        if(client.player == null) {
            return;
        }

        transform(graphics.pose());

        var text = client.font;

        var saturation = client.player.getFoodData().getSaturationLevel();
        var msg = String.format("🍖 %.2f", saturation);
        var saturationtext = RooHelper.gradientText(msg, color1, color2);

        getData().setWidth(text.width(saturationtext));

        graphics.text(text, saturationtext, 0, 0, 0xFFFFFFFF, true);

        endTransform(graphics.pose());
    }
}
