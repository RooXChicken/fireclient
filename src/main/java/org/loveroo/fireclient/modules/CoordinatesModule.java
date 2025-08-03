package org.loveroo.fireclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.dimension.DimensionType;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;

import java.util.ArrayList;
import java.util.List;

public class CoordinatesModule extends ModuleBase {

    private final Color xColor1 = new Color(247, 33, 33, 255);
    private final Color xColor2 = new Color(176, 18, 18, 255);

    private final Color yColor1 = new Color(47, 216, 39, 255);
    private final Color yColor2 = new Color(28, 158, 21, 255);

    private final Color zColor1 = new Color(76, 194, 224, 255);
    private final Color zColor2 = new Color(40, 131, 180, 255);

    private final Color netherColor1 = new Color(199, 57, 202, 255);
    private final Color netherColor2 = new Color(152, 33, 149, 255);

    private boolean showOther = false;

    public CoordinatesModule() {
        super(new ModuleData("Coordinates", "coordinates"));

        getData().setHeight(8);

        getData().setPosX(4);
        getData().setPosY(6);
    }

    @Override
    public void update(MinecraftClient client) {
        if(showOther) {
            getData().setHeight(16);
        }
        else {
            getData().setHeight(8);
        }
    }

    @Override
    public void draw(DrawContext context, RenderTickCounter ticks) {
        if(!getData().isVisible()) {
            return;
        }

        transform(context.getMatrices());

        if(!showOther) {
            drawNormal(context);
        }
        else {
            drawWithOther(context);
        }

        endTransform(context.getMatrices());
    }

    private void drawNormal(DrawContext context) {
        var client = MinecraftClient.getInstance();
        var text = client.textRenderer;

        var xText = String.format("X: %.2f ", client.player.getX());
        var yText = String.format("Y: %.2f ", client.player.getY());
        var zText = String.format("Z: %.2f", client.player.getZ());

        var x = RooHelper.gradientText(xText, xColor1, xColor2);
        var y = RooHelper.gradientText(yText, yColor1, yColor2);
        var z = RooHelper.gradientText(zText, zColor1, zColor2);

        var coordsText = x.append(y).append(z);

        getData().setWidth(text.getWidth(xText + yText + zText));
        context.drawText(text, coordsText, 0, 0, 0xFFFFFFFF, true);
    }

    private void drawWithOther(DrawContext context) {
        var client = MinecraftClient.getInstance();
        var dimension = client.player.getWorld().getDimensionEntry().getIdAsString();

        if(!dimension.equals("minecraft:overworld") && !dimension.equals("minecraft:the_nether")) {
            drawNormal(context);
            return;
        }

        var text = client.textRenderer;

        var xPos = client.player.getX();
        var yPos = client.player.getY();
        var zPos = client.player.getZ();

        var xText = String.format("X: %.2f ", xPos);
        var yText = String.format("Y: %.2f ", yPos);
        var zText = String.format("Z: %.2f", zPos);

        switch(dimension) {
            case "minecraft:overworld" -> {
                xPos /= 8.0;
                zPos /= 8.0;
            }

            case "minecraft:the_nether" -> {
                xPos *= 8.0;
                zPos *= 8.0;
            }
        }

        var otherXText = String.format("X: %.2f ", xPos);
        var otherYText = String.format("Y: %.2f ", yPos);
        var otherZText = String.format("Z: %.2f", zPos);

        MutableText normal;
        MutableText other;

        var finalNormal = xText + yText + zText;
        var finalOther = otherXText + otherYText + otherZText;

        if(dimension.equals("minecraft:overworld")) {
            normal = RooHelper.gradientText(finalNormal, yColor1, yColor2);
            other = RooHelper.gradientText(finalOther, netherColor1, netherColor2);
        }
        else {
            normal = RooHelper.gradientText(finalNormal, netherColor1, netherColor2);
            other = RooHelper.gradientText(finalOther, yColor1, yColor2);
        }

        getData().setWidth(Math.max(text.getWidth(finalNormal), text.getWidth(finalOther)));

        context.drawText(text, normal, 0, 0, 0xFFFFFFFF, true);
        context.drawText(text, other, 0, 10, 0xFFFFFFFF, true);
    }

    @Override
    public JSONObject saveJson() throws JSONException {
        var json = super.saveJson();
        json.put("show_other", showOther);

        return json;
    }

    @Override
    public void loadJson(JSONObject json) throws JSONException {
        super.loadJson(json);
        showOther = json.optBoolean("show_other", false);
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(getToggleVisibleButton(base.width/2 - 60, base.height/2 - 20));
        widgets.add(ButtonWidget.builder(Text.of("Other Dimension: " + showOther), this::showOtherButtonPressed)
                .dimensions(base.width/2 - 60,base.height / 2 + 10, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.coordinates.other_dimension")))
                .build());

        return widgets;
    }

    public void showOtherButtonPressed(ButtonWidget button) {
        showOther = !showOther;
        button.setMessage(Text.of("Other Dimension: " + showOther));
    }
}
