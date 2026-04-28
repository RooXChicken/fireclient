package org.loveroo.fireclient.screen.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.jspecify.annotations.NonNull;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.loveroo.fireclient.data.FireClientOption.SliderOptionData;
import org.loveroo.fireclient.screen.base.ConfigScreenBase;
import org.loveroo.fireclient.screen.base.ScrollableWidget;
import org.loveroo.fireclient.screen.widgets.SettingsSlider;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;

public class FireClientSettingsScreen extends ConfigScreenBase {

    private ScrollableWidget settingsWidget;
    private Button backButton;

    private final HashMap<String, AbstractWidget> settingsButtons = new HashMap<>();

    private final int settingsWidth = 440;
    private final int settingsHeight = 140;

    private EditBox searchBar;
    private String search = "";

    private static final MutableComponent defaultTrueText = Component.literal("✔").setStyle(Style.EMPTY.withColor(0x57D647));
    private static final MutableComponent defaultFalseText = Component.literal("❌").setStyle(Style.EMPTY.withColor(0xD63C3C));

    public FireClientSettingsScreen() {
        super(Component.translatable("fireclient.screen.settings.title"));
    }

    @Override
    public void init() {
        for(var option : FireClientOption.values()) {
            AbstractWidget widget = null;

            var width = 130;
            var height = 20;

            switch(option.getType()) {
                case TOGGLE -> {
                    widget = Button.builder(getOptionLabel(option), (button) -> handleSettings(button, option))
                        .bounds(0, 0, width, height)
                        .tooltip(Tooltip.create(option.getDescription()))
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

                    widget.setTooltip(Tooltip.create(option.getDescription()));
                }
            }

            settingsButtons.put(option.name(), widget);
        }

        backButton = Button.builder(Component.translatable("fireclient.screen.settings.back.name"), this::backButtonPressed)
            .bounds(width/2 - 40, height/2 + settingsHeight/2 + 20, 80, 20)
            .tooltip(Tooltip.create(Component.translatable("fireclient.screen.settings.back.tooltip")))
            .build();

        addRenderableWidget(backButton);

        settingsWidget = new ScrollableWidget(this, settingsWidth, settingsHeight, 0, 30, List.of());
        settingsWidget.setPosition(width/2 - (settingsWidth /2), height/2 - (settingsHeight /2));

        addRenderableWidget(settingsWidget);

        var barWidth = settingsWidth - 120;
        searchBar = new EditBox(minecraft.font, barWidth, 15, Component.literal(""));
        searchBar.setPosition(width/2 - (barWidth/2), height/2 - (settingsHeight/2) - 20);

        searchBar.setResponder(this::refreshSearch);
        searchBar.setValue(search);

        addRenderableWidget(searchBar);
        setFocused(searchBar);
    }

    private void backButtonPressed(Button button) {
        Minecraft.getInstance().setScreen(new MainConfigScreen());
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

        var widgets = new ArrayList<AbstractWidget>();

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
            var entryWidgets = new ArrayList<AbstractWidget>();

            var entryIndex = (i*3);
            var entryCount = Math.min(3, size - entryIndex);

            for(int k = 0; k < entryCount; k++) {
                entryWidgets.add(widgets.get(entryIndex + k));
            }

            var entry = new ScrollableWidget.ElementEntry(entryWidgets);
            entries.add(entry);
        }

        settingsWidget.setEntries(entries);
        settingsWidget.setScrollAmount(0);
    }

    private void refreshSearch(String input) {
        search = input;
        filterSettingsButtons();
    }

    private void handleSettings(AbstractWidget widget, FireClientOption option) {
        switch(option.getType()) {
            case TOGGLE -> {
                var value = (FireClientside.getSetting(option) == 0) ? 1 : 0;
                FireClientside.setSetting(option, value);

                widget.setMessage(getOptionLabel(option));
            }

            case SLIDER -> { }
        }
    }

    private Component getOptionLabel(FireClientOption option) {
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
        Minecraft.getInstance().setScreen(new MainConfigScreen());
        return true;
    }

    @Override
    public void exitOnInventory() { }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        var text = Minecraft.getInstance().font;

        graphics.centeredText(text, Component.translatable("fireclient.screen.settings.header"), width/2, height/2 - (settingsHeight/2 + 30), 0xFFFFFFFF);
    }

    public static MutableComponent getTrueText() {
        return defaultTrueText.copy();
    }

    public static MutableComponent getFalseText() {
        return defaultFalseText.copy();
    }
}
