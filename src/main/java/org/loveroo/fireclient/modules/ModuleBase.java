package org.loveroo.fireclient.modules;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.loveroo.fireclient.data.JsonOption;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.screen.config.ModuleConfigScreen;
import org.loveroo.fireclient.screen.widgets.ToggleButtonWidget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public abstract class ModuleBase {

    private final ModuleData data;
    private boolean drawingOverwritten = false;

    protected int padding = 2;

    protected ModuleBase(ModuleData data) {
        this.data = data;
    }

    public void postLoad() { }

    public ModuleData getData() {
        return data;
    }

    public void update(MinecraftClient client) { }

    protected boolean canDraw() {
        if(FireClientside.getSetting(FireClientOption.SHOW_MODULES_DEBUG) == 0) {
            if(MinecraftClient.getInstance().getDebugHud().shouldShowDebugHud()) {
                return false;
            }
        }

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

    public void handleTransformation(int mouseState, OldTransform old, int mouseX, int mouseY, int oldMouseX, int oldMouseY, boolean snap) {
        if(!getData().isGuiElement()) {
            return;
        }

        switch(mouseState) {
            case 0 -> {
                var posX = old.posX + (mouseX - oldMouseX);
                var posY = old.posY + (mouseY - oldMouseY);

                if(snap) {
                    posX = Math.round(posX * (1/getData().getSnapX())) * getData().getSnapX();
                    posY = Math.round(posY * (1/getData().getSnapY())) * getData().getSnapY();
                }

                getData().setPosX((int)posX);
                getData().setPosY((int)posY);
            }

            case 1 -> {
                var newScale = Math.max(0.15, old.scale - (oldMouseX - mouseX)/getData().getWidth());

                if(snap) {
                    newScale = Math.round(newScale * (1/getData().getSnapScale())) * getData().getSnapScale();
                }

                getData().setScale(newScale);
            }
        }
    }

    public boolean isPointInside(int mouseX, int mouseY) {
        var points = getPoints();

        return !(mouseX < points[0] || mouseX > points[1] || mouseY < points[2] || mouseY > points[3]);
    }

    protected int[] getPoints() {
        var x1 = data.getPosX() - getPadding();
        var x2 = data.getPosX() + (data.getWidth() * data.getScale()) + getPadding() - 1;

        var y1 = data.getPosY() - getPadding();
        var y2 = data.getPosY() + (data.getHeight() * data.getScale()) + getPadding() - 1;

        return new int[] { (int)Math.round(x1), (int)Math.round(x2), (int)Math.round(y1), (int)Math.round(y2) };
    }

    protected double getPadding() {
        return padding;
    }

    protected void transform(MatrixStack matrix) {
        matrix.push();

        matrix.translate(data.getPosX(), data.getPosY(), 0.0f);
        matrix.scale((float)data.getScale(), (float)data.getScale(), 1.0f);
    }

    protected void endTransform(MatrixStack matrix) {
        matrix.pop();
    }

    public void loadJson(JSONObject json) throws Exception {
        if(getData().isGuiElement()) {
            var posX = json.optDouble("pos_x", getData().getDefaultPosX());
            var posY = json.optDouble("pos_y", getData().getDefaultPosY());
    
            if(posX > 1.0) {
                posX = getData().getDefaultPosX();
            }
    
            if(posY > 1.0) {
                posY = getData().getDefaultPosY();
            }
    
            getData().setRawPosX(posX);
            getData().setRawPosY(posY);
    
            getData().setScale(json.optDouble("scale", getData().getScale()));
            
            getData().setVisible(json.optBoolean("visible", getData().isVisible()));
        }
        
        getData().setEnabled(json.optBoolean("enabled", getData().isEnabled()));
        getData().setFavorited(json.optBoolean("favorited", getData().isFavorited()));

        var clazz = this.getClass();
        for(var field : clazz.getDeclaredFields()) {
            if(!field.isAnnotationPresent(JsonOption.class)) {
                continue;
            }

            field.setAccessible(true);
            
            var jsonOption = field.getAnnotation(JsonOption.class);

            var value = json.opt(jsonOption.name());
            if(value == null) {
                value = getValueFromField(field);
            }
            
            // super sketchy 😬
            if(field.getType().isEnum() && value instanceof Integer ordinal) {
                value = field.getType().getEnumConstants()[ordinal];
            }

            field.set(this, value);
        }
    }

    public JSONObject saveJson() throws Exception {
        var json = new JSONObject();

        if(getData().isGuiElement()) {
            json.put("pos_x", getData().getRawPosX());
            json.put("pos_y", getData().getRawPosY());
    
            json.put("scale", getData().getScale());
    
            json.put("visible", getData().isVisible());
            json.put("enabled", getData().isEnabled());
        }

        json.put("enabled", getData().isEnabled());
        json.put("favorited", getData().isFavorited());

        var clazz = this.getClass();
        for(var field : clazz.getDeclaredFields()) {
            if(!field.isAnnotationPresent(JsonOption.class)) {
                continue;
            }

            field.setAccessible(true);
            var jsonOption = field.getAnnotation(JsonOption.class);

            var value = getValueFromField(field);
            json.put(jsonOption.name(), value);
        }

        return json;
    }

    private Object getValueFromField(Field field) throws Exception {
        if(field.getType().isEnum()) {
            return ((Enum<?>)field.get(this)).ordinal();
        }

        return field.get(this);
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
        return new ToggleButtonWidget.ToggleButtonBuilder(Text.translatable("fireclient.module.generic.toggle_visible"))
            .getValue(getData()::isVisible)
            .setValue(getData()::setVisible)
            .position(x, y)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.generic.visibility_toggle")))
            .build();
    }

    public ButtonWidget getToggleEnableButton(int x, int y) {
        return new ToggleButtonWidget.ToggleButtonBuilder(Text.translatable("fireclient.module.generic.toggle_enabled"))
            .getValue(getData()::isEnabled)
            .setValue(getData()::setEnabled)
            .position(x, y)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.generic.enabled_toggle")))
            .build();
    }

    public void drawScreen(Screen base, DrawContext context, float delta) {
        drawScreenHeader(context, base.width/2, base.height/2 - 40);
    }

    protected void drawScreenHeader(DrawContext context, int x, int y) {
        var text = MinecraftClient.getInstance().textRenderer;

        var configText = Text.translatable("fireclient.module.generic.config_text", getData().getShownName());
        context.drawCenteredTextWithShadow(text, configText, x, y, 0xFFFFFFFF);
    }

    public void openScreen(Screen screen) { }

    protected void reloadScreen() {
        var client = MinecraftClient.getInstance();
        client.setScreen(new ModuleConfigScreen(this));
    }

    public void onFilesDropped(List<Path> paths) { }

    public void closeScreen(Screen screen) { }

    public boolean isDrawingOverwritten() {
        return drawingOverwritten;
    }

    public void setDrawingOverwritten(boolean drawingOverwritten) {
        this.drawingOverwritten = drawingOverwritten;
    }

    public OldTransform getTransform() {
        return new OldTransform(getData().getPosX(), getData().getPosY(), getData().getScale());
    }

    public record OldTransform(double posX, double posY, double scale) {}
}