package org.loveroo.fireclient.screen.widgets;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RenderItemWidget extends ClickableWidget {

    private final ItemStack item;

    public RenderItemWidget(Item item, int x, int y) {
        super(x, y, 16, 16, item.getName());
        this.item = new ItemStack(item);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawItem(item, getX(), getY());
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, item.getName());
    }
}
