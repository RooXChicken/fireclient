package org.loveroo.fireclient.screen.widgets;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class CapeRenderWidget extends AbstractWidget {

    private final String capeName;
    private Identifier texture = Identifier.withDefaultNamespace("");
    
    private float scale;

    public CapeRenderWidget(String name, Identifier texture, int x, int y, float scale) {
        super(x, y, 12, 17, Component.literal(name));
        this.scale = scale;
        this.capeName = name;

        this.texture = texture;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        var matrix = graphics.pose();
        matrix.pushMatrix();
        matrix.translate(getX(), getY());
        matrix.scale(scale);
        
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, 0, 0, 0, 0, 12, 17, 64, 32);

        matrix.popMatrix();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
        builder.add(NarratedElementType.TITLE, capeName);
    }
}
