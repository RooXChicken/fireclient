package org.loveroo.fireclient.modules;

import java.util.ArrayList;
import java.util.List;

import org.joml.Quaternionf;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.JsonOption;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.screen.widgets.ColorPickerWidget;
import org.loveroo.fireclient.screen.widgets.ToggleButtonWidget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.shape.VoxelShapes;

public class BlockOutlineModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0x6E6E6E);

    @JsonOption(name = "thick")
    private boolean thick = false;

    @JsonOption(name = "outline")
    private int outline = 0x66000000;
    private int defaultOutline = 0x66000000;

    private float rot = 180.0f;

    public BlockOutlineModule() {
        super(new ModuleData("block_outline", "☐", color));

        getData().setGuiElement(false);
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 - 10));

        widgets.add(new ToggleButtonWidget.ToggleButtonBuilder(Text.translatable("fireclient.module.block_outline.thick_outline.name"))
            .getValue(() -> { return thick; })
            .setValue((value) -> { thick = value; })
            .position(base.width/2 - 60,base.height / 2 + 20)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.block_outline.thick_outline.tooltip")))
            .build());

        var colorPicker = new ColorPickerWidget(base.width/2 - 36, base.height/2 + 50, outline, (color) -> { outline = color; });
        widgets.add(colorPicker);

        colorPicker.registerWidgets(base);

        return widgets;
    }

    @Override
    public void closeScreen(Screen screen) {
        FireClientside.saveConfig();
    }

    @Override
    public void drawScreen(Screen base, DrawContext context, float delta) {
        drawScreenHeader(context, base.width/2, base.height/2 - 80);

        var shape = VoxelShapes.cuboid(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5);
        final var scale = 25.0f;

        var matrix = context.getMatrices();
        matrix.push();

        rot -= delta*2.0f;

        while(rot < 180) {
            rot += 360;
        }

        matrix.translate(base.width/2.0f, base.height/2.0f - 40, 0.0f);
        matrix.scale(scale, scale, scale);

        matrix.multiply(new Quaternionf().rotateXYZ(0.130f, (float)Math.toRadians(rot), 0.0f));

        var color = (getData().isEnabled()) ? getOutline() : defaultOutline;

        // var layer = (getData().isEnabled() && thick) ? RenderLayer.getSecondaryBlockOutline() : RenderLayer.getLines();
        // context.draw(vertex -> VertexRendering.drawOutline(matrix, vertex.getBuffer(layer), shape, 0, 0, 0, color));
        matrix.pop();
    }

    public int getOutline() {
        return outline;
    }

    public boolean isThick() {
        return thick;
    }
}
