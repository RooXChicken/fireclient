package org.loveroo.fireclient.screen.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.loveroo.fireclient.screen.base.ConfigScreenBase;
import org.loveroo.fireclient.screen.base.ScrollableWidget;
import org.loveroo.fireclient.screen.widgets.SettingsSlider;

import java.util.ArrayList;

public class FireClientSettingsScreen extends ConfigScreenBase {

    private ScrollableWidget settingsWidget;
    private ButtonWidget backButton;

    private final int settingsWidth = 440;
    private final int settingsHeight = 140;

    public static final Text defaultTrueText = MutableText.of(new PlainTextContent.Literal("✔")).setStyle(Style.EMPTY.withColor(0x57D647));
    public static final  Text defaultFalseText = MutableText.of(new PlainTextContent.Literal("❌")).setStyle(Style.EMPTY.withColor(0xD63C3C));

    public FireClientSettingsScreen() {
        super(Text.translatable("fireclient.screen.settings.title"));
    }

    @Override
    public void init() {
        var settingsButtons = new ArrayList<ClickableWidget>();
        var entries = new ArrayList<ScrollableWidget.ElementEntry>();

        for(var i = 0; i < FireClientOption.values().length; i++) {
            var option = FireClientOption.values()[i];

            var x = ((i % 3) - 1) * 145;

            ClickableWidget widget = null;

            var xPos = width/2 - 65 + x;
            var width = 130;
            var height = 20;

            switch(option.getType()) {
                case TOGGLE -> {
                    widget = ButtonWidget.builder(getOptionLabel(option), (button) -> handleSettings(button, option))
                                .dimensions(xPos, 0, width, height)
                                .tooltip(Tooltip.of(option.getDescription()))
                                .build();
                }

                case SLIDER -> {
                    var sliderAmount = (double)option.getValue() / ((FireClientOption.SliderOptionData)option.getData()).getMaxValue();
                    widget = new SettingsSlider(option, xPos, 0, width, height, option.getName(), sliderAmount) {

                        @Override
                        public void updateMessage() {
                            Text amountText = null;

                            var amount = option.getValue();
                            if(amount >= ((FireClientOption.SliderOptionData)option.getData()).getMaxValue()) {
                                amountText = Text.translatable("fireclient.settings.chat_history.unlimited");
                            }
                            else {
                                amountText = Text.literal(String.valueOf(amount));
                            }

                            setMessage(Text.translatable("fireclient.settings.chat_history.text", amountText));
                        }
                    };
                    widget.setTooltip(Tooltip.of(option.getDescription()));
                }
            }

            settingsButtons.add(widget);
        }

        backButton = ButtonWidget.builder(Text.translatable("fireclient.screen.settings.back.name"), this::backButtonPressed)
                .dimensions(width/2 - 40, height/2 + settingsHeight/2 + 20, 80, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.screen.settings.back.tooltip")))
                .build();

        addDrawableChild(backButton);

        var size = settingsButtons.size();
        var lineCount = (int)Math.ceil(size/3.0);

        for(int i = 0; i < lineCount; i++) {
            var entryWidgets = new ArrayList<ClickableWidget>();

            var settingsEntryIndex = (i*3);
            var settingsEntryCount = Math.min(3, size - settingsEntryIndex);

            for(int k = 0; k < settingsEntryCount; k++) {
                entryWidgets.add(settingsButtons.get(settingsEntryIndex + k));
            }

            var entry = new ScrollableWidget.ElementEntry(entryWidgets);
            entries.add(entry);
        }

        settingsWidget = new ScrollableWidget(this, settingsWidth, settingsHeight, 0, 30, entries);
        settingsWidget.setPosition(width/2 - (settingsWidth /2), height/2 - (settingsHeight /2));

        addDrawableChild(settingsWidget);
    }

    private void backButtonPressed(ButtonWidget button) {
        MinecraftClient.getInstance().setScreen(new MainConfigScreen());
    }

    private void handleSettings(ClickableWidget widget, FireClientOption option) {
        switch(option.getType()) {
            case TOGGLE -> {
                var value = (FireClientside.getSetting(option) == 0) ? 1 : 0;
                FireClientside.setSetting(option, value);

                widget.setMessage(getOptionLabel(option));
            }

            case SLIDER -> {

            }
        }
    }

    private Text getOptionLabel(FireClientOption option) {
        switch(option.getType()) {
            case TOGGLE -> {
                var value = FireClientside.getSetting(option);

                var nameText = option.getName();
                return ((value == 1) ? defaultTrueText : defaultFalseText).copy().append(" ").append(nameText);
            }
        }

        return option.getName();
    }

    @Override
    protected boolean escapePressed() {
        MinecraftClient.getInstance().setScreen(new MainConfigScreen());
        return true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        var text = MinecraftClient.getInstance().textRenderer;

        context.drawCenteredTextWithShadow(text, Text.translatable("fireclient.screen.settings.header"), width/2, height/2 - (settingsHeight/2 + 20), 0xFFFFFFFF);
    }
}
