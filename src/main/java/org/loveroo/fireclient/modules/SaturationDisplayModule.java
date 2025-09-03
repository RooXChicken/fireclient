package org.loveroo.fireclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
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
        getData().setDefaultPosY(86, 360);

        getData().setVisible(false);

        var toggleBind = new Keybind("toggle_saturation_display",
                Text.translatable("fireclient.keybind.generic.toggle.name"),
                Text.translatable("fireclient.keybind.generic.toggle_visibility.description", getData().getShownName()),
                true, null,
                () -> getData().setVisible(!getData().isVisible()), null);

        FireClientside.getKeybindManager().registerKeybind(toggleBind);
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("toggle_saturation_display").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleVisibleButton(base.width/2 - 60, base.height/2 - 10));

        return widgets;
    }

    @Override
    public void draw(DrawContext context, RenderTickCounter ticks) {
        if(!canDraw()) {
            return;
        }

        transform(context.getMatrices());

        var client = MinecraftClient.getInstance();
        var text = client.textRenderer;

        var saturation = (client.player != null) ? (client.player.getHungerManager().getSaturationLevel()) : 20.0;
        var msg = String.format("🍖 %.2f", saturation);
        var saturationtext = RooHelper.gradientText(msg, color1, color2);

        getData().setWidth(text.getWidth(saturationtext));

        context.drawText(text, saturationtext, 0, 0, 0xFFFFFFFF, true);

        endTransform(context.getMatrices());
    }
}
