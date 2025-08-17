package org.loveroo.fireclient.screen.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.loveroo.fireclient.modules.ModuleBase;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ModuleConfigScreen extends ConfigScreenBase {

    private final List<ModuleBase> modules;
    private final String about;

    @Nullable
    private ModuleBase selectedModule = null;
    private ModuleBase.OldTransform oldTransform = null;

    public ModuleConfigScreen(ModuleBase module) {
        this(module.getData().getName(), module.getData().getDescription(), List.of(module));
    }

    public ModuleConfigScreen(String title, String about, List<ModuleBase> module) {
        super(Text.of( title + " Config"));

        this.modules = module;
        this.about = about;

        openScreen();
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

        addDrawableChild(ButtonWidget.builder(Text.of("About"), (button) -> {})
                .dimensions(width - 125, height - 25, 120, 20)
                .tooltip(Tooltip.of(Text.of(about)))
                .build());
    }

    @Override
    public void close() {
        closeModule();
        super.close();
    }

    private void closeModule() {
        for(var module : modules) {
            module.setDrawingOverwritten(false);
            module.closeScreen(this);
        }
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
        closeModule();

        MinecraftClient.getInstance().setScreen(new ModuleSelectScreen());
        return true;
    }

    @Override
    protected void exitOnInventory() {
        for(var widget : children()) {
            if(widget instanceof TextFieldWidget text && text.isSelected()) {
                return;
            }
        }

        closeModule();
        super.exitOnInventory();
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
            module.drawScreen(this, context);

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
