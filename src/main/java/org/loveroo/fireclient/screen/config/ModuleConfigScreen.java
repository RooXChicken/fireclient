package org.loveroo.fireclient.screen.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.loveroo.fireclient.modules.ModuleBase;
import org.loveroo.fireclient.screen.base.ConfigScreenBase;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

public class ModuleConfigScreen extends ConfigScreenBase {

    private final List<ModuleBase> modules;
    private final Text about;

    private boolean reloading = false;

    @Nullable
    private ModuleBase selectedModule = null;
    private ModuleBase.OldTransform oldTransform = null;

    public ModuleConfigScreen(ModuleBase module) {
        this(module.getData().getShownName(), module.getData().getDescription(), List.of(module));
    }

    public ModuleConfigScreen(Text title, Text about, List<ModuleBase> module) {
        super(Text.translatable("fireclient.module.generic.config_text", title));

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
        var widgets = new ArrayList<ClickableWidget>();

        for(var module : modules) {
            widgets.addAll(module.getConfigScreen(this));
        }

        for(var widget : widgets) {
            addDrawableChild(widget);
        }

        addDrawableChild(ButtonWidget.builder(Text.translatable("fireclient.module.generic.back.name"), (button) -> escapePressed())
                .dimensions(width - 85, height - 25, 80, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.generic.back.tooltip")))
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
        MinecraftClient.getInstance().setScreen(new ModuleSelectScreen());
        return true;
    }

    @Override
    public void onFilesDropped(List<Path> paths) {
        for(var module : modules) {
            module.onFilesDropped(paths);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if(selectedModule != null) {
            selectedModule.handleTransformation(mouseState, oldTransform, this.mouseX, this.mouseY, oldMouseX, oldMouseY, doSnap());
        }

        for(var module : modules) {
            module.drawOutline(context);
            module.drawScreen(this, context, delta);

            module.setDrawingOverwritten(false);
            module.draw(context, RenderTickCounter.ZERO);
            module.setDrawingOverwritten(true);
        }

        for(var module : modules) {
            if(module.isPointInside(mouseX, mouseY)) {
                setTooltip(module.getData().getTooltip(showTransform()));
                break;
            }
        }
    }
}
