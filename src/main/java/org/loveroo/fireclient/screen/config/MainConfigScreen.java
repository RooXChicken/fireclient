package org.loveroo.fireclient.screen.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.loveroo.fireclient.modules.ModuleBase;
import org.loveroo.fireclient.screen.base.ConfigScreenBase;

public class MainConfigScreen extends ConfigScreenBase {

    private ButtonWidget modulesButton;
    private ButtonWidget settingsButton;

    private ModuleBase selectedModule = null;
    private ModuleBase.OldTransform oldTransform = null;

    public MainConfigScreen() {
        super(Text.translatable("fireclient.screen.main_config.title"));
    }

    @Override
    public void init() {
        for(var module : FireClientside.getModules()) {
            module.setDrawingOverwritten(true);
        }

        modulesButton = ButtonWidget.builder(Text.translatable("fireclient.screen.main_config.modules.name"), this::modulesButtonPressed)
                .dimensions(width/2 - 50, height/2 - 10, 100, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.screen.main_config.modules.tooltip")))
                .build();

        settingsButton = ButtonWidget.builder(Text.translatable("fireclient.screen.main_config.settings.name"), this::settingsButtonPressed)
                .dimensions(width/2 - 60, height/2 + 20, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.screen.main_config.settings.tooltip")))
                .build();

        addDrawableChild(modulesButton);
        addDrawableChild(settingsButton);
    }

    private void modulesButtonPressed(ButtonWidget button) {
        removeOverwrite();

        ModuleSelectScreen.resetScroll();
        MinecraftClient.getInstance().setScreen(new ModuleSelectScreen());
    }

    private void settingsButtonPressed(ButtonWidget button) {
        removeOverwrite();
        MinecraftClient.getInstance().setScreen(new FireClientSettingsScreen());
    }

    @Override
    protected void handleClick() {
        if(mouseState == -1) {
            selectedModule = null;
        }
        else if(mouseState == 0 || mouseState == 1) {
            for(var module : FireClientside.getModules()) {
                if(!module.getData().isGuiElement()) {
                    continue;
                }

                if(!module.getData().isVisible() && FireClientside.getSetting(FireClientOption.SHOW_HIDDEN_MODULES) == 0) {
                    continue;
                }

                if(module.isPointInside(mouseX, mouseY)) {
                    oldTransform = module.getTransform();
                    selectedModule = module;
                    break;
                }
            }
        }
    }

    @Override
    public void close() {
        removeOverwrite();
        super.close();
    }

    private void removeOverwrite() {
        for(var module : FireClientside.getModules()) {
            module.setDrawingOverwritten(false);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if(selectedModule != null) {
            selectedModule.handleTransformation(mouseState, oldTransform, this.mouseX, this.mouseY, oldMouseX, oldMouseY, doSnap());
        }

        for(var module : FireClientside.getModules()) {
            if(!module.getData().isGuiElement()) {
                continue;
            }

            if(!module.getData().isVisible() && FireClientside.getSetting(FireClientOption.SHOW_HIDDEN_MODULES) == 0) {
                continue;
            }

            module.drawOutline(context);

            module.setDrawingOverwritten(false);
            module.draw(context, RenderTickCounter.ZERO);
            module.setDrawingOverwritten(true);
        }

        for(var module : FireClientside.getModules()) {
            if(!module.getData().isVisible() && FireClientside.getSetting(FireClientOption.SHOW_HIDDEN_MODULES) == 0) {
                continue;
            }

            if(module.isPointInside(mouseX, mouseY)) {
                setTooltip(module.getData().getTooltip(showTransform()));
                break;
            }
        }

        renderSnapTutorial(context);
    }
}
