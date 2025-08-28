package org.loveroo.fireclient.screen.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.ModuleBase;
import org.loveroo.fireclient.screen.base.ConfigScreenBase;
import org.loveroo.fireclient.screen.base.ScrollableWidget;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleSelectScreen extends ConfigScreenBase {

    private ScrollableWidget modulesWidget;
    private ButtonWidget backButton;
    private TextFieldWidget searchBar;

    private String search = "";

    private final HashMap<String, ButtonWidget> moduleButtons = new HashMap<>();

    private final int moduleSelectWidth = 400;
    private final int moduleSelectHeight = 140;

    private static double scroll = 0.0;

    public ModuleSelectScreen() {
        super(Text.translatable("fireclient.screen.module_select.title"));
    }

    @Override
    public void init() {
        for(var module : FireClientside.getModules()) {
            var button = ButtonWidget.builder(module.getData().getShownName(), module::moduleConfigPressed)
                .tooltip(Tooltip.of(module.getData().getDescription()))
                .dimensions(0, 0, 120, 20)
                .build();
            
            moduleButtons.put(module.getData().getId(), button);
        }

        backButton = ButtonWidget.builder(Text.translatable("fireclient.screen.settings.back.name"), this::backButtonPressed)
            .dimensions(width/2 - 40, height/2 + moduleSelectHeight/2 + 20, 80, 20)
            .tooltip(Tooltip.of(Text.translatable("fireclient.screen.settings.back.tooltip")))
            .build();
            
        addDrawableChild(backButton);
            
        modulesWidget = new ScrollableWidget(this, moduleSelectWidth, moduleSelectHeight, 0, 30, List.of());
        modulesWidget.setPosition(width/2 - (moduleSelectWidth/2), height/2 - (moduleSelectHeight/2));
        modulesWidget.setScrollY(scroll);
        
        addDrawableChild(modulesWidget);
        
        var barWidth = moduleSelectWidth - 120;
        searchBar = new TextFieldWidget(client.textRenderer, barWidth, 15, Text.literal(""));
        searchBar.setPosition(width/2 - (barWidth/2), height/2 - (moduleSelectHeight/2) - 20);
        
        searchBar.setChangedListener(this::refreshSearch);
        searchBar.setText(search);

        addDrawableChild(searchBar);
        setFocused(searchBar);
    }

    private void filterModuleButtons() {
        var filter = search.toLowerCase().trim();
        var modules = new ArrayList<>(FireClientside.getModules().stream()
        .filter((module) -> {
            var nameSplit = module.getData().getName().getString().split(" ");

            for(var name : nameSplit) {
                if(name.toLowerCase().startsWith(filter)) {
                    return true;
                }
            }
            
            return false;
        })
        .map((module) -> module.getData().getId())
        .collect(Collectors.toList()));
        
        modules.sort(Comparator.naturalOrder());

        var buttons = new ArrayList<ButtonWidget>();
        
        var skips = 0;
        for(var i = 0; i < modules.size(); i++) {
            var module = FireClientside.getModule(modules.get(i));

            if(module.getData().isSkip()) {
                skips++;
                continue;
            }

            var index = i - skips;

            var x = ((index % 3) - 1) * 130;
            var button = moduleButtons.get(module.getData().getId());
            button.setPosition(width/2 - 60 + x, 0);

            buttons.add(button);
        }

        var entries = new ArrayList<ScrollableWidget.ElementEntry>();
        
        var size = buttons.size();
        var lineCount = (int)Math.ceil(size/3.0);

        for(int i = 0; i < lineCount; i++) {
            var entryWidgets = new ArrayList<ClickableWidget>();

            var moduleEntryIndex = (i*3);
            var moduleEntryCount = Math.min(3, size - moduleEntryIndex);

            for(int k = 0; k < moduleEntryCount; k++) {
                entryWidgets.add(buttons.get(moduleEntryIndex + k));
            }

            var entry = new ScrollableWidget.ElementEntry(entryWidgets);
            entries.add(entry);
        }

        modulesWidget.setEntries(entries);
        if(modules.size() == moduleButtons.size()) {
            modulesWidget.setScrollY(scroll);
        }
        else {
            modulesWidget.setScrollY(0);
        }
    }

    private void refreshSearch(String input) {
        search = input;
        filterModuleButtons();
    }

    @Override
    public void exitOnInventory() { }

    private void backButtonPressed(ButtonWidget button) {
        MinecraftClient.getInstance().setScreen(new MainConfigScreen());
    }

    @Override
    public void tick() {
        scroll = modulesWidget.getScrollY();
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

        context.drawCenteredTextWithShadow(text, Text.translatable("fireclient.screen.module_select.header"), width/2, height/2 - (moduleSelectHeight/2 + 30), 0xFFFFFFFF);
    }

    public static void resetScroll() {
        scroll = 0;
    }
}
