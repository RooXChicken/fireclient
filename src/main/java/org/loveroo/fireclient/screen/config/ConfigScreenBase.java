package org.loveroo.fireclient.screen.config;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.lwjgl.glfw.GLFW;

public class ConfigScreenBase extends Screen {

    private final MutableText configText = RooHelper.gradientText("FireClient Config", FireClientside.mainColor1, FireClientside.mainColor2);
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
        if(keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if(escapePressed()) {
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    protected boolean escapePressed() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        this.oldMouseX = this.mouseX;
        this.oldMouseY = this.mouseY;

        this.mouseX = mouseX;
        this.mouseY = mouseY;

        var text = MinecraftClient.getInstance().textRenderer;
        context.drawCenteredTextWithShadow(text, configText, width/2, 10, 0xFFFFFFFF);
    }
}
