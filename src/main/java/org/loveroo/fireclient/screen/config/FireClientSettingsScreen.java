package org.loveroo.fireclient.screen.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.loveroo.fireclient.data.FireClientOption.SliderOptionData;
import org.loveroo.fireclient.screen.base.ConfigScreenBase;
import org.loveroo.fireclient.screen.base.ScrollableWidget;
import org.loveroo.fireclient.screen.widgets.SettingsSlider;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class FireClientSettingsScreen extends ConfigScreenBase {

    private ScrollableWidget settingsWidget;
    private ButtonWidget backButton;

    private final HashMap<String, ClickableWidget> settingsButtons = new HashMap<>();

    private final int settingsWidth = 440;
    private final int settingsHeight = 140;

    private TextFieldWidget searchBar;
    private String search = "";

    private static final MutableText defaultTrueText = Text.literal("✔").setStyle(Style.EMPTY.withColor(0x57D647));
    private static final MutableText defaultFalseText = Text.literal("❌").setStyle(Style.EMPTY.withColor(0xD63C3C));

    public FireClientSettingsScreen() {
        super(Text.translatable("fireclient.screen.settings.title"));
    }

    @Override
    public void init() {
        for(var option : FireClientOption.values()) {
            ClickableWidget widget = null;

            var width = 130;
            var height = 20;

            switch(option.getType()) {
                case TOGGLE -> {
                    widget = ButtonWidget.builder(getOptionLabel(option), (button) -> handleSettings(button, option))
                        .dimensions(0, 0, width, height)
                        .tooltip(Tooltip.of(option.getDescription()))
                        .build();
                }

                case SLIDER -> {
                    var sliderAmount = (double)option.getValue() / ((FireClientOption.SliderOptionData)option.getData()).getMaxValue();
                    widget = new SettingsSlider(option, 0, 0, width, height, option.getName(), sliderAmount) {

                        @Override
                        public void updateMessage() {
                            var data = (SliderOptionData)option.getData();
                            setMessage(data.updateMessage(option, option.getValue()));
                        }
                    };

                    widget.setTooltip(Tooltip.of(option.getDescription()));
                }
            }

            settingsButtons.put(option.name(), widget);
        }

        backButton = ButtonWidget.builder(Text.translatable("fireclient.screen.settings.back.name"), this::backButtonPressed)
            .dimensions(width/2 - 40, height/2 + settingsHeight/2 + 20, 80, 20)
            .tooltip(Tooltip.of(Text.translatable("fireclient.screen.settings.back.tooltip")))
            .build();

        addDrawableChild(backButton);

        settingsWidget = new ScrollableWidget(this, settingsWidth, settingsHeight, 0, 30, List.of());
        settingsWidget.setPosition(width/2 - (settingsWidth /2), height/2 - (settingsHeight /2));

        addDrawableChild(settingsWidget);

        var barWidth = settingsWidth - 120;
        searchBar = new TextFieldWidget(client.textRenderer, barWidth, 15, Text.literal(""));
        searchBar.setPosition(width/2 - (barWidth/2), height/2 - (settingsHeight/2) - 20);

        searchBar.setChangedListener(this::refreshSearch);
        searchBar.setText(search);

        addDrawableChild(searchBar);
        setFocused(searchBar);
    }

    private void backButtonPressed(ButtonWidget button) {
        MinecraftClient.getInstance().setScreen(new MainConfigScreen());
    }

    private void filterSettingsButtons() {
        var filter = search.toLowerCase().trim();
        
        var options = new ArrayList<>(Arrays.stream(FireClientOption.values())
        .filter((setting) -> {
            var nameSplit = setting.getName().getString().split(" ");

            for(var name : nameSplit) {
                if(name.toLowerCase().startsWith(filter)) {
                    return true;
                }
            }
            
            return false;
        })
        .sorted(Comparator.comparing(setting -> setting.getName().getString()))
        .collect(Collectors.toList()));

        var widgets = new ArrayList<ClickableWidget>();

        for(var i = 0; i < options.size(); i++) {
            var option = options.get(i);
            
            var x = ((i % 3) - 1) * 145;

            var widget = settingsButtons.get(option.name());
            widget.setPosition(width/2 - 65 + x, 0);

            widgets.add(widget);
        }

        var entries = new ArrayList<ScrollableWidget.ElementEntry>();

        var size = widgets.size();
        var lineCount = (int)Math.ceil(size/3.0);

        for(int i = 0; i < lineCount; i++) {
            var entryWidgets = new ArrayList<ClickableWidget>();

            var entryIndex = (i*3);
            var entryCount = Math.min(3, size - entryIndex);

            for(int k = 0; k < entryCount; k++) {
                entryWidgets.add(widgets.get(entryIndex + k));
            }

            var entry = new ScrollableWidget.ElementEntry(entryWidgets);
            entries.add(entry);
        }

        settingsWidget.setEntries(entries);
        settingsWidget.setScrollY(0);
    }

    private void refreshSearch(String input) {
        search = input;
        filterSettingsButtons();
    }

    private void handleSettings(ClickableWidget widget, FireClientOption option) {
        switch(option.getType()) {
            case TOGGLE -> {
                var value = (FireClientside.getSetting(option) == 0) ? 1 : 0;
                FireClientside.setSetting(option, value);

                widget.setMessage(getOptionLabel(option));
            }

            case SLIDER -> { }
        }
    }

    private Text getOptionLabel(FireClientOption option) {
        switch(option.getType()) {
            case TOGGLE -> {
                var value = FireClientside.getSetting(option);

                var nameText = option.getName();
                return ((value == 1) ? defaultTrueText : defaultFalseText).copy().append(" ").append(nameText);
            }

            case SLIDER -> { }
        }

        return option.getName();
    }

    @Override
    protected boolean escapePressed() {
        MinecraftClient.getInstance().setScreen(new MainConfigScreen());
        return true;
    }

    @Override
    public void exitOnInventory() { }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        var text = MinecraftClient.getInstance().textRenderer;

        context.drawCenteredTextWithShadow(text, Text.translatable("fireclient.screen.settings.header"), width/2, height/2 - (settingsHeight/2 + 30), 0xFFFFFFFF);
    }

    public static MutableText getTrueText() {
        return defaultTrueText.copy();
    }

    public static MutableText getFalseText() {
        return defaultFalseText.copy();
    }
}
