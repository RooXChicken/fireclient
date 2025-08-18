package org.loveroo.fireclient.screen.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.screen.config.ModuleConfigScreen;
import org.lwjgl.glfw.GLFW;

public class KitPreviewScreen extends HandledScreen<PlayerScreenHandler> {

    private final Color color1 = Color.fromRGB(0xFF8B73);
    private final Color color2 = Color.fromRGB(0xE8C5BE);

    private final Text label;

    private boolean fromCommand = false;

    public KitPreviewScreen(PlayerEntity player, PlayerInventory inventory, String kitName, boolean fromCommand) {
        super(new PlayerScreenHandler(inventory, true, player), inventory, Text.of("Preview \"" + kitName + "\""));

        this.fromCommand = fromCommand;
        label = RooHelper.gradientText("Preview \"" + kitName + "\"", color1, color2);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(client.textRenderer, label, titleX - 5, titleY-15, 0xFFFFFFFF, true);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(fromCommand || keyCode != GLFW.GLFW_KEY_ESCAPE) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        var kit = FireClientside.getModule("kit");
        if(kit == null) {
            return true;
        }

        MinecraftClient.getInstance().setScreen(new ModuleConfigScreen(kit));
        return true;
    }
}
