package org.loveroo.fireclient.modules;

import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.fabricmc.fabric.api.client.rendering.v1.LayeredDrawerWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.screen.config.FireClientSettingsScreen;
import org.loveroo.fireclient.screen.config.MainConfigScreen;
import org.loveroo.fireclient.screen.config.ModuleConfigScreen;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class ModuleBase implements HudLayerRegistrationCallback {

    private final ModuleData data;
    private boolean drawingOverwritten = false;

    protected int padding = 2;

    protected ModuleBase(ModuleData data) {
        this.data = data;

        if(!this.data.isGuiElement()) {
            return;
        }

        HudLayerRegistrationCallback.EVENT.register(this);
    }

    public void postLoad() { }

    public ModuleData getData() {
        return data;
    }

    public MutableText generateDisplayName(int emojiColor) {
        var emoji = getData().getName().substring(0, getData().getName().indexOf(" "));
        var name = getData().getName().substring(getData().getName().indexOf(" "));

        var nameText = MutableText.of(new PlainTextContent.Literal(name)).setStyle(Style.EMPTY.withColor(0xFFFFFF));
        return MutableText.of(new PlainTextContent.Literal(emoji)).setStyle(Style.EMPTY.withColor(emojiColor)).append(nameText);
    }

    @Override
    public void register(LayeredDrawerWrapper layeredDrawer) {
        layeredDrawer.attachLayerAfter(IdentifiedLayer.HOTBAR_AND_BARS, Identifier.of(FireClient.MOD_ID, getData().getId()), this::draw);
    }

    public void update(MinecraftClient client) { }

    protected boolean canDraw() {
        return !drawingOverwritten && getData().isVisible();
    }

    public void draw(DrawContext context, RenderTickCounter ticks) { }

    public void drawOutline(DrawContext context) {
        if(!getData().isGuiElement()) {
            return;
        }

        var points = getPoints();

        context.drawHorizontalLine(points[0], points[1], points[2], 0xFFFFFFFF);
        context.drawHorizontalLine(points[0], points[1], points[3], 0xFFFFFFFF);

        context.drawVerticalLine(points[0], points[2], points[3], 0xFFFFFFFF);
        context.drawVerticalLine(points[1], points[2], points[3], 0xFFFFFFFF);
    }

    public void handleTransformation(int mouseState, int mouseX, int mouseY, int oldMouseX, int oldMouseY) {
        if(!getData().isGuiElement()) {
            return;
        }

        if(mouseState == 0) {
            getData().setPosX(getData().getPosX() + (mouseX - oldMouseX));
            getData().setPosY(getData().getPosY() + (mouseY - oldMouseY));
        }
    }

    public boolean isPointInside(int mouseX, int mouseY) {
        var points = getPoints();

        return !(mouseX < points[0] || mouseX > points[1] || mouseY < points[2] || mouseY > points[3]);
    }

    protected int[] getPoints() {
        var x1 = (int)(data.getPosX()) - padding;
        var x2 = (int)(data.getPosX() + (data.getWidth() * data.getScale())) + padding;

        var y1 = (int)(data.getPosY()) - padding;
        var y2 = (int)(data.getPosY() + (data.getHeight() * data.getScale())) + padding;

        return new int[] { x1, x2, y1, y2 };
    }

    protected void transform(MatrixStack matrix) {
        matrix.push();

        matrix.scale((float)data.getScale(), (float)data.getScale(), 0.0f);
        matrix.translate(data.getPosX() * 1/data.getScale(), data.getPosY() * 1/data.getScale(), 0.0f);
    }

    protected void endTransform(MatrixStack matrix) {
        matrix.pop();
    }

    public void loadJson(JSONObject json) throws JSONException {
        getData().setPosX(json.optDouble("pos_x", getData().getPosX()));
        getData().setPosY(json.optDouble("pos_y", getData().getPosY()));
        getData().setScale(json.optDouble("scale", getData().getScale()));
        getData().setVisible(json.optBoolean("visible", getData().isVisible()));
        getData().setEnabled(json.optBoolean("enabled", getData().isEnabled()));
    }

    public JSONObject saveJson() throws JSONException {
        var json = new JSONObject();

        json.put("pos_x", getData().getPosX());
        json.put("pos_y", getData().getPosY());
        json.put("scale", getData().getScale());
        json.put("visible", getData().isVisible());
        json.put("enabled", getData().isEnabled());

        return json;
    }

    public void moduleConfigPressed(ButtonWidget button) {
        var client = MinecraftClient.getInstance();
        client.setScreen(new ModuleConfigScreen(this));
    }

    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 - 10));

        return widgets;
    }

    public ButtonWidget getToggleVisibleButton(int x, int y) {
        return ButtonWidget.builder(getToggleText(Text.of("Visible"), getData().isVisible()), this::visibleButtonPressed)
                .dimensions(x, y, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.generic.visibility_toggle")))
                .build();
    }

    public ButtonWidget getToggleEnableButton(int x, int y) {
        return ButtonWidget.builder(getToggleText(Text.of("Enabled"), getData().isEnabled()), this::enableButtonPressed)
                .dimensions(x, y, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.generic.enabled_toggle")))
                .build();
    }

    public void enableButtonPressed(ButtonWidget button) {
        getData().setEnabled(!getData().isEnabled());
        button.setMessage(getToggleText(Text.of("Enabled"), getData().isEnabled()));
    }

    public void visibleButtonPressed(ButtonWidget button) {
        getData().setVisible(!getData().isVisible());
        button.setMessage(getToggleText(Text.of("Visible"), getData().isVisible()));
    }

    public MutableText getToggleText(Text message, boolean value) {
        return ((value) ? FireClientSettingsScreen.defaultTrueText : FireClientSettingsScreen.defaultFalseText).copy().append(message);
    }

    public void drawScreen(Screen base, DrawContext context) {
        var text = MinecraftClient.getInstance().textRenderer;

        var configText = getData().getShownName().copy().append(MutableText.of(new PlainTextContent.Literal(" Configuration")).setStyle(Style.EMPTY.withColor(0xFFFFFF)));
        context.drawCenteredTextWithShadow(text, configText, base.width/2, base.height/2 - 40, 0xFFFFFFFF);
    }

    public void openScreen(Screen screen) { }

    public void onFilesDropped(List<Path> paths) { }

    public void closeScreen(Screen screen) { }

    public boolean isDrawingOverwritten() {
        return drawingOverwritten;
    }

    public void setDrawingOverwritten(boolean drawingOverwritten) {
        this.drawingOverwritten = drawingOverwritten;
    }
}