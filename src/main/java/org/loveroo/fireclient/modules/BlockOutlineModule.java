package org.loveroo.fireclient.modules;

import java.util.ArrayList;
import java.util.List;

import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.JsonOption;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.screen.widgets.ColorPickerWidget;
import org.loveroo.fireclient.screen.widgets.ToggleButtonWidget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.network.chat.Component;

public class BlockOutlineModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0x6E6E6E);

    @JsonOption(name = "thick")
    private boolean thick = false;

    @JsonOption(name = "outline")
    private int outline = 0x66000000;
    private int defaultOutline = 0x66000000;

    private float rot = 180.0f;
    private ProjectionMatrixBuffer proj = null;

    private boolean screenOpen = false;

    public BlockOutlineModule() {
        super(new ModuleData("block_outline", "☐", color));

        getData().setGuiElement(false);
    }

    @Override
    public List<AbstractWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<AbstractWidget>();

        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 - 10));

        widgets.add(new ToggleButtonWidget.ToggleButtonBuilder(Component.translatable("fireclient.module.block_outline.thick_outline.name"))
            .getValue(() -> { return thick; })
            .setValue((value) -> { thick = value; })
            .position(base.width/2 - 60,base.height / 2 + 20)
            .tooltip(Tooltip.create(Component.translatable("fireclient.module.block_outline.thick_outline.tooltip")))
            .build());

        var colorPicker = new ColorPickerWidget(base.width/2 - 36, base.height/2 + 50, outline, (color) -> { outline = color; });
        widgets.add(colorPicker);

        colorPicker.registerWidgets(base);

        return widgets;
    }

    @Override
    public void openScreen(Screen screen) {
        super.openScreen(screen);
        screenOpen = true;
    }

    @Override
    public void closeScreen(Screen screen) {
        FireClientside.saveConfig();
        screenOpen = false;
    }

    @Override
    public void drawScreen(Screen base, GuiGraphicsExtractor context, float delta) {
        drawScreenHeader(context, base.width/2, base.height/2 - 80);
    }

    public int getDefaultOutline() {
        return defaultOutline;
    }

    public int getOutline() {
        return outline;
    }

    public boolean isScreenOpen() {
        return screenOpen;
    }

    public boolean isThick() {
        return thick;
    }
}
