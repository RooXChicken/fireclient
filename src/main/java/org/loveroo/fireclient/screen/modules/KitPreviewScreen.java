package org.loveroo.fireclient.screen.modules;

import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;

public class KitPreviewScreen extends KitViewScreen {

    public KitPreviewScreen(Player player, Inventory inventory, String kitName, boolean fromCommand) {
        super(player, inventory, Component.translatable("fireclient.screen.preview_kit.title", kitName), kitName, fromCommand);
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent click, boolean doubled) {
        return false;
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent click) {
        return false;
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent click, double offsetX, double offsetY) {
        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if(input.key() == GLFW.GLFW_KEY_ESCAPE || minecraft.options.keyInventory.matches(input)) {
            return super.keyPressed(input);
        }

        return true;
    }
}
