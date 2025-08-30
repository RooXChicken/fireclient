package org.loveroo.fireclient.modules;

import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.shape.*;
import org.joml.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.JsonOption;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.screen.widgets.ColorPickerWidget;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

public class BlockOutlineModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0x6E6E6E);

    @JsonOption(name = "thick")
    private boolean thick = false;

    @JsonOption(name = "outline")
    private int outline = 0x66000000;
    private int defaultOutline = 0x66000000;

    private float rot = 180.0f;
    private ProjectionMatrix2 proj = null;

    private boolean screenOpen = false;

    public BlockOutlineModule() {
        super(new ModuleData("block_outline", "☐", color));

        getData().setGuiElement(false);
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 - 10));

        widgets.add(ButtonWidget.builder(getToggleText(Text.translatable("fireclient.module.block_outline.thick_outline.name"), thick), this::thickButtonPressed)
                .dimensions(base.width/2 - 60,base.height / 2 + 20, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.block_outline.thick_outline.tooltip")))
                .build());

        var colorPicker = new ColorPickerWidget(base.width/2 - 36, base.height/2 + 50, outline, (color) -> { outline = color; });
        widgets.add(colorPicker);

        colorPicker.registerWidgets(base);

        return widgets;
    }

    private void thickButtonPressed(ButtonWidget button) {
        thick = !thick;
        button.setMessage(getToggleText(Text.translatable("fireclient.module.block_outline.thick_outline.name"), thick));
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
    public void drawScreen(Screen base, DrawContext context, float delta) {
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
