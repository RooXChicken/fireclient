package org.loveroo.fireclient.screen.widgets;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.screen.config.FireClientSettingsScreen;
import org.loveroo.fireclient.screen.widgets.FavoriteButtonWidget.FavoriteButtonBuilder.GetFavoriteStatus;
import org.loveroo.fireclient.screen.widgets.FavoriteButtonWidget.FavoriteButtonBuilder.SetFavoriteStatus;
import org.loveroo.fireclient.screen.widgets.ToggleButtonWidget.ToggleButtonBuilder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class FavoriteButtonWidget extends ButtonWidget {

    private static final Text favoriteIcon = Text.literal("⭐");

    private static final Color enabledColor = Color.fromRGB(0xF2EF8D);
    private static final Color hoveredColor = Color.fromRGB(0xD9D768);

    private final GetFavoriteStatus getFavoriteStatus;
    private final SetFavoriteStatus setFavoriteStatus;

    private boolean favorited = false;

    private int lastButton = -1;

    protected FavoriteButtonWidget(int x, int y, int width, int height, Text message, Tooltip tooltip, Consumer<ButtonWidget> onPress, GetFavoriteStatus getFavoriteStatus, SetFavoriteStatus setFavoriteStatus) {
        super(x, y, width, height, message, (button) -> onPress.accept(button), ButtonWidget.DEFAULT_NARRATION_SUPPLIER);

        setTooltip(tooltip);

        this.getFavoriteStatus = getFavoriteStatus;
        this.setFavoriteStatus = setFavoriteStatus;

        favorited = getFavoriteStatus.get();
    }

    @Override
	public void onClick(double mouseX, double mouseY) {
        if(lastButton == 0) {
            this.onPress();
            return;
        }

        favorited = !getFavoriteStatus.get();
        setFavoriteStatus.set(favorited);
	}

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        lastButton = button;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        lastButton = button;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected boolean isValidClickButton(int button) {
		return button == 0 || button == 1;
	}

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);

        if(favorited) {
            var textRenderer = MinecraftClient.getInstance().textRenderer;

            var matricies = context.getMatrices();
            matricies.pushMatrix();

            matricies.translate(this.getX() + this.getWidth() - 1, this.getY() + 2);
            if(isHovered()) {
                matricies.scale(1.35f, 1.35f);
            }

            context.drawText(textRenderer, favoriteIcon, -4, -4, enabledColor.toInt(), true);

            matricies.popMatrix();
        }
    }
    
    public static class FavoriteButtonBuilder {

        @Nullable
        private final Text text;
    
        private GetFavoriteStatus getFavoriteStatus;
        private SetFavoriteStatus setFavoriteStatus;
    
        @Nullable
        private Consumer<ButtonWidget> onPress = null;
    
        private Tooltip tooltip;
    
        private int x = 0;
        private int y = 0;
    
        private int width = 120;
        private int height = 20;
    
        public FavoriteButtonBuilder(@Nullable Text text) {
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
    
        public FavoriteButtonBuilder onPress(Consumer<ButtonWidget> onPress) {
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
