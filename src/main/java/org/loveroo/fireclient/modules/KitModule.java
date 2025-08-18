package org.loveroo.fireclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.*;
import org.loveroo.fireclient.keybind.Keybind;
import org.loveroo.fireclient.screen.base.ScrollableWidget;
import org.loveroo.fireclient.screen.config.ModuleConfigScreen;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KitModule extends ModuleBase {

    private GameMode previousGameMoode = GameMode.SURVIVAL;
    private String kitToLoadName = "";
    private String kitToLoad = "";

    private String previousInventory = "";

    @Nullable
    private ButtonWidget lastPressed = null;
    private String aboutToDelete = "";

    @Nullable
    private TextFieldWidget kitNameField;

    private final int kitWidgetWidth = 300;
    private final int kitWidgetHeight = 120;

    public KitModule() {
        super(new ModuleData("kit", "\uD83E\uDDF0 Kit", "Allows you to save and load kits"));
        getData().setShownName(generateDisplayName(0x9C9C7C));

        getData().setGuiElement(false);

        for(var kit : KitManager.getKits()) {
            createKeybindFromKit(kit);
        }
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
                loadKitString(kitToLoadName, kitToLoad, true);

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

        widgets.add(ButtonWidget.builder(Text.of("\uD83D\uDCCB"), this::createFromClipboard)
                .tooltip(Tooltip.of(Text.of("Create kit from clipboard")))
                .dimensions(base.width/2 + 105, base.height/2 - 20, 20, 15)
                .build());

        widgets.add(ButtonWidget.builder(Text.of("↶"), this::undoButtonPressed)
                .tooltip(Tooltip.of(Text.of("Undo")))
                .dimensions(base.width/2 - 100, base.height/2 - 20, 20, 15)
                .build());

        widgets.add(ButtonWidget.builder(Text.of("\uD83D\uDCC2"), this::folderButtonPressed)
                .tooltip(Tooltip.of(Text.of("Open kits folder (any kit can be shared and loaded with valid .json files)")))
                .dimensions(base.width/2 - 125, base.height/2 - 20, 20, 15)
                .build());

        var client = MinecraftClient.getInstance();

        kitNameField = new TextFieldWidget(client.textRenderer, base.width/2 - 70, base.height/2 - 20, 140, 15, Text.of(""));
        kitNameField.setSuggestion("Kit name");
        kitNameField.setMaxLength(32);
        kitNameField.setChangedListener(this::kitNameFieldChanged);

        widgets.add(kitNameField);

        aboutToDelete = "";
        lastPressed = null;

        var elements = new ArrayList<ScrollableWidget.ElementEntry>();

        for(var kit : KitManager.getKits()) {
            var elementWidgets = new ArrayList<ClickableWidget>();
            createKeybindFromKit(kit);

            var loadKeybindButton = FireClientside.getKeybindManager().getKeybind(getKitKeyName(kit));
            elementWidgets.add(loadKeybindButton.getRebindButton(base.width / 2 - 140, 0, 60, 20));

            elementWidgets.add(ButtonWidget.builder(Text.of(kit), (button) -> loadKit(kit, true))
                    .tooltip(Tooltip.of(Text.of("Load \"" + kit + "\"")))
                    .dimensions(base.width/2 - 70, 0, 140, 20)
                    .build());

            elementWidgets.add(ButtonWidget.builder(Text.of("-"), this::deleteButtonPressed)
                    .tooltip(Tooltip.of(Text.of("Delete \"" + kit + "\"")))
                    .dimensions(base.width/2 + 80, 0, 20, 20)
                    .build());

            elementWidgets.add(ButtonWidget.builder(Text.of("\uD83D\uDCCB"), (button) -> {
                        GLFW.glfwSetClipboardString(client.getWindow().getHandle(), KitManager.getKitFromName(kit));
                    })
                    .tooltip(Tooltip.of(Text.of("Copy \"" + kit + "\" to your clipboard")))
                    .dimensions(base.width/2 + 105, 0, 20, 20)
                    .build());

            elements.add(new ScrollableWidget.ElementEntry(elementWidgets));
        }

        var scrollable = new ScrollableWidget(base, kitWidgetWidth, kitWidgetHeight, 0, 25, elements);
        scrollable.setPosition(base.width/2 - (kitWidgetWidth/2), base.height/2 + 10);

        widgets.add(scrollable);
        return widgets;
    }

    public KitManager.KitLoadStatus loadKit(String kitName, boolean notify) {
        previousInventory = KitManager.getKitString();
        return loadKitString(kitName, KitManager.getKitFromName(kitName), notify);
    }

    private KitManager.KitLoadStatus loadKitString(String kitName, String kitContents, boolean notify) {
        var loadStatus = KitManager.loadKitFromString(kitContents);

        kitToLoadName = "";
        kitToLoad = "";

        switch(loadStatus) {
            case SUCCESS -> { }
            case INVALID_PLAYER -> {
                if(notify) {
                    RooHelper.sendNotification("Failed to load \"" + kitName + "\"!", "Invalid player");
                }
            }
            case INVALID_PERMS -> {
                if(notify) {
                    RooHelper.sendNotification("Failed to load \"" + kitName + "\"!", "Invalid permissions");
                }
            }

            case NEEDS_GMC -> {
                previousGameMoode = MinecraftClient.getInstance().interactionManager.getCurrentGameMode();

                kitToLoadName = kitName;
                kitToLoad = kitContents;

                if(notify) {
                    RooHelper.sendNotification("Loading \"" + kitName + "\"...", "Waiting for Creative Mode");
                }

                RooHelper.sendChatCommand("gamemode creative");
            }

            case INVALID_KIT -> {
                if(notify) {
                    RooHelper.sendNotification("Failed to load \"" + kitName + "\"!", "Invalid kit");
                }
            }
        }

        return loadStatus;
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
            case ALREADY_EXISTS -> { RooHelper.sendNotification("Failed to create \"" + kitName + "\"!", "Kit name already exists"); }
            case INVALID_KIT -> { RooHelper.sendNotification("Failed to create \"" + kitName + "\"!", "Invalid kit"); }
            case WRITE_FAIL -> { RooHelper.sendNotification("Failed to create \"" + kitName + "\"!", "Failed to save file"); }
        }

        reloadScreen();
    }

    private void folderButtonPressed(ButtonWidget button) {
        Util.getOperatingSystem().open(new File(KitManager.KIT_BASE_PATH));
    }

    private void undoButtonPressed(ButtonWidget widget) {
        undo(true);
    }

    public KitManager.KitLoadStatus undo(boolean notify) {
        var previousContents = KitManager.getKitString();

        var status = loadKitString("__previous", previousInventory, notify);
        previousInventory = previousContents;

        return status;
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
            case SUCCESS -> { RooHelper.sendNotification("Loaded \"" + kitName + "\" from clipboard!", kitName); }
            case ALREADY_EXISTS -> { RooHelper.sendNotification("Failed to create \"" + kitName + "\"!", "Kit name already exists"); }
            case INVALID_KIT -> { RooHelper.sendNotification("Failed to create \"" + kitName + "\" from clipboard!", "Invalid kit"); }
            case WRITE_FAIL -> { RooHelper.sendNotification("Failed to create \"" + kitName + "\" from clipboard!", "Failed to save file"); }
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
        var kit = button.getMessage().getLiteralString();

        if(!aboutToDelete.equals(kit)) {
            if(lastPressed != null) {
                lastPressed.setMessage(Text.of("-"));
                lastPressed.setTooltip(Tooltip.of(Text.of("Delete \"" + aboutToDelete + "\"")));
            }

            aboutToDelete = kit;
            lastPressed = button;

            button.setMessage(MutableText.of(new PlainTextContent.Literal("-")).withColor(0xD63C3C));
            button.setTooltip(Tooltip.of(MutableText.of(new PlainTextContent.Literal("Confirm delete \"" + kit + "\"!")).withColor(0xD63C3C)));

            return;
        }

        var deleteStatus = KitManager.deleteKit(kit);

        switch(deleteStatus) {
            case SUCCESS -> { RooHelper.sendNotification("Recycled \"" + kit + "\"!", "It will be deleted next startup!"); }
            case FAILURE -> { RooHelper.sendNotification("Failed to delete \"" + kit + "\"!", "The kit won't be deleted"); }
        }

        FireClientside.getKeybindManager().unregisterKeybind(getKitKeyName(kit));
        FireClientside.saveConfig();

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

            if(validationStatus != KitManager.KitValidationStatus.SUCCESS) {
                continue;
            }

            var kitName = file.getName().split("\\.")[0];
            var kitContents = KitManager.getKitFromFile(file);

            var createStatus = KitManager.createKit(kitName, kitContents);

            switch(createStatus) {
                case SUCCESS -> { RooHelper.sendNotification("Successfully loaded \"" + kitName + "\"!", kitName); }
                case ALREADY_EXISTS -> { RooHelper.sendNotification("Failed to create \"" + kitName + "\"!", "Kit name already exists"); }
                case INVALID_KIT -> { RooHelper.sendNotification("Failed to create \"" + kitName + "\"!", "Invalid kit"); }
                case WRITE_FAIL -> { RooHelper.sendNotification("Failed to create \"" + kitName + "\"!", "Failed to save file"); }
            }
        }

        reloadScreen();
    }

    private void createKeybindFromKit(String kitName) {
        var keyName = getKitKeyName(kitName);
        if(FireClientside.getKeybindManager().hasKey(keyName)) {
            return;
        }

        var keybind = new Keybind(keyName, "\uD83E\uDDF0", "Load \"" + kitName + "\"", true, null,
                () -> loadKit(kitName, true), null);

        keybind.setShortName(true);
        FireClientside.getKeybindManager().registerKeybind(keybind);
    }

    private String getKitKeyName(String kitName) {
        return "use_kit_" + kitName;
    }
}