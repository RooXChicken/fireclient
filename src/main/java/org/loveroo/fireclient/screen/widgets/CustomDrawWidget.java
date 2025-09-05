package org.loveroo.fireclient.screen.widgets;

import org.jetbrains.annotations.Nullable;
import org.loveroo.fireclient.screen.config.FireClientSettingsScreen;
import org.loveroo.fireclient.screen.widgets.ToggleButtonWidget.ToggleButtonBuilder;
import org.loveroo.fireclient.screen.widgets.ToggleButtonWidget.ToggleButtonBuilder.GetValue;
import org.loveroo.fireclient.screen.widgets.ToggleButtonWidget.ToggleButtonBuilder.SetValue;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class CustomDrawWidget extends ClickableWidget {
    
    private final Draw draw;

    protected CustomDrawWidget(int x, int y, Draw draw) {
        super(x, y, 1, 1, Text.literal(""));

        this.draw = draw;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        var matrix = context.getMatrices();
        matrix.pushMatrix();

        matrix.translate(getX(), getY());
        draw.draw(context, mouseX, mouseY, delta);

        matrix.popMatrix();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, Text.literal(""));
    }

    public static class CustomDrawBuilder {
    
        private Draw draw;
    
        private int x = 0;
        private int y = 0;
    
        public CustomDrawWidget build() {
            return new CustomDrawWidget(x, y, draw);
        }
    
        public CustomDrawBuilder onDraw(Draw draw) {
            this.draw = draw;
    
            return this;
        }
    
        public CustomDrawBuilder position(int x, int y) {
            this.x = x;
            this.y = y;
    
            return this;
        }
    }

    public interface Draw {
    
        void draw(DrawContext context, int mouseX, int mouseY, float delta);
    }
}
