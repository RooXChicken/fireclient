package org.loveroo.fireclient.screen.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.DeltaTracker;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.loveroo.fireclient.modules.ModuleBase;
import org.loveroo.fireclient.screen.base.ConfigScreenBase;

public class MainConfigScreen extends ConfigScreenBase {

    private ModuleBase selectedModule = null;
    private ModuleBase.OldTransform oldTransform = null;

    public MainConfigScreen() {
        super(Component.translatable("fireclient.screen.main_config.title"));
    }

    @Override
    public void init() {
        for(var module : FireClientside.getModules()) {
            module.setDrawingOverwritten(true);
        }

        addRenderableWidget(Button.builder(Component.translatable("fireclient.screen.main_config.modules.name"), this::modulesButtonPressed)
                .bounds(width/2 - 50, height/2 - 10, 100, 20)
                .tooltip(Tooltip.create(Component.translatable("fireclient.screen.main_config.modules.tooltip")))
                .build());

        addRenderableWidget(Button.builder(Component.translatable("fireclient.screen.main_config.settings.name"), this::settingsButtonPressed)
                .bounds(width/2 - 60, height/2 + 20, 120, 20)
                .tooltip(Tooltip.create(Component.translatable("fireclient.screen.main_config.settings.tooltip")))
                .build());

        addRenderableWidget(Button.builder(Component.translatable("fireclient.screen.main_config.exit.name"), this::exitButtonPressed)
                .bounds(width - 85, height - 25, 80, 20)
                .tooltip(Tooltip.create(Component.translatable("fireclient.screen.main_config.exit.tooltip")))
                .build());
    }

    private void modulesButtonPressed(Button button) {
        removeOverwrite();

        ModuleSelectScreen.resetScroll();
        Minecraft.getInstance().setScreen(new ModuleSelectScreen());
    }

    private void settingsButtonPressed(Button button) {
        removeOverwrite();
        Minecraft.getInstance().setScreen(new FireClientSettingsScreen());
    }

    private void exitButtonPressed(Button button) {
        removeOverwrite();
        Minecraft.getInstance().setScreen(null);
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
    public void onClose() {
        removeOverwrite();
        super.onClose();
    }

    private void removeOverwrite() {
        for(var module : FireClientside.getModules()) {
            module.setDrawingOverwritten(false);
        }
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

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

            module.drawOutline(graphics);

            module.setDrawingOverwritten(false);
            module.draw(graphics, DeltaTracker.ZERO);
            module.setDrawingOverwritten(true);
        }

        for(var module : FireClientside.getModules()) {
            if(!module.getData().isVisible() && FireClientside.getSetting(FireClientOption.SHOW_HIDDEN_MODULES) == 0) {
                continue;
            }

            if(module.isPointInside(mouseX, mouseY)) {
                graphics.setTooltipForNextFrame(Tooltip.splitTooltip(minecraft, module.getData().getTooltip(showTransform())), mouseX, mouseY);
                break;
            }
        }

        renderTutorialText(graphics, Component.translatable("fireclient.screen.main_config.tutorial"));
    }
}
