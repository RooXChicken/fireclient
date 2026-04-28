package org.loveroo.fireclient.screen.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.mixin.AddWidgetAccessor;

import java.util.function.Consumer;

public class ColorPickerWidget extends AbstractWidget {

    private final int fillColor = 0xAA4D4D4D;
    private final int outlineColor = 0xFFFFFFFF;

    private final int[] currentLocation;

    private final EditBox colorField;
    private final ColorSlider rSlider;
    private final ColorSlider gSlider;
    private final ColorSlider bSlider;
    private final ColorSlider aSlider;

    private final Consumer<Integer> colorChanged;

    public ColorPickerWidget(int x, int y, int color, Consumer<Integer> colorChanged) {
        super(0, 0, 88, 100, Component.translatable("fireclient.widget.color_picker.name"));

        var client = Minecraft.getInstance();
        var inputColor = Color.fromARGB(color);

        this.colorChanged = colorChanged;

        rSlider = new ColorSlider("R", inputColor.r()/255.0, this::setColors);
        gSlider = new ColorSlider("G", inputColor.g()/255.0, this::setColors);
        bSlider = new ColorSlider("B", inputColor.b()/255.0, this::setColors);
        aSlider = new ColorSlider("A", inputColor.a()/255.0, this::setColors);

        colorField = new ColorTextField(client.font, x, y);
        colorField.setResponder(this::colorFieldChanged);
        colorField.setMaxLength(8);

        colorField.setValue(inputColor.toARGBHex());

        currentLocation = getDimensions(colorField.getX() + colorField.getWidth() - 1, colorField.getY());

        var sliderX = (currentLocation[0] + 4);

        rSlider.setPosition(sliderX, currentLocation[2] + 6);
        gSlider.setPosition(sliderX, currentLocation[2] + 28);
        bSlider.setPosition(sliderX, currentLocation[2] + 52);
        aSlider.setPosition(sliderX, currentLocation[2] + 76);
    }

    public void registerWidgets(Screen base) {
        var invoker = (AddWidgetAccessor)base;

        invoker.addDrawableChildInvoker(colorField);

        invoker.addSelectableChildInvoker(rSlider);
        invoker.addSelectableChildInvoker(gSlider);
        invoker.addSelectableChildInvoker(bSlider);
        invoker.addSelectableChildInvoker(aSlider);
    }

    private void colorFieldChanged(String text) {
        var colorText = text.toUpperCase();
        colorText += ("0".repeat(Math.max(0, 8 - text.length())));

        if(!colorText.matches("[0-9a-fA-F]+")) {
            colorText = "FFFFFFFF";
        }

        var colorValue = (int)Long.parseLong(colorText, 16);
        colorChanged.accept(colorValue);

        var color = Color.fromARGB(colorValue);
        rSlider.setColor(color.r());
        gSlider.setColor(color.g());
        bSlider.setColor(color.b());
        aSlider.setColor(color.a());
    }

    private void setColors() {
        colorField.setValue(getColor().toARGBHex());
    }

    private Color getColor() {
        var r = rSlider.color;
        var g = gSlider.color;
        var b = bSlider.color;
        var a = aSlider.color;

        return new Color(r, g, b, a);
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        var visible = (colorField.isFocused() || sliderFocused());
        rSlider.active = visible;
        gSlider.active = visible;
        bSlider.active = visible;
        aSlider.active = visible;

        if(!visible) {
            return;
        }

        graphics.fill(currentLocation[0], currentLocation[2], currentLocation[1], currentLocation[3], fillColor);

        graphics.horizontalLine(currentLocation[0], currentLocation[1], currentLocation[2], outlineColor);
        graphics.horizontalLine(currentLocation[0], currentLocation[1], currentLocation[3], outlineColor);

        graphics.verticalLine(currentLocation[0], currentLocation[2], currentLocation[3], outlineColor);
        graphics.verticalLine(currentLocation[1], currentLocation[2], currentLocation[3], outlineColor);

        rSlider.extractRenderState(graphics, mouseX, mouseY, delta);
        gSlider.extractRenderState(graphics, mouseX, mouseY, delta);
        bSlider.extractRenderState(graphics, mouseX, mouseY, delta);
        aSlider.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    private boolean sliderFocused() {
        return (rSlider.isFocused() || gSlider.isFocused() || bSlider.isFocused() || aSlider.isFocused());
    }

    private int[] getDimensions(int x, int y) {
        return new int[] { x, x + width, y, y + height };
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput builder) {

    }

    protected static class ColorSlider extends AbstractSliderButton {

        private final String colorText;
        private int color = 0;

        private final Runnable onChanged;

        public ColorSlider(String colorText, double value, Runnable onChanged) {
            super(0, 0, 80, 18, Component.nullToEmpty(""), value);

            this.colorText = colorText + ": ";
            this.visible = true;

            applyValue();
            updateMessage();

            this.onChanged = onChanged;
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.nullToEmpty(colorText + color));
        }

        @Override
        protected void applyValue() {
            color = (int)Math.round(value * 255);

            if(onChanged != null) {
                onChanged.run();
            }
        }

        @Override
        public void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
            super.extractWidgetRenderState(graphics, mouseX, mouseY, delta);
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
            this.value = color / 255.0;

            updateMessage();
        }
    }

    protected static class ColorTextField extends EditBox {

        public ColorTextField(Font textRenderer, int x, int y) {
            super(textRenderer, 64, 15, Component.nullToEmpty(""));

            setPosition(x, y);
        }
    }
}
