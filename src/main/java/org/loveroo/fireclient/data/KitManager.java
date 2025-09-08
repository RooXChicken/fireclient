package org.loveroo.fireclient.data;

import java.io.File;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

import org.json.JSONObject;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.screen.modules.KitEditScreen;
import org.loveroo.fireclient.screen.modules.KitPreviewScreen;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Dynamic;

import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.visitor.StringNbtWriter;

public class KitManager {

    public static final String KIT_BASE_PATH = "fireclient/kits/";
    public static final String KIT_DELETED_PATH = "fireclient/kits/deleted/";

    private static final String DEFAULT_KIT = "{inv:[]}";

    private static final int KIT_MAX_SIZE = 102400;

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
        var client = MinecraftClient.getInstance();

        var nbt = new NbtCompound();
        nbt.put("data_version", NbtInt.of(SharedConstants.getGameVersion().getSaveVersion().getId()));
        nbt.put("mc_version", NbtString.of(SharedConstants.getGameVersion().getName()));
        nbt.put("creator", NbtString.of(client.player.getName().getString()));
        
        var kitNbt = new NbtList();
        var ops = MinecraftClient.getInstance().player.getRegistryManager().getOps(NbtOps.INSTANCE);

        var writeIndex = 0;
        for(var i = 0; i < inv.size(); i++) {
            var slot = new NbtCompound();
            var slotIndex = i;

            if(slotIndex >= PlayerInventory.MAIN_SIZE && slotIndex < PlayerInventory.OFF_HAND_SLOT) {
                slotIndex += 100-PlayerInventory.MAIN_SIZE;
            }
            else if(slotIndex == PlayerInventory.OFF_HAND_SLOT) {
                slotIndex = 150;
            }

            slot.putByte("Slot", (byte) slotIndex);

            var element = ItemStack.CODEC.encode(inv.getStack(i), ops, slot).result().orElse(null);
            if(element == null) {
                continue;
            }

            kitNbt.add(writeIndex++, element);
        }

        nbt.put("inv", kitNbt);

        var writer = new StringNbtWriter();
        return writer.apply(nbt);
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
        var nbt = NbtHelper.fromNbtProviderString(kit);

        var version = SharedConstants.getGameVersion().getSaveVersion().getId();
        var kitVersion = version;
        try {
            kitVersion = nbt.getInt("data_version");
        }
        catch(Exception ignored) { }

        var kitInventory = (NbtList)nbt.get("inv");

        var loadedInv = new PlayerInventory(client.player);
        loadedInv.clear();

        var ops = client.player.getRegistryManager().getOps(NbtOps.INSTANCE);

        for(var i = 0; i < kitInventory.size(); i++) {
            var itemNbt = kitInventory.getCompound(i);
            var slot = i;

            // store slot manually for compatibility
            if(itemNbt.contains("Slot")) {
                slot = itemNbt.getByte("Slot") & 255;

                // how mc does it :/
                if(slot >= 100 && slot < 150) {
                    slot -= 100 - PlayerInventory.MAIN_SIZE;
                }
                else if(slot >= 150) {
                    slot = PlayerInventory.OFF_HAND_SLOT;
                }
            }

            NbtElement fix;

            // data fixer upper!!
            if(version == kitVersion) {
                fix = itemNbt;
            }
            else {
                fix = client.getDataFixer().update(TypeReferences.ITEM_STACK, new Dynamic<NbtElement>(NbtOps.INSTANCE, itemNbt), kitVersion, version).getValue();
            }

            var item = ItemStack.CODEC.parse(ops, fix).result().orElse(ItemStack.EMPTY);
            loadedInv.setStack(slot, item);
        }

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
            var nbt = NbtHelper.fromNbtProviderString(kit);
            if(nbt.contains("inv")) {
                return KitValidationStatus.SUCCESS;
            }
        }
        catch(Exception ignored) { }

        return KitValidationStatus.INVALID_KIT;
    }

    public static List<String> getKits(HashSet<String> favorites) {
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

        kits.sort((kit1, kit2) -> {
            var favorited1 = favorites.contains(kit1);
            var favorited2 = favorites.contains(kit2);

            if((favorited1 && favorited2) || (!favorited1 && !favorited2)) {
                return kit1.compareTo(kit2);
            }
            else if(favorited1 &&! favorited2) {
                return -1;
            }
            else if(!favorited1 && favorited2) {
                return 1;
            }

            return 0;
        });
        
        return kits;
    }

    public static List<String> getKits() {
        return getKits(HashSet.newHashSet(0));
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

    public static void uploadKit(String kitName, String kitContents, Consumer<KitUploadStatus> onComplete) {
        if(kitStringStatus(kitContents) != KitValidationStatus.SUCCESS) {
            onComplete.accept(KitUploadStatus.INVALID_KIT);
            return;
        }

        var bytes = kitContents.getBytes();
        if(bytes.length > KIT_MAX_SIZE) {
            onComplete.accept(KitUploadStatus.TOO_LARGE);
            return;
        }

        var thread = new KitUploadThread(kitName, kitContents, onComplete);
        thread.start();
    }

    public static void downloadKit(String kitName, String kitId, Consumer<KitDownloadStatus> onComplete) {
        var thread = new KitDownloadThread(kitName, kitId, onComplete);
        thread.start();
    }

    private static final String SHARED_KIT_PREFIX = "__!fireclient_shared_kit_";

    public static String toSharedKit(String kitName, String kitId) {
        var result = "{}";

        try {
            var json = new JSONObject();

            json.put("name", kitName);
            json.put("id", kitId);

            result = json.toString();
        }
        catch(Exception e) { }

        return SHARED_KIT_PREFIX + result;
    }

    public static boolean isSharedKit(String message) {
        return message.contains(SHARED_KIT_PREFIX);
    }

    public static String getSharedKitSender(String message) {
        if(!isSharedKit(message)) {
            return "";
        }

        var split = message.split(SHARED_KIT_PREFIX);
        return split[0];
    }

    public static String getSharedKitId(String message) {
        return getSharedKitData(message, "id");
    }

    public static String getSharedKitName(String message) {
        return getSharedKitData(message, "name");
    }

    private static String getSharedKitData(String message, String data) {
        return getSharedKitJson(message).optString(data, "");
    }

    public static JSONObject getSharedKitJson(String message) {
        if(!isSharedKit(message)) {
            return new JSONObject();
        }

        return RooHelper.jsonFromStringSafe(message.split(SHARED_KIT_PREFIX)[1]);
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

    public enum KitUploadStatus {
        SUCCESS,
        INVALID_KIT,
        TOO_LARGE,
        FAILURE,
        RATE_LIMITED,
    }

    public enum KitDownloadStatus {
        SUCCESS,
        NO_KIT,
        ALREADY_EXISTS,
        INVALID_KIT,
        FAILURE,
        RATE_LIMITED,
    }

    static class KitUploadThread extends Thread {

        private final String kitName;
        private final String kitContents;

        private final Consumer<KitUploadStatus> onComplete;

        public KitUploadThread(String kitName, String kitContents, Consumer<KitUploadStatus> onComplete) {
            this.kitName = kitName;
            this.kitContents = kitContents;

            this.onComplete = onComplete;
        }

        @Override
        public void run() {
            try {
                var url = new URI(FireClient.getServerUrl("kit/upload")).toURL();

                var connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                var output = connection.getOutputStream();
                output.write(kitContents.getBytes());
                output.flush();
                output.close();

                var code = connection.getResponseCode();

                if(code == 429) {
                    FireClient.LOGGER.info("Failed to upload kit! Rate limited");

                    onComplete.accept(KitUploadStatus.RATE_LIMITED);
                    return;
                }

                var input = connection.getInputStream();
                var receivedData = new String(input.readAllBytes());
                input.close();

                switch(code) {
                    case 400 -> {
                        var status = KitUploadStatus.values()[Integer.parseInt(receivedData)];
                        FireClient.LOGGER.info("Failed to upload kit! {}", status);

                        onComplete.accept(status);
                        return;
                    }
                }

                onComplete.accept(KitUploadStatus.SUCCESS);
                RooHelper.sendChatMessage(KitManager.toSharedKit(kitName, receivedData));
            }
            catch(Exception e) {
                FireClient.LOGGER.error("Failed to upload kit!", e);

                onComplete.accept(KitUploadStatus.FAILURE);
            }
        }
    }

    static class KitDownloadThread extends Thread {

        private final String kitName;
        private final String kitId;

        private final Consumer<KitDownloadStatus> onComplete;

        public KitDownloadThread(String kitName, String kitId, Consumer<KitDownloadStatus> onComplete) {
            this.kitName = kitName;
            this.kitId = kitId;

            this.onComplete = onComplete;
        }

        @Override
        public void run() {
            if(KitManager.kitStatus(kitName) == KitValidationStatus.SUCCESS) {
                onComplete.accept(KitDownloadStatus.ALREADY_EXISTS);
                return;
            }

            try {
                var url = new URI(FireClient.getServerUrl("kit/download?id=" + kitId)).toURL();

                var connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                var code = connection.getResponseCode();

                if(code == 429) {
                    FireClient.LOGGER.info("Failed to download kit! Rate limited");

                    onComplete.accept(KitDownloadStatus.RATE_LIMITED);
                    return;
                }

                var input = connection.getInputStream();
                var receivedData = new String(input.readAllBytes());
                input.close();

                switch(code) {
                    case 400 -> {
                        var status = KitDownloadStatus.values()[Integer.parseInt(receivedData)];
                        FireClient.LOGGER.info("Failed to download kit! {}", status);

                        onComplete.accept(status);
                        return;
                    }
                }

                var createStatus = KitManager.createKit(kitName, receivedData);

                switch(createStatus) {
                    case SUCCESS -> { }

                    case ALREADY_EXISTS -> {
                        onComplete.accept(KitDownloadStatus.ALREADY_EXISTS);
                        return;
                    }
                    case INVALID_KIT -> {
                        onComplete.accept(KitDownloadStatus.INVALID_KIT);
                        return;
                    }

                    default -> {
                        onComplete.accept(KitDownloadStatus.FAILURE);
                        return;
                    }
                }

                onComplete.accept(KitDownloadStatus.SUCCESS);
            }
            catch(Exception e) {
                FireClient.LOGGER.error("Failed to download kit!", e);
                onComplete.accept(KitDownloadStatus.FAILURE);
            }
        }
    }
}