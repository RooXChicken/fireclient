package org.loveroo.fireclient.screen.widgets;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

public class RenderItemWidget extends AbstractWidget {

    private final ItemStack item;

    public RenderItemWidget(Item item, int x, int y) {
        this.item = new ItemStack(item);
        super(x, y, 16, 16, item.getName(item.getDefaultInstance())); // TODO: idk
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        context.item(item, getX(), getY());
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
        builder.add(NarratedElementType.TITLE, item.getItemName());
    }
}
