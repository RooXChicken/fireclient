package org.loveroo.fireclient.screen.widgets;

import java.lang.reflect.Field;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.screen.config.FireClientSettingsScreen;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ToggleButtonWidget extends ButtonWidget {

    protected ToggleButtonWidget(int x, int y, int width, int height, Text message, Tooltip tooltip, PressAction onPress) {
        super(x, y, width, height, message, onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        setTooltip(tooltip);
    }
    
    public static class ToggleButtonBuilder {
    
        @Nullable
        private final Text text;
    
        private GetValue getValue;
        private SetValue setValue;
    
        @Nullable
        private Runnable onChange = null;
    
        private Tooltip tooltip;
    
        private int x = 0;
        private int y = 0;
    
        private int width = 120;
        private int height = 20;
    
        public ToggleButtonBuilder(@Nullable Text text) {
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
        
        private void onPress(ButtonWidget button) {
            var value = !getValue.get();
            setValue.set(value);
    
            button.setMessage(getToggleText(value));
    
            if(onChange != null) {
                onChange.run();
            }
        }
    
        protected MutableText getToggleText(boolean value) {
            var toggle = ((value) ? FireClientSettingsScreen.defaultTrueText : FireClientSettingsScreen.defaultFalseText);
    
            if(text != null) {
                return toggle.copy().append(" ").append(text);
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

