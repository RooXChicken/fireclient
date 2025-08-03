package org.loveroo.fireclient.screen.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.ModuleBase;

public class MainConfigScreen extends ConfigScreenBase {

    private ButtonWidget modulesButton;
    private ButtonWidget settingsButton;

    private ModuleBase selectedModule = null;

    public MainConfigScreen() {
        super(Text.of("FireClient Main Config"));
    }

    @Override
    public void init() {
        modulesButton = ButtonWidget.builder(Text.of("Modules"), this::modulesButtonPressed)
                .dimensions(width/2 - 50, height/2 - 10, 100, 20)
                .build();

        settingsButton = ButtonWidget.builder(Text.of("FireClient Settings"), this::settingsButtonPressed)
                .dimensions(width/2 - 60, height/2 + 20, 120, 20)
                .build();

        addSelectableChild(modulesButton);
        addSelectableChild(settingsButton);
    }

    private void modulesButtonPressed(ButtonWidget button) {
        MinecraftClient.getInstance().setScreen(new ModuleSelectScreen());
    }

    private void settingsButtonPressed(ButtonWidget button) {
        MinecraftClient.getInstance().setScreen(new FireClientSettingsScreen());

    }

    @Override
    public void tick() {

    }

    @Override
    protected void handleClick() {
        if(mouseState == -1) {
            selectedModule = null;
        }
        else if(mouseState == 0 || mouseState == 1) {
            for(var module : FireClientside.getModules()) {
                if(module.getData().isSkip()) {
                    continue;
                }

                if(module.isPointInside(mouseX, mouseY)) {
                    selectedModule = module;
                    break;
                }
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if(selectedModule != null) {
            selectedModule.handleTransformation(mouseState, this.mouseX, this.mouseY, oldMouseX, oldMouseY);
        }

        for(var module : FireClientside.getModules()) {
            if(module.getData().isSkip()) {
                continue;
            }

            module.drawOutline(context);
        }

        var tooltip = "";
        for(var module : FireClientside.getModules()) {
            if(module.isPointInside(mouseX, mouseY)) {
                tooltip = module.getData().getName();
                break;
            }
        }

        setTooltip(Text.of(tooltip));

        modulesButton.render(context, mouseX, mouseY, delta);
        settingsButton.render(context, mouseX, mouseY, delta);
    }
}
