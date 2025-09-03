package org.loveroo.fireclient.modules;

import java.util.ArrayList;
import java.util.List;

import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

public class TPSModule extends ModuleBase {

    private static final Color color1 = Color.fromRGB(0xDCB0F7);
    private static final Color color2 = Color.fromRGB(0x9F4ECF);

    private double oldSystemTime = -1;

    private double tps = 0.0;

    public TPSModule() {
        super(new ModuleData("tps_display", "⏳", color1));

        getData().setHeight(8);
        getData().setWidth(40);

        getData().setDefaultPosX(2, 640);
        getData().setDefaultPosY(74, 360);

        var toggleBind = new Keybind("toggle_tps_display",
            Text.translatable("fireclient.keybind.generic.toggle.name"),
            Text.translatable("fireclient.keybind.generic.toggle_visibility.description", getData().getShownName()),
            true, null,
            () -> getData().setVisible(!getData().isVisible()), null);

        oldSystemTime = System.currentTimeMillis();

        FireClientside.getKeybindManager().registerKeybind(toggleBind);
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("toggle_tps_display").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleVisibleButton(base.width/2 - 60, base.height/2 - 10));

        return widgets;
    }

    @Override
    public void draw(DrawContext context, RenderTickCounter ticks) {
        if(!canDraw()) {
            return;
        }

        var newSystemTime = System.currentTimeMillis();
        var calcTps = calcTps(newSystemTime, oldSystemTime);
        
        transform(context.getMatrices());

        var client = MinecraftClient.getInstance();
        var text = client.textRenderer;

        var msg = "⏳ " + String.format("%.2f", (calcTps < tps) ? calcTps : tps);
        var tpsText = RooHelper.gradientText(msg, color1, color2);

        getData().setWidth(text.getWidth(tpsText));

        context.drawText(text, tpsText, 0, 0, 0xFFFFFFFF, true);

        endTransform(context.getMatrices());
    }

    public void setTps() {
        tps = calcTps(System.currentTimeMillis(), oldSystemTime);
        oldSystemTime = System.currentTimeMillis();
    }

    private double calcTps(double currentTime, double oldTime) {
        var timeDiff = 1000.0 / Math.max(1, currentTime - oldTime);
        return timeDiff * 20.0;
    }
}
