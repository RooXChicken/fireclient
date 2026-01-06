package org.loveroo.fireclient.screen.modules;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class KitPreviewScreen extends KitViewScreen {

    public KitPreviewScreen(PlayerEntity player, PlayerInventory inventory, String kitName, boolean fromCommand) {
        super(player, inventory, Text.translatable("fireclient.screen.preview_kit.title", kitName), kitName, fromCommand);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        return false;
    }

    @Override
    public boolean mouseReleased(Click click) {
        return false;
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        return false;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if(input.key() == GLFW.GLFW_KEY_ESCAPE || client.options.inventoryKey.matchesKey(input)) {
            return super.keyPressed(input);
        }

        return true;
    }
}
