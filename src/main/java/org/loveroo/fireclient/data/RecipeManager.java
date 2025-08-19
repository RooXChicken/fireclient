package org.loveroo.fireclient.data;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.Registries;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.keybind.Key;
import org.loveroo.fireclient.keybind.Keybind;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.List;

public class RecipeManager {

//    private static final HashMap<ScreenHandlerType<?>, NetworkRecipeId> lastRecipes = new HashMap<>();
//
//    public RecipeManager() {
////        FireClientside.getKeybindManager().registerKeybind(
////                new Keybind("use_recipe_autofill", Text.of("Use"), Text.of("Use Autofill Recipe"), false, List.of(new Key(GLFW.GLFW_KEY_Y, Key.KeyType.KEY_CODE)),
////                        RecipeManager::fillRecipe, null)
////        );
//    }
//
//    public static void fillRecipe() {
//        var client = MinecraftClient.getInstance();
//        if(client.player == null || client.interactionManager == null || !(client.currentScreen instanceof HandledScreen<?> screen)) {
//            return;
//        }
//
//        // TODO: find a way to detect when recipes are crafted and store their recipe,
//        // TODO: then load that recipe using interactionManager#clickreicpe when they press a key
//
////        var handler = screen.getScreenHandler();
////        handler.
//
////        var recipe = lastRecipes.get(client.player.playerScreenHandler.getType());
////        if(recipe == null) {
////            return;
////        }
//
//        client.interactionManager.clickRecipe(client.player.currentScreenHandler.syncId, new NetworkRecipeId(10), Screen.hasShiftDown());
//    }
}
