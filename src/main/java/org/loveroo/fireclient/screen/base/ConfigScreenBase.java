package org.loveroo.fireclient.screen.base;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.lwjgl.glfw.GLFW;

public class ConfigScreenBase extends Screen {

    protected int mouseState = 0;

    protected int mouseX = 0;
    protected int mouseY = 0;

    protected int oldMouseY = 0;
    protected int oldMouseX = 0;

    protected ConfigScreenBase(Text title) {
        super(title);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        mouseState = button;

        this.oldMouseX = this.mouseX;
        this.oldMouseY = this.mouseY;

        handleClick();

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        mouseState = -1;
        handleClick();

        FireClientside.saveConfig();

        return super.mouseReleased(mouseX, mouseY, button);
    }

    protected void handleClick() { }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            exitOnInventory();
        }

        if(keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if(escapePressed()) {
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    protected void exitOnInventory() {
        client.setScreen(new InventoryScreen(client.player));
    }

    protected boolean escapePressed() {
        return false;
    }

    protected boolean doSnap() {
        return (GLFW.glfwGetKey(client.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS);
    }

    protected boolean showTransform() {
        return (GLFW.glfwGetKey(client.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        this.mouseX = mouseX;
        this.mouseY = mouseY;

        var text = MinecraftClient.getInstance().textRenderer;

        var configText = RooHelper.gradientText(Text.translatable("fireclient.screen.generic.header").getString(), FireClientside.mainColor1, FireClientside.mainColor2);
        context.drawCenteredTextWithShadow(text, configText, width/2, 10, 0xFFFFFFFF);
    }

    protected void renderSnapTutorial(DrawContext context) {
        if(FireClientside.getSetting(FireClientOption.SHOW_TUTORIAL_TEXT) == 0) {
            return;
        }

        var tutorialText = RooHelper.gradientText(Text.translatable("fireclient.screen.main_config.tutorial").getString(), FireClientside.mainColor1, FireClientside.mainColor2);
        context.drawText(textRenderer, tutorialText, 2, height-10, 0xFFFFFFFF, true);
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }
}
