package org.loveroo.fireclient.screen.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.EntityEquipment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.screen.config.ModuleConfigScreen;
import org.lwjgl.glfw.GLFW;

public abstract class KitViewScreen extends HandledScreen<PlayerScreenHandler> {

    private final Color color1 = Color.fromRGB(0xFF8B73);
    private final Color color2 = Color.fromRGB(0xE8C5BE);

    private final Text label;
    protected final String kitName;

    private boolean fromCommand = false;

    public KitViewScreen(PlayerEntity player, PlayerInventory inventory, Text labelText, String kitName, boolean fromCommand) {
        super(new PlayerScreenHandler(inventory, false, player), inventory, labelText);

        this.fromCommand = fromCommand;
        this.kitName = kitName;

        label = RooHelper.gradientText(labelText.getString(), color1, color2);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(MinecraftClient.getInstance().textRenderer, label, titleX - 6, titleY-15, 0xFFFFFFFF, true);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode != GLFW.GLFW_KEY_ESCAPE) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        if(!fromCommand) {
            exitToKit();
        }
        else {
            close();
        }

        return true;
    }

    @Override
    public void close() {
        onClose();
        super.close();
    }

    protected void exitToKit() {
        var kit = FireClientside.getModule("kit");
        if(kit == null) {
            close();
            return;
        }

        onClose();
        MinecraftClient.getInstance().setScreen(new ModuleConfigScreen(kit));
    }

    protected PlayerInventory getInventory() {
        var inv = new PlayerInventory(MinecraftClient.getInstance().player, new EntityEquipment());

        var slots = getScreenHandler().slots;
        for(var slot : slots) {
            inv.setStack(slot.getIndex(), slot.getStack());
        }

        return inv;
    }

    protected boolean isFromCommand() {
        return fromCommand;
    }

    protected void onClose() { }
}
