package org.loveroo.fireclient.screen.base;

import java.util.HashMap;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.KeyMapping;
import org.jspecify.annotations.NonNull;

public class ScrollableWidget extends ContainerObjectSelectionList<ScrollableWidget.Entry> {

    public ScrollableWidget(Screen base, int width, int contentHeight, int y, int itemHeight, List<ElementEntry> entries) {
        super(Minecraft.getInstance(), width, contentHeight, y, itemHeight);
        // FireClient.LOGGER.info("{}", getScrollY());
        
        for(var entry : entries) {
            addEntry(entry);
        }


        // setScrollY(getScrollY());
        // recalculateAllChildrenPositions();
    }

    public void setEntries(List<ElementEntry> entries) {
        clearEntries();
        
        for(var entry : entries) {
            addEntry(entry);
        }
    }

    public void update() {
        KeyMapping.resetMapping();
        this.updateChildren();
    }

    public void updateChildren() {
        this.children().forEach(ScrollableWidget.Entry::update);
    }

    @Override
    public int getRowWidth() {
        return width - 31;
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        setScrollAmount(scrollAmount());
    }

    public abstract static class Entry extends ContainerObjectSelectionList.Entry<ScrollableWidget.Entry> {
        abstract void update();
    }

    public static class ElementEntry extends ScrollableWidget.Entry {

        private final List<AbstractWidget> widgets;
        private final HashMap<AbstractWidget, Integer> heightOffset = new HashMap<>();

        public ElementEntry(List<AbstractWidget> widgets) {
            this.widgets = widgets;

            for(var widget : widgets) {
                heightOffset.put(widget, widget.getY());
            }
        }

        @Override
        void update() { }

        @Override
        public @NonNull List<? extends GuiEventListener> children() {
            return widgets;
        }

        @Override
        public @NonNull List<? extends NarratableEntry> narratables() {
            return widgets;
        }

        @Override
        public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            // TODO: might be this#getContentY
            for(var widget : widgets) {
                widget.setPosition(widget.getX(), getY() + heightOffset.getOrDefault(widget, 0));
                widget.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
            }
        }
    }
}
