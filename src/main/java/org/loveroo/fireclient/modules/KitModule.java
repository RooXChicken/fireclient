package org.loveroo.fireclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.screen.config.ModuleConfigScreen;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

enum KitCreateStatus {
    SUCCESS,
    ALREADY_EXISTS,
    INVALID_KIT,
    WRITE_FAIL,
}

enum KitLoadStatus {
    SUCCESS,
    INVALID_PLAYER,
    INVALID_PERMS,
    NEEDS_GMC,
    INVALID_KIT,
}

enum KitManageStatus {
    SUCCESS,
    FAILURE,
}

enum KitValidationStatus {
    SUCCESS,
    NO_FILE,
    INVALID_KIT,
}

public class KitModule extends ModuleBase {

    private final HashMap<Integer, String> buttonToKit = new HashMap<>();

    private GameMode previousGameMoode = GameMode.SURVIVAL;
    private String kitToLoadName = "";
    private String kitToLoad = "";

    private String previousInventory = "";

    @Nullable
    private ButtonWidget lastPressed = null;
    private String aboutToDelete = "";

    @Nullable
    private TextFieldWidget kitNameField;

    public KitModule() {
        super(new ModuleData("kit", "\uD83E\uDDF0 Kit", "Allows you to save and load kits"));
        getData().setShownName(generateDisplayName(0x9C9C7C));

        getData().setSelectable(false);
    }

    @Override
    public void postLoad() {
        KitManager.initializeDirectories();
        KitManager.deleteRecycledKits();
    }

    @Override
    public void update(MinecraftClient client) {
        if(!kitToLoad.isEmpty()) {
            if(client.player != null && client.player.isInCreativeMode()) {
                loadKitString(kitToLoadName, kitToLoad);

                RooHelper.sendChatCommand("gamemode " + previousGameMoode.getName());
            }
        }
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(ButtonWidget.builder(Text.of("+"), this::addKitButtonPressed)
                .dimensions(base.width/2 + 80, base.height/2 - 20, 20, 15)
                .tooltip(Tooltip.of(Text.of("Create kit")))
                .build());

        widgets.add(ButtonWidget.builder(Text.of("\uD83D\uDCC2"), this::folderButtonPressed)
                .tooltip(Tooltip.of(Text.of("Open kits folder (any kit can be shared and loaded with valid .json files)")))
                .dimensions(base.width/2 + 105, base.height/2 - 20, 20, 15)
                .build());

        widgets.add(ButtonWidget.builder(Text.of("\uD83D\uDCCB"), this::createFromClipboard)
                .tooltip(Tooltip.of(Text.of("Create kit from clipboard")))
                .dimensions(base.width/2 + 130, base.height/2 - 20, 20, 15)
                .build());

        widgets.add(ButtonWidget.builder(Text.of("↶"), this::undoButtonPressed)
                .tooltip(Tooltip.of(Text.of("Undo")))
                .dimensions(base.width/2 - 100, base.height/2 - 20, 20, 15)
                .build());

        var client = MinecraftClient.getInstance();
        previousGameMoode = client.interactionManager.getCurrentGameMode();

        kitNameField = new TextFieldWidget(client.textRenderer, base.width/2 - 70, base.height/2 - 20, 140, 15, Text.of(""));
        kitNameField.setSuggestion("Kit name");
        kitNameField.setMaxLength(32);
        kitNameField.setChangedListener(this::kitNameFieldChanged);

        widgets.add(kitNameField);

        buttonToKit.clear();

        aboutToDelete = "";
        lastPressed = null;

        var index = 0;
        for(var kit : KitManager.getKits()) {
            var y = base.height/2 + 10 + (index * 22);

            buttonToKit.put(y, kit);

            widgets.add(ButtonWidget.builder(Text.of(kit), (button) -> loadKit(kit))
                    .tooltip(Tooltip.of(Text.of("Load " + kit)))
                    .dimensions(base.width/2 - 70, y, 140, 20)
                    .build());

            widgets.add(ButtonWidget.builder(Text.of("-"), this::deleteButtonPressed)
                    .tooltip(Tooltip.of(Text.of("Delete " + kit)))
                    .dimensions(base.width/2 + 80, y, 20, 20)
                    .build());

            widgets.add(ButtonWidget.builder(Text.of("\uD83D\uDCCB"), (button) -> {
                        GLFW.glfwSetClipboardString(client.getWindow().getHandle(), KitManager.getKitFromName(kit));
                    })
                    .tooltip(Tooltip.of(Text.of("Copy " + kit + " to your clipboard")))
                    .dimensions(base.width/2 + 105, y, 20, 20)
                    .build());

            index++;
        }

        return widgets;
    }

    private void loadKit(String kitName) {
        previousInventory = KitManager.getKitString();
        loadKitString(kitName, KitManager.getKitFromName(kitName));
    }

    private void loadKitString(String kitName, String kitContents) {
        var loadStatus = KitManager.loadKitFromString(kitContents);

        kitToLoadName = "";
        kitToLoad = "";

        switch(loadStatus) {
            case SUCCESS -> { }
            case INVALID_PLAYER -> { RooHelper.sendNotification("Failed to load " + kitName + "!", "Invalid player"); }
            case INVALID_PERMS -> { RooHelper.sendNotification("Failed to load " + kitName + "!", "Invalid permissions"); }

            case NEEDS_GMC -> {
                kitToLoadName = kitName;
                kitToLoad = kitContents;
                RooHelper.sendNotification("Loading " + kitName + "...", "Waiting for Creative Mode");
                RooHelper.sendChatCommand("gamemode creative");
            }

            case INVALID_KIT -> { RooHelper.sendNotification("Failed to load " + kitName + "!", "Invalid kit"); }
        }
    }

    private void kitNameFieldChanged(String text) {
        if(kitNameField == null) {
            return;
        }

        if(text.isEmpty()) {
            kitNameField.setSuggestion("Kit name");
        }
        else {
            kitNameField.setSuggestion("");
        }
    }

    private void addKitButtonPressed(ButtonWidget button) {
        if(!checkField()) {
            return;
        }

        var kitName = kitNameField.getText();
        var createStatus = KitManager.createKit(kitName, KitManager.getKitString());

        switch(createStatus) {
            case SUCCESS -> { }
            case ALREADY_EXISTS -> { RooHelper.sendNotification("Failed to create kit!", "Kit name already exists"); }
            case INVALID_KIT -> { RooHelper.sendNotification("Failed to create kit!", "Invalid kit"); }
            case WRITE_FAIL -> { RooHelper.sendNotification("Failed to create kit!", "Failed to save kit file"); }
        }

        reloadScreen();
    }

    private void folderButtonPressed(ButtonWidget button) {
        Util.getOperatingSystem().open(new File(KitManager.KIT_BASE_PATH));
    }

    private void undoButtonPressed(ButtonWidget widget) {
        var previousContents = KitManager.getKitString();

        loadKitString("__previous", previousInventory);
        previousInventory = previousContents;
    }

    private void createFromClipboard(ButtonWidget widget) {
        var client = MinecraftClient.getInstance();

        if(!checkField()) {
            return;
        }

        var kitName = kitNameField.getText();

        var kitContents = GLFW.glfwGetClipboardString(client.getWindow().getHandle());
        if(kitContents == null) {
            kitContents = "";
        }

        var createStatus = KitManager.createKit(kitName, kitContents);

        switch(createStatus) {
            case SUCCESS -> { RooHelper.sendNotification("Loaded kit from clipboard!", kitName); }
            case ALREADY_EXISTS -> { RooHelper.sendNotification("Failed to create kit!", "Kit name already exists"); }
            case INVALID_KIT -> { RooHelper.sendNotification("Failed to create kit from clipboard!", "Invalid kit"); }
            case WRITE_FAIL -> { RooHelper.sendNotification("Failed to create kit from clipboard!", "Failed to save kit file"); }
        }

        reloadScreen();
    }

    private boolean checkField() {
        if(kitNameField == null || kitNameField.getText().isEmpty()) {
            RooHelper.sendNotification("Please set a kit name!", "Empty kit name field");
            return false;
        }

        return true;
    }

    private void deleteButtonPressed(ButtonWidget button) {
        var kit = buttonToKit.getOrDefault(button.getY(), "");

        if(!aboutToDelete.equals(kit)) {
            if(lastPressed != null) {
                lastPressed.setMessage(Text.of("-"));
                lastPressed.setTooltip(Tooltip.of(Text.of("Delete " + aboutToDelete)));
            }

            aboutToDelete = kit;
            lastPressed = button;

            button.setMessage(MutableText.of(new PlainTextContent.Literal("-")).withColor(0xD63C3C));
            button.setTooltip(Tooltip.of(MutableText.of(new PlainTextContent.Literal("Confirm delete " + kit + "!")).withColor(0xD63C3C)));

            return;
        }

        var deleteStatus = KitManager.deleteKit(kit);

        switch(deleteStatus) {
            case SUCCESS -> { RooHelper.sendNotification("Recycled " + kit + "!", "It will be deleted next startup!"); }
            case FAILURE -> { RooHelper.sendNotification("Failed to delete " + kit + "!", "The kit won't be deleted"); }
        }

        reloadScreen();
    }

    @Override
    public void onFilesDropped(List<Path> paths) {
        for(var path : paths) {
            var file = new File(path.toUri());

            var validationStatus = KitManager.kitStatus(file);

            switch(validationStatus) {
                case SUCCESS -> { }
                case NO_FILE -> { RooHelper.sendNotification("Failed to load dragged kit!", "Invalid file"); }
                case INVALID_KIT -> { RooHelper.sendNotification("Failed to load dragged kit!", "Invalid kit"); }
            }

            if(validationStatus != KitValidationStatus.SUCCESS) {
                continue;
            }

            var kitName = file.getName().split("\\.")[0];
            var kitContents = KitManager.getKitFromFile(file);

            var createStatus = KitManager.createKit(kitName, kitContents);

            switch(createStatus) {
                case SUCCESS -> { RooHelper.sendNotification("Successfully loaded kit!", kitName); }
                case ALREADY_EXISTS -> { RooHelper.sendNotification("Failed to create kit!", "Kit name already exists"); }
                case INVALID_KIT -> { RooHelper.sendNotification("Failed to create kit!", "Invalid kit"); }
                case WRITE_FAIL -> { RooHelper.sendNotification("Failed to create kit!", "Failed to save kit file"); }
            }
        }

        reloadScreen();
    }

    private void reloadScreen() {
        var client = MinecraftClient.getInstance();
        client.setScreen(new ModuleConfigScreen(this));
    }

    static class KitManager {

        private static final String KIT_BASE_PATH = "fireclient/kits/";
        private static final String KIT_DELETED_PATH = "fireclient/kits/deleted/";

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

        public static String getKitString() {
            var client = MinecraftClient.getInstance();
            if(client.player == null) {
                return DEFAULT_KIT;
            }

            var invNbt = client.player.getInventory().writeNbt(new NbtList());
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
                var from = NbtHelper.fromNbtProviderString(kit);
                var invList = (NbtList)from.get("inv");

                var playerInv = client.player.getInventory();

                var loadedInv = new PlayerInventory(client.player);
                loadedInv.readNbt(invList);

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
    }
}