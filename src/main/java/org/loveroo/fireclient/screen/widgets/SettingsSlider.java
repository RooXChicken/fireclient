package org.loveroo.fireclient.screen.widgets;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import org.loveroo.fireclient.data.FireClientOption;

public abstract class SettingsSlider extends AbstractSliderButton {

    private final FireClientOption option;

    public SettingsSlider(FireClientOption option, int x, int y, int width, int height, Component text, double value) {
        super(x, y, width, height, text, value);
        this.option = option;

        updateMessage();
    }

    @Override
    protected void applyValue() {
        var amount = (int)(value * ((FireClientOption.SliderOptionData)option.getData()).getMaxValue());
        option.setValue(amount);
    }
}
