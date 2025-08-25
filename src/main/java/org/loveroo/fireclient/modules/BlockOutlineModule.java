package org.loveroo.fireclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.*;
import net.minecraft.text.Text;
import net.minecraft.util.shape.*;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Quaternionf;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.screen.widgets.ColorPickerWidget;

import java.util.ArrayList;
import java.util.List;

public class BlockOutlineModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0x6E6E6E);

    private boolean thick = false;

    private int outline = 0x66000000;
    private int defaultOutline = 0x66000000;

    private float rot = 180.0f;

    public BlockOutlineModule() {
        super(new ModuleData("block_outline", "☐", color));

        getData().setGuiElement(false);
    }

    @Override
    public void loadJson(JSONObject json) throws JSONException {
        getData().setEnabled(json.optBoolean("enabled", getData().isEnabled()));

        // because i was silly and forgot to rename the variable
        outline = json.optInt("outline", defaultOutline);
        if(json.has("outline_color")) {
            outline = (int)Long.parseLong(json.getString("outline_color"), 16);
        }

        thick = json.optBoolean("thick_outline", thick);
    }

    @Override
    public JSONObject saveJson() throws JSONException {
        var json = new JSONObject();

        json.put("enabled", getData().isEnabled());
        json.put("outline", outline);
        json.put("thick_outline", thick);

        return json;
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
    public void closeScreen(Screen screen) {
        FireClientside.saveConfig();
    }

    @Override
    public void drawScreen(Screen base, DrawContext context, float delta) {
        drawScreenHeader(context, base.width/2, base.height/2 - 80);

        var shape = VoxelShapes.cuboid(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5);
        final var scale = 25.0f;

        var matrix = context.getMatrices();
        matrix.pushMatrix();

        rot -= delta*2.0f;

        while(rot < 180) {
            rot += 360;
        }

        matrix.translate(base.width/2.0f, base.height/2.0f - 40);
        matrix.scale(scale, scale);

//        var rotQuat = new Quaternionf().rotateXYZ(0.130f, (float)Math.toRadians(rot), 0.0f);
//        matrix.mul(new Matrix3x2f());

        var color = (getData().isEnabled()) ? getOutline() : defaultOutline;

//        var layer = (getData().isEnabled() && thick) ? RenderLayer.getSecondaryBlockOutline() : RenderLayer.getLines();
//        context.draw(vertex -> VertexRendering.drawOutline(matrix, vertex.getBuffer(layer), shape, 0, 0, 0, color));
        matrix.popMatrix();
    }

    public int getOutline() {
        return outline;
    }

    public boolean isThick() {
        return thick;
    }
}
