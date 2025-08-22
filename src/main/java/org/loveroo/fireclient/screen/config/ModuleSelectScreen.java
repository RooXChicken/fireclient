package org.loveroo.fireclient.screen.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.screen.base.ConfigScreenBase;
import org.loveroo.fireclient.screen.base.ScrollableWidget;

import java.util.ArrayList;
import java.util.Comparator;

public class ModuleSelectScreen extends ConfigScreenBase {

    private ScrollableWidget modulesWidget;
    private ButtonWidget backButton;

    private final int moduleSelectWidth = 400;
    private final int moduleSelectHeight = 140;

    private static double scroll = 0.0;

    public ModuleSelectScreen() {
        super(Text.translatable("fireclient.screen.module_select.title"));
    }

    @Override
    public void init() {
        var moduleButtons = new ArrayList<ClickableWidget>();
        var entries = new ArrayList<ScrollableWidget.ElementEntry>();

        var skips = 0;
        var modules = new ArrayList<String>();

        for(var module : FireClientside.getModules()) {
            modules.add(module.getData().getId());
        }

        modules.sort(Comparator.naturalOrder());

        for(var i = 0; i < modules.size(); i++) {
            var module = FireClientside.getModule(modules.get(i));

            if(module.getData().isSkip()) {
                skips++;
                continue;
            }

            var index = i - skips;

            var x = ((index % 3) - 1) * 130;

            moduleButtons.add(ButtonWidget.builder(module.getData().getShownName(), module::moduleConfigPressed)
                    .tooltip(Tooltip.of(module.getData().getDescription()))
                    .dimensions(width/2 - 60 + x, 0, 120, 20)
                    .build());
        }

        backButton = ButtonWidget.builder(Text.translatable("fireclient.screen.settings.back.name"), this::backButtonPressed)
                .dimensions(width/2 - 40, height/2 + moduleSelectHeight/2 + 20, 80, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.screen.settings.back.tooltip")))
                .build();

        addDrawableChild(backButton);

        var size = moduleButtons.size();
        var lineCount = (int)Math.ceil(size/3.0);

        for(int i = 0; i < lineCount; i++) {
            var entryWidgets = new ArrayList<ClickableWidget>();

            var moduleEntryIndex = (i*3);
            var moduleEntryCount = Math.min(3, size - moduleEntryIndex);

            for(int k = 0; k < moduleEntryCount; k++) {
                entryWidgets.add(moduleButtons.get(moduleEntryIndex + k));
            }

            var entry = new ScrollableWidget.ElementEntry(entryWidgets);
            entries.add(entry);
        }

        modulesWidget = new ScrollableWidget(this, moduleSelectWidth, moduleSelectHeight, 0, 30, entries);
        modulesWidget.setPosition(width/2 - (moduleSelectWidth/2), height/2 - (moduleSelectHeight/2));
        modulesWidget.setScrollY(scroll);

        addDrawableChild(modulesWidget);
    }

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

        context.drawCenteredTextWithShadow(text, Text.translatable("fireclient.screen.module_select.header"), width/2, height/2 - (moduleSelectHeight/2 + 20), 0xFFFFFFFF);
    }

    public static void resetScroll() {
        scroll = 0;
    }
}
