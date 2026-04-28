package org.loveroo.fireclient.screen.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.loveroo.fireclient.modules.ModuleBase;
import org.loveroo.fireclient.screen.base.ConfigScreenBase;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.DeltaTracker;
import net.minecraft.network.chat.Component;

public class ModuleConfigScreen extends ConfigScreenBase {

    private final List<ModuleBase> modules;
    private final Component about;

    private boolean reloading = false;

    @Nullable
    private ModuleBase selectedModule = null;

    private ModuleBase.OldTransform oldTransform = null;

    public ModuleConfigScreen(ModuleBase module) {
        this(module.getData().getShownName(), module.getData().getDescription(), List.of(module));
    }

    public ModuleConfigScreen(Component title, Component about, List<ModuleBase> module) {
        super(Component.translatable("fireclient.module.generic.config_text", title));

        this.modules = module;
        this.about = about;
    }

    private void openScreen() {
        for(var module : modules) {
            module.openScreen(this);
        }
    }

    @Override
    public void init() {
        var widgets = new ArrayList<AbstractWidget>();

        for(var module : modules) {
            widgets.addAll(module.getConfigScreen(this));
        }

        for(var widget : widgets) {
            addRenderableWidget(widget);
        }

        addRenderableWidget(Button.builder(Component.translatable("fireclient.module.generic.back.name"), (button) -> escapePressed())
                .bounds(width - 85, height - 25, 80, 20)
                .tooltip(Tooltip.create(Component.translatable("fireclient.module.generic.back.tooltip")))
                .build());

        openScreen();
    }

    @Override
    protected void onExit() {
        if(reloading) {
            return;
        }

        for(var module : modules) {
            module.setDrawingOverwritten(false);
            module.closeScreen(this);
        }
    }

    public void setReloading() {
        reloading = true;
    }

    @Override
    protected void handleClick() {
        if(mouseState == -1) {
            selectedModule = null;
        }
        else if(mouseState == 0 || mouseState == 1) {
            for(var module : modules) {
                if(module.isPointInside(mouseX, mouseY)) {
                    oldTransform = module.getTransform();
                    selectedModule = module;
                    break;
                }
            }
        }
    }

    @Override
    protected boolean escapePressed() {
        onExit();
        Minecraft.getInstance().setScreen(new ModuleSelectScreen());
        return true;
    }

    @Override
    public void onFilesDrop(List<Path> paths) {
        for(var module : modules) {
            module.onFilesDropped(paths);
        }
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        if(selectedModule != null) {
            selectedModule.handleTransformation(mouseState, oldTransform, this.mouseX, this.mouseY, oldMouseX, oldMouseY, doSnap());
        }

        for(var module : modules) {
            module.drawOutline(graphics);
            module.drawScreen(this, graphics, delta);

            module.setDrawingOverwritten(false);
            module.draw(graphics, DeltaTracker.ZERO);
            module.setDrawingOverwritten(true);
        }

        for(var module : modules) {
            if(module.isPointInside(mouseX, mouseY)) {
                graphics.setTooltipForNextFrame(Tooltip.splitTooltip(minecraft, (module.getData().getTooltip(showTransform()))), mouseX, mouseY);
                break;
            }
        }
    }
}
