package org.loveroo.fireclient.screen.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.mixin.AddWidgetAccessor;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ColorPickerWidget extends ClickableWidget {

    private final int fillColor = 0xAA4D4D4D;
    private final int outlineColor = 0xFFFFFFFF;

    private final int[] currentLocation;

    private final TextFieldWidget colorField;
    private final ColorSlider rSlider;
    private final ColorSlider gSlider;
    private final ColorSlider bSlider;
    private final ColorSlider aSlider;

    private final Consumer<Integer> colorChanged;

    public ColorPickerWidget(int x, int y, int color, Consumer<Integer> colorChanged) {
        super(0, 0, 88, 100, Text.of("Color Picker"));

        var client = MinecraftClient.getInstance();
        var inputColor = Color.fromARGB(color);

        this.colorChanged = colorChanged;

        rSlider = new ColorSlider("R", inputColor.r()/255.0, this::setColors);
        gSlider = new ColorSlider("G", inputColor.g()/255.0, this::setColors);
        bSlider = new ColorSlider("B", inputColor.b()/255.0, this::setColors);
        aSlider = new ColorSlider("A", inputColor.a()/255.0, this::setColors);

        colorField = new ColorTextField(client.textRenderer, x, y);
        colorField.setChangedListener(this::colorFieldChanged);
        colorField.setMaxLength(8);

        colorField.setText(inputColor.toARGBHex());

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
        colorField.setText(getColor().toARGBHex());
    }

    private Color getColor() {
        var r = rSlider.color;
        var g = gSlider.color;
        var b = bSlider.color;
        var a = aSlider.color;

        return new Color(r, g, b, a);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        var visible = (colorField.isFocused() || sliderFocused());
        rSlider.active = visible;
        gSlider.active = visible;
        bSlider.active = visible;
        aSlider.active = visible;

        if(!visible) {
            return;
        }

        context.fill(currentLocation[0], currentLocation[2], currentLocation[1], currentLocation[3], fillColor);

        context.drawHorizontalLine(currentLocation[0], currentLocation[1], currentLocation[2], outlineColor);
        context.drawHorizontalLine(currentLocation[0], currentLocation[1], currentLocation[3], outlineColor);

        context.drawVerticalLine(currentLocation[0], currentLocation[2], currentLocation[3], outlineColor);
        context.drawVerticalLine(currentLocation[1], currentLocation[2], currentLocation[3], outlineColor);

        rSlider.render(context, mouseX, mouseY, delta);
        gSlider.render(context, mouseX, mouseY, delta);
        bSlider.render(context, mouseX, mouseY, delta);
        aSlider.render(context, mouseX, mouseY, delta);
    }

    private boolean sliderFocused() {
        return (rSlider.isFocused() || gSlider.isFocused() || bSlider.isFocused() || aSlider.isFocused());
    }

    private int[] getDimensions(int x, int y) {
        return new int[] { x, x + width, y, y + height };
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    protected static class ColorSlider extends SliderWidget {

        private final String colorText;
        private int color = 0;

        private final Runnable onChanged;

        public ColorSlider(String colorText, double value, Runnable onChanged) {
            super(0, 0, 80, 18, Text.of(""), value);

            this.colorText = colorText + ": ";
            this.visible = true;

            applyValue();
            updateMessage();

            this.onChanged = onChanged;
        }

        @Override
        protected void updateMessage() {
            setMessage(Text.of(colorText + color));
        }

        @Override
        protected void applyValue() {
            color = (int)Math.round(value * 255);

            if(onChanged != null) {
                onChanged.run();
            }
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            super.renderWidget(context, mouseX, mouseY, delta);
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

    protected static class ColorTextField extends TextFieldWidget {

        public ColorTextField(TextRenderer textRenderer, int x, int y) {
            super(textRenderer, 64, 15, Text.of(""));

            setPosition(x, y);
        }
    }
}
