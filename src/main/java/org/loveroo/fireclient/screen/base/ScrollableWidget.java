package org.loveroo.fireclient.screen.base;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.option.KeyBinding;

import java.util.HashMap;
import java.util.List;

public class ScrollableWidget extends ElementListWidget<ScrollableWidget.Entry> {

    public ScrollableWidget(Screen base, int width, int contentHeight, int y, int itemHeight, List<ElementEntry> entries) {
        super(MinecraftClient.getInstance(), width, contentHeight, y, itemHeight);

        for(var entry : entries) {
            addEntry(entry);
        }
    }

    public void update() {
        KeyBinding.updateKeysByCode();
        this.updateChildren();
    }

    public void updateChildren() {
        this.children().forEach(ScrollableWidget.Entry::update);
    }

    @Override
    public int getRowWidth() {
        return width - 31;
    }

    public abstract static class Entry extends ElementListWidget.Entry<ScrollableWidget.Entry> {
        abstract void update();
    }

    public static class ElementEntry extends ScrollableWidget.Entry {

        private final List<ClickableWidget> widgets;
        private final HashMap<ClickableWidget, Integer> heightOffset = new HashMap<>();

        public ElementEntry(List<ClickableWidget> widgets) {
            this.widgets = widgets;

            for(var widget : widgets) {
                heightOffset.put(widget, widget.getY());
            }
        }

        @Override
        void update() { }

        @Override
        public List<? extends Element> children() {
            return widgets;
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return widgets;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            for(var widget : widgets) {
                widget.setPosition(widget.getX(), y + heightOffset.getOrDefault(widget, 0));
                widget.render(context, mouseX, mouseY, tickDelta);
            }
        }
    }
}
