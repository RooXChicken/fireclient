package org.loveroo.fireclient.screen.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.ModuleBase;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ModuleConfigScreen extends ConfigScreenBase {

    private final ModuleBase module;
    private boolean moduleSelected = false;

    public ModuleConfigScreen(ModuleBase module) {
        super(Text.of(module.getData().getName() + " Config"));

        this.module = module;
        this.module.openScreen(this);
    }

    @Override
    public void init() {
        var widgets = module.getConfigScreen(this);

        for(var widget : widgets) {
            addDrawableChild(widget);
        }
    }

    @Override
    public void close() {
        this.module.closeScreen(this);
        super.close();
    }

    @Override
    protected void handleClick() {
        if(mouseState == -1) {
            moduleSelected = false;
        }
        else if(mouseState == 0 || mouseState == 1) {
            moduleSelected = module.isPointInside(mouseX, mouseY);
        }
    }

    @Override
    protected boolean escapePressed() {
        this.module.closeScreen(this);

        MinecraftClient.getInstance().setScreen(new ModuleSelectScreen());
        return true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if(moduleSelected) {
            module.handleTransformation(mouseState, this.mouseX, this.mouseY, oldMouseX, oldMouseY);
        }

        module.drawScreen(this, context);
        module.drawOutline(context);

        
//        var tooltip = "";
//        if(module.isPointInside(this.mouseX, this.mouseY)) {
//            tooltip = module.getData().getName();
//        }

//        setTooltip(Text.of(tooltip));
    }
}
