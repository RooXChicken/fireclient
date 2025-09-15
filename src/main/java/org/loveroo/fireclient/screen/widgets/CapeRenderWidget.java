package org.loveroo.fireclient.screen.widgets;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CapeRenderWidget extends ClickableWidget {

    private final String capeName;
    private Identifier texture = Identifier.ofVanilla("");
    
    private float scale;

    public CapeRenderWidget(String name, Identifier texture, int x, int y, float scale) {
        super(x, y, 12, 17, Text.literal(name));
        this.scale = scale;
        this.capeName = name;

        this.texture = texture;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        var matrix = context.getMatrices();
        matrix.pushMatrix();
        matrix.translate(getX(), getY());
        matrix.scale(scale);
        
        context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, 0, 0, 0, 0, 12, 17, 64, 32);

        matrix.popMatrix();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, capeName);
    }
}
