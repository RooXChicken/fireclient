package org.loveroo.fireclient.screen.modules;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class KitPreviewScreen extends KitViewScreen {

    public KitPreviewScreen(PlayerEntity player, PlayerInventory inventory, String kitName, boolean fromCommand) {
        super(player, inventory, Text.translatable("fireclient.screen.preview_kit.title", kitName), kitName, fromCommand);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == GLFW.GLFW_KEY_ESCAPE || client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        return true;
    }
}
