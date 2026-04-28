package org.loveroo.fireclient.screen.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import org.jspecify.annotations.NonNull;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.screen.base.ConfigScreenBase;
import org.loveroo.fireclient.screen.base.ScrollableWidget;
import org.loveroo.fireclient.screen.widgets.FavoriteButtonWidget.FavoriteButtonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleSelectScreen extends ConfigScreenBase {

    private ScrollableWidget modulesWidget;
    private Button backButton;
    private EditBox searchBar;

    private String search = "";

    private final HashMap<String, Button> moduleButtons = new HashMap<>();

    private final int moduleSelectWidth = 400;
    private final int moduleSelectHeight = 140;

    private static double scroll = 0.0;

    public ModuleSelectScreen() {
        super(Component.translatable("fireclient.screen.module_select.title"));
    }

    @Override
    public void init() {
        for(var module : FireClientside.getModules()) {
            var button = new FavoriteButtonBuilder(module.getData().getShownName())
                .onPress(module::moduleConfigPressed)
                .getValue(module.getData()::isFavorited)
                .setValue(module.getData()::setFavorited)
                .tooltip(Tooltip.create(module.getData().getDescription()))
                .dimensions(0, 0, 120, 20)
                .build();
            
            moduleButtons.put(module.getData().getId(), button);
        }

        backButton = Button.builder(Component.translatable("fireclient.screen.settings.back.name"), this::backButtonPressed)
            .bounds(width/2 - 40, height/2 + moduleSelectHeight/2 + 20, 80, 20)
            .tooltip(Tooltip.create(Component.translatable("fireclient.screen.settings.back.tooltip")))
            .build();
            
        addRenderableWidget(backButton);
            
        modulesWidget = new ScrollableWidget(this, moduleSelectWidth, moduleSelectHeight, 0, 30, List.of());
        modulesWidget.setPosition(width/2 - (moduleSelectWidth/2), height/2 - (moduleSelectHeight/2));
        modulesWidget.setScrollAmount(scroll);
        
        addRenderableWidget(modulesWidget);
        
        var barWidth = moduleSelectWidth - 120;
        searchBar = new EditBox(minecraft.font, barWidth, 15, Component.literal(""));
        searchBar.setPosition(width/2 - (barWidth/2), height/2 - (moduleSelectHeight/2) - 20);
        
        searchBar.setResponder(this::refreshSearch);
        searchBar.setValue(search);

        addRenderableWidget(searchBar);
        setFocused(searchBar);
    }

    private void filterModuleButtons() {
        var filter = search.toLowerCase().trim();
        var sortedModules = new ArrayList<>(FireClientside.getModules().stream()
        .filter((module) -> {
            var nameSplit = module.getData().getName().getString().split(" ");

            for(var name : nameSplit) {
                if(name.toLowerCase().startsWith(filter)) {
                    return true;
                }
            }
            
            return false;
        })
        // .map((module) -> module.getData().getId())
        .collect(Collectors.toList()));
        
        sortedModules.sort((module1, module2) -> {
            var favorited1 = module1.getData().isFavorited();
            var favorited2 = module2.getData().isFavorited();

            if((favorited1 && favorited2) || (!favorited1 && !favorited2)) {
                return module1.getData().getId().compareTo(module2.getData().getId());
            }
            else if(favorited1 &&! favorited2) {
                return -1;
            }
            else if(!favorited1 && favorited2) {
                return 1;
            }

            return 0;
        });

        var modules = sortedModules.stream().map((module) -> module.getData().getId()).toList();
        var buttons = new ArrayList<Button>();
        
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
            var entryWidgets = new ArrayList<AbstractWidget>();

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
            modulesWidget.setScrollAmount(scroll);
        }
        else {
            modulesWidget.setScrollAmount(0);
        }
    }

    private void refreshSearch(String input) {
        search = input;
        filterModuleButtons();
    }

    @Override
    public void exitOnInventory() { }

    private void backButtonPressed(Button button) {
        Minecraft.getInstance().setScreen(new MainConfigScreen());
    }

    @Override
    public void tick() {
        scroll = modulesWidget.scrollAmount();
    }

    @Override
    protected boolean escapePressed() {
        Minecraft.getInstance().setScreen(new MainConfigScreen());
        return true;
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        var text = Minecraft.getInstance().font;

        graphics.centeredText(text, Component.translatable("fireclient.screen.module_select.header"), width/2, height/2 - (moduleSelectHeight/2 + 30), 0xFFFFFFFF);
        renderTutorialText(graphics, Component.translatable("fireclient.screen.module_select.tutorial"));
    }

    public static void resetScroll() {
        scroll = 0;
    }
}
