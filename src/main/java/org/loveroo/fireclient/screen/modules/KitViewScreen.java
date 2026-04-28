package org.loveroo.fireclient.screen.modules;

import org.jspecify.annotations.NonNull;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.screen.config.ModuleConfigScreen;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.network.chat.Component;

public abstract class KitViewScreen extends AbstractContainerScreen<InventoryMenu> {

    private final Color color1 = Color.fromRGB(0xFF8B73);
    private final Color color2 = Color.fromRGB(0xE8C5BE);

    private final Component label;
    protected final String kitName;

    private boolean fromCommand = false;

    public KitViewScreen(Player player, Inventory inventory, Component labelText, String kitName, boolean fromCommand) {
        super(new InventoryMenu(inventory, false, player), inventory, labelText);

        this.fromCommand = fromCommand;
        this.kitName = kitName;

        label = RooHelper.gradientText(labelText.getString(), color1, color2);
    }

    // TODO: idk
//    @Override
//    protected void extractBackground(GuiGraphicsExtractor graphics, float delta, int mouseX, int mouseY) {
//        graphics.blit(RenderPipelines.GUI_TEXTURED, INVENTORY_LOCATION, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
//    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        extractTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(Minecraft.getInstance().font, label, titleLabelX - 6, titleLabelY -15, 0xFFFFFFFF, true);
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if(input.key() != GLFW.GLFW_KEY_ESCAPE) {
            return super.keyPressed(input);
        }

        if(!fromCommand) {
            exitToKit();
        }
        else {
            onClose();
        }

        return true;
    }

    @Override
    public void onClose() {
        onOnClose();
        super.onClose();
    }

    protected void exitToKit() {
        var kit = FireClientside.getModule("kit");
        if(kit == null) {
            onClose();
            return;
        }

        onOnClose();
        Minecraft.getInstance().setScreen(new ModuleConfigScreen(kit));
    }

    protected Inventory getInventory() {
        var inv = new Inventory(Minecraft.getInstance().player, new EntityEquipment());

        var slots = getMenu().slots;
        for(var slot : slots) {
            inv.setItem(slot.getContainerSlot(), slot.getItem());
        }

        return inv;
    }

    protected boolean isFromCommand() {
        return fromCommand;
    }

    protected void onOnClose() { }
}
