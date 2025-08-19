package org.loveroo.fireclient.data;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import org.json.JSONObject;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.screen.modules.KitEditScreen;
import org.loveroo.fireclient.screen.modules.KitPreviewScreen;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class KitManager {

    public static final String KIT_BASE_PATH = "fireclient/kits/";
    public static final String KIT_DELETED_PATH = "fireclient/kits/deleted/";

    private static final String DEFAULT_KIT = "{\"inv\":[]}";

    public static String getKitPath(String kitName) {
        return KIT_BASE_PATH + kitName + ".json";
    }

    public static String getDeletedKitPath(String kitName) {
        return KIT_DELETED_PATH + kitName + ".json";
    }

    public static KitManageStatus initializeDirectories() {
        try {
            new File(KIT_BASE_PATH).mkdirs();
            new File(KIT_DELETED_PATH).mkdirs();

            return KitManageStatus.SUCCESS;
        }
        catch(Exception e) {
            FireClient.LOGGER.error("Failed to initialize kit directories!", e);
        }

        return KitManageStatus.FAILURE;
    }

    public static KitManageStatus deleteRecycledKits() {
        try {
            var deletedFolder = new File(KIT_DELETED_PATH);
            deletedFolder.mkdirs();

            for(var file : deletedFolder.listFiles()) {
                if(file == null) {
                    continue;
                }

                file.delete();
            }

            return KitManageStatus.SUCCESS;
        }
        catch(Exception e) {
            FireClient.LOGGER.error("Failed to delete reycled kits!", e);
        }

        return KitManageStatus.FAILURE;
    }

    public static KitCreateStatus createKit(String kitName, String kitContents) {
        var validationStatus = kitStringStatus(kitContents);

        if(validationStatus != KitValidationStatus.SUCCESS) {
            FireClient.LOGGER.error("Failed to create kit {}! Error type: {}", kitName, validationStatus.name());
            return KitCreateStatus.INVALID_KIT;
        }

        if(kitStatus(kitName) == KitValidationStatus.SUCCESS) {
            return KitCreateStatus.ALREADY_EXISTS;
        }

        try {
            var writer = new FileWriter(getKitPath(kitName));

            writer.write(kitContents);
            writer.close();

            return KitCreateStatus.SUCCESS;
        }
        catch(Exception e) {
            FireClient.LOGGER.error("Failed to save kit {}!", kitName, e);
            return KitCreateStatus.WRITE_FAIL;
        }
    }

    // saving and loading

    public static String getPlayerInventoryString() {
        var client = MinecraftClient.getInstance();
        if(client.player == null) {
            return DEFAULT_KIT;
        }

        return getInventoryAsString(client.player.getInventory());
    }

    public static String getInventoryAsString(PlayerInventory inv) {
        var invNbt = inv.writeNbt(new NbtList());
        return "{\"inv\":" + invNbt.asString() + "}";
    }

    public static KitLoadStatus loadKit(String kitName) {
        var kit = getKitFromFile(new File(getKitPath(kitName)));
        return loadKitFromString(kit);
    }

    public static KitLoadStatus loadKitFromString(String kit) {
        if(kitStringStatus(kit) != KitValidationStatus.SUCCESS) {
            return KitLoadStatus.INVALID_KIT;
        }

        var client = MinecraftClient.getInstance();
        if(client.player == null) {
            return KitLoadStatus.INVALID_PLAYER;
        }

        if(!client.player.isInCreativeMode()) {
            if(client.player.getPermissionLevel() >= 2) {
                return KitLoadStatus.NEEDS_GMC;
            }

            return KitLoadStatus.INVALID_PERMS;
        }

        try {
            var loadedInv = getInventoryFromKit(kit);
            var playerInv = client.player.getInventory();

            var slots = client.player.playerScreenHandler.slots;

            for(var i = 0; i < slots.size(); i++) {
                var slot = slots.get(i);
                if(slot.inventory != playerInv) {
                    continue;
                }

                var item = loadedInv.getStack(slot.getIndex());

                playerInv.setStack(slot.getIndex(), item);
                client.interactionManager.clickCreativeStack(item, i);
            }

            client.player.playerScreenHandler.sendContentUpdates();
        }
        catch(Exception e) {
            FireClient.LOGGER.error("Failed to load kit!", e);
            return KitLoadStatus.INVALID_KIT;
        }

        return KitLoadStatus.SUCCESS;
    }

    private static PlayerInventory getInventoryFromKit(String kit) throws CommandSyntaxException {
        var client = MinecraftClient.getInstance();

        var from = NbtHelper.fromNbtProviderString(kit);
        var invList = (NbtList)from.get("inv");

        var loadedInv = new PlayerInventory(client.player);
        loadedInv.readNbt(invList);

        return loadedInv;
    }

    public static KitViewStatus previewKit(String kitName, boolean fromCommand) {
        var kit = getKitFromFile(new File(getKitPath(kitName)));
        return previewKitFromString(kitName, kit, fromCommand);
    }

    public static KitViewStatus previewKitFromString(String kitName, String kit, boolean fromCommand) {
        if(kitStringStatus(kit) != KitValidationStatus.SUCCESS) {
            return KitViewStatus.INVALID_KIT;
        }

        var client = MinecraftClient.getInstance();
        if(client.player == null) {
            return KitViewStatus.INVALID_PLAYER;
        }

        try {
            var loadedInv = getInventoryFromKit(kit);
            client.send(() -> client.setScreen(new KitPreviewScreen(client.player, loadedInv, kitName, fromCommand)));
        }
        catch(Exception e) {
            FireClient.LOGGER.info("Failed to preview kit!", e);
            return KitViewStatus.INVALID_KIT;
        }

        return KitViewStatus.SUCCESS;
    }

    public static KitViewStatus editKit(String kitName, boolean fromCommand) {
        var kit = getKitFromFile(new File(getKitPath(kitName)));
        return editKitFromString(kitName, kit, fromCommand);
    }

    public static KitViewStatus editKitFromString(String kitName, String kit, boolean fromCommand) {
        if(kitStringStatus(kit) != KitValidationStatus.SUCCESS) {
            return KitViewStatus.INVALID_KIT;
        }

        var client = MinecraftClient.getInstance();
        if(client.player == null) {
            return KitViewStatus.INVALID_PLAYER;
        }

        try {
            var loadedInv = getInventoryFromKit(kit);
            client.send(() -> client.setScreen(new KitEditScreen(client.player, loadedInv, kitName, fromCommand)));
        }
        catch(Exception e) {
            FireClient.LOGGER.info("Failed to edit kit!", e);
            return KitViewStatus.INVALID_KIT;
        }

        return KitViewStatus.SUCCESS;
    }

    // file manager

    public static String getKitFromName(String kitName) {
        return getKitFromFile(new File(getKitPath(kitName)));
    }

    public static String getKitFromFile(File file) {
        try {
            if(file.exists()) {
                return Files.readString(Paths.get(file.toURI()));
            }
        }
        catch(Exception e) {
            FireClient.LOGGER.error("Failed to load kit from file!", e);
        }

        return "";
    }

    public static KitValidationStatus kitStatus(String kitName) {
        return kitStatus(new File(getKitPath(kitName)));
    }

    public static KitValidationStatus kitStatus(File file) {
        if(!file.exists()) {
            return KitValidationStatus.NO_FILE;
        }

        var kit = getKitFromFile(file);
        return kitStringStatus(kit);
    }

    public static KitValidationStatus kitStringStatus(String kit) {
        if(kit.isEmpty()) {
            return KitValidationStatus.INVALID_KIT;
        }

        try {
            var json = new JSONObject(kit);
            if(json.optJSONArray("inv") != null) {
                return KitValidationStatus.SUCCESS;
            }
        }
        catch(Exception ignored) { }

        return KitValidationStatus.INVALID_KIT;
    }

    public static List<String> getKits() {
        var kits = new ArrayList<String>();

        try {
            initializeDirectories();

            var kitsFolder = new File(KIT_BASE_PATH);

            for(var kitFile : kitsFolder.listFiles()) {
                var kit = kitFile.getName();
                if(!kit.endsWith(".json") || kitStatus(kitFile) != KitValidationStatus.SUCCESS) {
                    continue;
                }

                var kitName = kit.substring(0, kit.length() - 5);
                kits.add(kitName);
            }
        }
        catch(Exception e) {
            FireClient.LOGGER.error("Failed to get kits!", e);
        }

        kits.sort(String::compareTo);
        return kits;
    }

    public static KitManageStatus deleteKit(String kitName) {
        try {
            new File(KIT_DELETED_PATH).mkdirs();
            new File(getKitPath(kitName)).renameTo(new File(getDeletedKitPath(kitName)));

            return KitManageStatus.SUCCESS;
        }
        catch(Exception e) {
            FireClient.LOGGER.error("Failed to delete kit {}!", kitName, e);
        }

        return KitManageStatus.FAILURE;
    }

    public enum KitCreateStatus {
        SUCCESS,
        ALREADY_EXISTS,
        INVALID_KIT,
        WRITE_FAIL,
    }

    public enum KitLoadStatus {
        SUCCESS,
        INVALID_PLAYER,
        INVALID_PERMS,
        NEEDS_GMC,
        INVALID_KIT,
    }

    public enum KitManageStatus {
        SUCCESS,
        FAILURE,
    }

    public enum KitValidationStatus {
        SUCCESS,
        NO_FILE,
        INVALID_KIT,
    }

    public enum KitViewStatus {
        SUCCESS,
        INVALID_PLAYER,
        INVALID_KIT
    }
}