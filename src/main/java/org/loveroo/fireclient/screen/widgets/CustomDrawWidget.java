package org.loveroo.fireclient.screen.widgets;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.Nullable;
import org.loveroo.fireclient.screen.config.FireClientSettingsScreen;
import org.loveroo.fireclient.screen.widgets.ToggleButtonWidget.ToggleButtonBuilder;
import org.loveroo.fireclient.screen.widgets.ToggleButtonWidget.ToggleButtonBuilder.GetValue;
import org.loveroo.fireclient.screen.widgets.ToggleButtonWidget.ToggleButtonBuilder.SetValue;

import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;

public class CustomDrawWidget extends AbstractWidget {
    
    private final Draw draw;

    protected CustomDrawWidget(int x, int y, Draw draw) {
        super(x, y, 1, 1, Component.literal(""));

        this.draw = draw;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        var matrix = graphics.pose();
        matrix.pushMatrix();

        matrix.translate(getX(), getY());
        draw.draw(graphics, mouseX, mouseY, delta);

        matrix.popMatrix();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
        builder.add(NarratedElementType.TITLE, Component.literal(""));
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
    
        void draw(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta);
    }
}
