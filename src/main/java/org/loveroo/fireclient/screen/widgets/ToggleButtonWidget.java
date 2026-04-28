package org.loveroo.fireclient.screen.widgets;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button.OnPress;
import org.jetbrains.annotations.Nullable;
import org.loveroo.fireclient.screen.config.FireClientSettingsScreen;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;

public class ToggleButtonWidget extends Button.Plain {

    protected ToggleButtonWidget(int x, int y, int width, int height, net.minecraft.network.chat.Component message, Tooltip tooltip, OnPress onPress) {
        super(x, y, width, height, message, onPress, Button.DEFAULT_NARRATION);
        setTooltip(tooltip);
    }

    public static class ToggleButtonBuilder {
    
        @Nullable
        private final net.minecraft.network.chat.Component text;

        private MutableComponent trueText = FireClientSettingsScreen.getTrueText();
        private MutableComponent falseText = FireClientSettingsScreen.getFalseText();
    
        private GetValue getValue;
        private SetValue setValue;
    
        @Nullable
        private Runnable onChange = null;
    
        private Tooltip tooltip;
    
        private int x = 0;
        private int y = 0;
    
        private int width = 120;
        private int height = 20;
    
        public ToggleButtonBuilder(@Nullable net.minecraft.network.chat.Component text) {
            this.text = text;
        }
    
        public ToggleButtonWidget build() {
            return new ToggleButtonWidget(x, y, width, height, getToggleText(getValue.get()), tooltip, this::onPress);
        }
    
        public ToggleButtonBuilder getValue(GetValue getValue) {
            this.getValue = getValue;
    
            return this;
        }
    
        public ToggleButtonBuilder setValue(SetValue setValue) {
            this.setValue = setValue;
    
            return this;
        }
    
        public ToggleButtonBuilder position(int x, int y) {
            this.x = x;
            this.y = y;
    
            return this;
        }
    
        public ToggleButtonBuilder scale(int width, int height) {
            this.width = width;
            this.height = height;
    
            return this;
        }
    
        public ToggleButtonBuilder dimensions(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            
            this.width = width;
            this.height = height;
    
            return this;
        }
    
        public ToggleButtonBuilder tooltip(Tooltip tooltip) {
            this.tooltip = tooltip;
    
            return this;
        }
    
        public ToggleButtonBuilder onChange(Runnable onChange) {
            this.onChange = onChange;
    
            return this;
        }

        public ToggleButtonBuilder trueText(MutableComponent trueText) {
            this.trueText = trueText;

            return this;
        }

        public ToggleButtonBuilder falseText(MutableComponent falseText) {
            this.falseText = falseText;

            return this;
        }
        
        private void onPress(Button button) {
            var value = !getValue.get();
            setValue.set(value);
    
            button.setMessage(getToggleText(value));
    
            if(onChange != null) {
                onChange.run();
            }
        }
    
        protected MutableComponent getToggleText(boolean value) {
            var toggle = ((value) ? trueText.copy() : falseText.copy());
    
            if(text != null) {
                return toggle.append(" ").append(text);
            }
            else {
                return toggle;
            }
        }
    
        public interface GetValue {
    
            boolean get();
        }
    
        public interface SetValue {
    
            void set(boolean value);
        }
    }
}

