package org.loveroo.fireclient.screen.base;

import org.jspecify.annotations.NonNull;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;

public class ConfigScreenBase extends Screen {

    protected int mouseState = 0;

    protected int mouseX = 0;
    protected int mouseY = 0;

    protected int oldMouseY = 0;
    protected int oldMouseX = 0;

    protected ConfigScreenBase(Component title) {
        super(title);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        mouseState = click.button();

        this.oldMouseX = this.mouseX;
        this.oldMouseY = this.mouseY;

        handleClick();

        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent click) {
        mouseState = -1;
        handleClick();

        FireClientside.saveConfig();

        return super.mouseReleased(click);
    }

    protected void handleClick() { }

    @Override
    public void removed() {
        onExit();
    }

    protected void onExit() { }

    @Override
    public boolean keyPressed(@NonNull KeyEvent input) {
        if(minecraft.options.keyInventory.matches(input)) {
            exitOnInventory();
        }

        if(input.key() == GLFW.GLFW_KEY_ESCAPE) {
            if(escapePressed()) {
                return true;
            }
        }

        return super.keyPressed(input);
    }

    protected void exitOnInventory() {
        if(minecraft.player == null || getFocused() instanceof EditBox) {
            return;
        }

        if(getFocused() instanceof ScrollableWidget scroll) {
            if(scroll.getFocused().getFocused() instanceof EditBox) {
                return;
            }
        }

        minecraft.setScreen(new InventoryScreen(minecraft.player));
    }

    protected boolean escapePressed() {
        return false;
    }

    protected boolean doSnap() {
        return (GLFW.glfwGetKey(minecraft.getWindow().handle(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS);
    }

    protected boolean showTransform() {
        return (GLFW.glfwGetKey(minecraft.getWindow().handle(), GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        this.mouseX = mouseX;
        this.mouseY = mouseY;

        var text = Minecraft.getInstance().font;

        var configText = RooHelper.gradientText(Component.translatable("fireclient.screen.generic.header").getString(), FireClientside.mainColor1, FireClientside.mainColor2);
        graphics.centeredText(text, configText, width/2, 10, 0xFFFFFFFF);
    }

    protected void renderTutorialText(GuiGraphicsExtractor context, Component text) {
        if(FireClientside.getSetting(FireClientOption.SHOW_TUTORIAL_TEXT) == 0) {
            return;
        }

        var gradientText = RooHelper.gradientText(text.getString(), FireClientside.mainColor1, FireClientside.mainColor2);
        context.text(font, gradientText, 2, height-10, 0xFFFFFFFF, true);
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }
}
