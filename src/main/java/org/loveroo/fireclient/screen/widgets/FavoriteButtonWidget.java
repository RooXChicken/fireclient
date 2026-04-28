package org.loveroo.fireclient.screen.widgets;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.screen.widgets.FavoriteButtonWidget.FavoriteButtonBuilder.GetFavoriteStatus;
import org.loveroo.fireclient.screen.widgets.FavoriteButtonWidget.FavoriteButtonBuilder.SetFavoriteStatus;

import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.MouseButtonInfo;

public class FavoriteButtonWidget extends Button.Plain {

    private static final net.minecraft.network.chat.Component favoriteIcon = net.minecraft.network.chat.Component.literal("⭐");

    private static final Color enabledColor = Color.fromRGB(0xF2EF8D);
    private static final Color hoveredColor = Color.fromRGB(0xD9D768);

    private final GetFavoriteStatus getFavoriteStatus;
    private final SetFavoriteStatus setFavoriteStatus;

    private boolean favorited = false;

    private int lastButton = -1;

    protected FavoriteButtonWidget(int x, int y, int width, int height, net.minecraft.network.chat.Component message, Tooltip tooltip, Consumer<Button> onPress, GetFavoriteStatus getFavoriteStatus, SetFavoriteStatus setFavoriteStatus) {
        super(x, y, width, height, message, onPress::accept, Button.DEFAULT_NARRATION);

        setTooltip(tooltip);

        this.getFavoriteStatus = getFavoriteStatus;
        this.setFavoriteStatus = setFavoriteStatus;

        favorited = getFavoriteStatus.get();
    }

    @Override
	public void onClick(MouseButtonEvent click, boolean doubled) {
        if(lastButton == 0) {
            this.onPress(click);
            return;
        }

        favorited = !getFavoriteStatus.get();
        setFavoriteStatus.set(favorited);
	}

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        lastButton = click.button();
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) {
        lastButton = click.button();
        return super.mouseReleased(click);
    }

    @Override
    protected boolean isValidClickButton(MouseButtonInfo input) {
		return input.button() == 0 || input.button() == 1;
	}

    @Override
    protected void extractContents(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        super.extractContents(graphics, mouseX, mouseY, deltaTicks);

        if(favorited) {
            var textRenderer = Minecraft.getInstance().font;

            var matricies = graphics.pose();
            matricies.pushMatrix();

            matricies.translate(this.getX() + this.getWidth() - 1, this.getY() + 2);
            if(isHovered()) {
                matricies.scale(1.35f, 1.35f);
            }

            graphics.text(textRenderer, favoriteIcon, -4, -4, enabledColor.toInt(), true);

            matricies.popMatrix();
        }
    }

    public static class FavoriteButtonBuilder {

        @Nullable
        private final net.minecraft.network.chat.Component text;
    
        private GetFavoriteStatus getFavoriteStatus;
        private SetFavoriteStatus setFavoriteStatus;
    
        @Nullable
        private Consumer<Button> onPress = null;
    
        private Tooltip tooltip;
    
        private int x = 0;
        private int y = 0;
    
        private int width = 120;
        private int height = 20;
    
        public FavoriteButtonBuilder(@Nullable net.minecraft.network.chat.Component text) {
            this.text = text;
        }
    
        public FavoriteButtonWidget build() {
            return new FavoriteButtonWidget(x, y, width, height, text, tooltip, onPress, getFavoriteStatus, setFavoriteStatus);
        }
    
        public FavoriteButtonBuilder getValue(GetFavoriteStatus getValue) {
            this.getFavoriteStatus = getValue;
    
            return this;
        }
    
        public FavoriteButtonBuilder setValue(SetFavoriteStatus setValue) {
            this.setFavoriteStatus = setValue;
    
            return this;
        }
    
        public FavoriteButtonBuilder position(int x, int y) {
            this.x = x;
            this.y = y;
    
            return this;
        }
    
        public FavoriteButtonBuilder scale(int width, int height) {
            this.width = width;
            this.height = height;
    
            return this;
        }
    
        public FavoriteButtonBuilder dimensions(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            
            this.width = width;
            this.height = height;
    
            return this;
        }
    
        public FavoriteButtonBuilder tooltip(Tooltip tooltip) {
            this.tooltip = tooltip;
    
            return this;
        }
    
        public FavoriteButtonBuilder onPress(Consumer<Button> onPress) {
            this.onPress = onPress;
    
            return this;
        }
    
        public interface GetFavoriteStatus {
    
            boolean get();
        }
    
        public interface SetFavoriteStatus {
    
            void set(boolean value);
        }
    }
}
