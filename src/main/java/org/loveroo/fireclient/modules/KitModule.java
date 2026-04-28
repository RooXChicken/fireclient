package org.loveroo.fireclient.modules;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.KitManager;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;
import org.loveroo.fireclient.screen.base.ScrollableWidget;
import org.loveroo.fireclient.screen.widgets.FavoriteButtonWidget.FavoriteButtonBuilder;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.world.level.GameType;

public class KitModule extends ModuleBase {
    
    private static final Color color = Color.fromRGB(0x9C9C7C);
    
    private GameType previousGameMode = GameType.SURVIVAL;
    private String kitToLoadName = "";
    private String kitToLoad = "";
    
    private String previousInventory = "";
    
    @Nullable
    private Button lastPressed = null;
    private String aboutToDelete = "";
    
    @Nullable
    private EditBox kitNameField;
    
    @Nullable
    private ScrollableWidget scrollable;
    
    private double scroll = 0.0;
    
    private final int kitWidgetWidth = 330;
    private final int kitWidgetHeight = 140;

    private final HashSet<String> favoriteKits = new HashSet<>();
    
    public KitModule() {
        super(new ModuleData("kit", "\uD83E\uDDF0", color));
        
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
    public void update(Minecraft client) {
        if(!kitToLoad.isEmpty()) {
            if(client.player != null && client.player.hasInfiniteMaterials()) {
                loadKitString(kitToLoadName, kitToLoad, true);

                RooHelper.sendChatCommand("gamemode " + previousGameMode.getName());
            }
        }
    }

    @Override
    public void loadJson(JSONObject json) throws Exception {
        super.loadJson(json);

        var favoriteList = json.optJSONArray("favorites");
        if(favoriteList == null) {
            favoriteList = new JSONArray();
        }

        for(var i = 0; i < favoriteList.length(); i++) {
            var kitName = favoriteList.optString(i, null);
            if(kitName == null) {
                continue;
            }

            favoriteKits.add(kitName);
        }
    }
    
    @Override
    public JSONObject saveJson() throws Exception {
        var json = super.saveJson();

        var favoriteList = new JSONArray();

        for(var kitName : favoriteKits) {
            favoriteList.put(kitName);
        }

        json.put("favorites", favoriteList);

        return json;
    }

    @Override
    public List<AbstractWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<AbstractWidget>();
        
        widgets.add(Button.builder(Component.translatable("fireclient.module.kit.create.name"), this::addKitButtonPressed)
            .bounds(base.width/2 + 80, base.height/2 - 80, 20, 15)
            .tooltip(Tooltip.create(Component.translatable("fireclient.module.kit.create.tooltip")))
            .build());
        
        widgets.add(Button.builder(Component.translatable("fireclient.module.kit.create_clipboard.name"), this::createFromClipboard)
            .tooltip(Tooltip.create(Component.translatable("fireclient.module.kit.create_clipboard.tooltip")))
            .bounds(base.width/2 + 105, base.height/2 - 80, 20, 15)
            .build());
        
        widgets.add(Button.builder(Component.translatable("fireclient.module.kit.undo.name"), this::undoButtonPressed)
            .tooltip(Tooltip.create(Component.translatable("fireclient.module.kit.undo.tooltip")))
            .bounds(base.width/2 - 100, base.height/2 - 80, 20, 15)
            .build());
        
        widgets.add(Button.builder(Component.translatable("fireclient.module.kit.open_folder.name"), this::folderButtonPressed)
            .tooltip(Tooltip.create(Component.translatable("fireclient.module.kit.open_folder.tooltip")))
            .bounds(base.width/2 - 125, base.height/2 - 80, 20, 15)
            .build());
        
        var client = Minecraft.getInstance();
        
        kitNameField = new EditBox(client.font, base.width/2 - 70, base.height/2 - 80, 140, 15, Component.nullToEmpty(""));
        kitNameField.setSuggestion(Component.translatable("fireclient.module.kit.name_suggestion").getString());
        kitNameField.setMaxLength(32);
        
        kitNameField.setResponder((text) -> {
            if(text.isEmpty()) {
                kitNameField.setSuggestion(Component.translatable("fireclient.module.kit.name_suggestion").getString());
            }
            else {
                kitNameField.setSuggestion("");
            }
        });
        
        widgets.add(kitNameField);
        
        aboutToDelete = "";
        lastPressed = null;
        
        var elements = new ArrayList<ScrollableWidget.ElementEntry>();
        
        for(var kit : KitManager.getKits(favoriteKits)) {
            var elementWidgets = new ArrayList<AbstractWidget>();
            createKeybindFromKit(kit);
            
            var loadKeybindButton = FireClientside.getKeybindManager().getKeybind(getKitKeyName(kit));
            elementWidgets.add(loadKeybindButton.getRebindButton(base.width / 2 - 155, 0, 50, 20));
            
            elementWidgets.add(new FavoriteButtonBuilder(Component.nullToEmpty(kit))
                .getValue(() -> { return isFavorited(kit); })
                .setValue((value) -> { setFavorited(kit, value); })
                .onPress((button) -> loadKit(kit, true))
                .tooltip(Tooltip.create(Component.translatable("fireclient.module.kit.load.tooltip", kit)))
                .dimensions(base.width/2 - 70, 0, 140, 20)
                .build());
            
            elementWidgets.add(Button.builder(Component.translatable("fireclient.module.kit.delete.name"), (button) -> deleteButtonPressed(button, kit))
                .tooltip(Tooltip.create(Component.translatable("fireclient.module.kit.delete.tooltip", kit)))
                .bounds(base.width/2 + 80, 0, 20, 20)
                .build());
            
            elementWidgets.add(Button.builder(Component.translatable("fireclient.module.kit.copy_to_clipboard.name"), (button) -> {
                GLFW.glfwSetClipboardString(client.getWindow().handle(), KitManager.getKitFromName(kit));
            })
                .tooltip(Tooltip.create(Component.translatable("fireclient.module.kit.copy_to_clipboard.tooltip", kit)))
                .bounds(base.width/2 + 105, 0, 20, 20)
                .build());
            
            elementWidgets.add(Button.builder(Component.translatable("fireclient.module.kit.edit.name"), (button -> editButtonPressed(button, kit)))
                .tooltip(Tooltip.create(Component.translatable("fireclient.module.kit.edit.tooltip", kit)))
                .bounds(base.width/2 + 130, 0, 20, 20)
                .build());
            
            elementWidgets.add(Button.builder(Component.translatable("fireclient.module.kit.share.name"), (button -> uploadKitButtonPressed(button, kit)))
                .tooltip(Tooltip.create(Component.translatable("fireclient.module.kit.share.tooltip", kit)))
                .bounds(base.width/2 - 100, 0, 20, 20)
                .build());
            
            elements.add(new ScrollableWidget.ElementEntry(elementWidgets));
        }
        
        scrollable = new ScrollableWidget(base, kitWidgetWidth, kitWidgetHeight, 0, 25, elements);
        scrollable.setPosition(base.width/2 - (kitWidgetWidth/2), base.height/2 - 50);
        scrollable.setScrollAmount(scroll);
        
        widgets.add(scrollable);
        return widgets;
    }
    
    @Override
    public void moduleConfigPressed(Button button) {
        scroll = 0.0;
        super.moduleConfigPressed(button);
    }
    
    @Override
    public void drawScreen(Screen base, GuiGraphicsExtractor context, float delta) {
        drawScreenHeader(context, base.width/2, base.height/2 - 95);
        
        if(scrollable != null) {
            scroll = scrollable.scrollAmount();
        }
    }

    private boolean isFavorited(String kitName) {
        return (favoriteKits.contains(kitName));
    }

    private void setFavorited(String kitName, boolean favorited) {
        if(favorited) {
            favoriteKits.add(kitName);
        }
        else {
            favoriteKits.remove(kitName);
        }
    }
    
    public KitManager.KitLoadStatus loadKit(String kitName, boolean notify) {
        previousInventory = KitManager.getPlayerInventoryString();
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
                    RooHelper.sendNotification(
                    Component.translatable("fireclient.module.kit.generic.load_failure.title", kitName),
                    Component.translatable("fireclient.module.kit.load.generic.invalid_player.contents"));
                }
            }
            case INVALID_PERMS -> {
                if(notify) {
                    RooHelper.sendNotification(
                    Component.translatable("fireclient.module.kit.generic.load_failure.title", kitName),
                    Component.translatable("fireclient.module.kit.load.failure.invalid_permission.contents"));
                }
            }
            
            case NEEDS_GMC -> {
                previousGameMode = Minecraft.getInstance().gameMode.getPlayerMode();
                
                kitToLoadName = kitName;
                kitToLoad = kitContents;
                
                if(notify) {
                    RooHelper.sendNotification(
                    Component.translatable("fireclient.module.kit.load.waiting_gmc.title", kitName),
                    Component.translatable("fireclient.module.kit.load.waiting_gmc.contents"));
                }
                
                RooHelper.sendChatCommand("gamemode creative");
            }
            
            case INVALID_KIT -> {
                if(notify) {
                    RooHelper.sendNotification(
                    Component.translatable("fireclient.module.kit.generic.load_failure.title", kitName),
                    Component.translatable("fireclient.module.kit.generic.invalid_kit.contents"));
                }
            }
        }
        
        return loadStatus;
    }
    
    private void addKitButtonPressed(Button button) {
        if(!checkField()) {
            return;
        }
        
        var kitName = kitNameField.getValue();
        var createStatus = KitManager.createKit(kitName, KitManager.getPlayerInventoryString());
        
        switch(createStatus) {
            case SUCCESS -> { }
            
            case ALREADY_EXISTS -> {
                RooHelper.sendNotification(
                Component.translatable("fireclient.module.kit.generic.create_failure.title", kitName),
                Component.translatable("fireclient.module.kit.generic.already_exists.contents"));
                
                return;
            }
            
            case INVALID_KIT -> {
                RooHelper.sendNotification(
                Component.translatable("fireclient.module.kit.generic.create_failure.title", kitName),
                Component.translatable("fireclient.module.kit.generic.invalid_kit.contents"));
            }
            
            case WRITE_FAIL -> {
                RooHelper.sendNotification(
                Component.translatable("fireclient.module.kit.generic.create_failure.title", kitName),
                Component.translatable("fireclient.module.kit.generic.write_failure.contents"));
            }
        }
        
        reloadScreen();
    }
    
    private void editButtonPressed(Button button, String kitName) {
        var status = KitManager.editKit(kitName, false);
        
        switch(status) {
            case SUCCESS -> {}
            
            case INVALID_KIT -> {
                RooHelper.sendNotification(
                Component.translatable("fireclient.module.kit.preview.failure.title", kitName),
                Component.translatable("fireclient.module.kit.generic.invalid_kit.contents"));
            }
            
            case INVALID_PLAYER -> {
                RooHelper.sendNotification(
                Component.translatable("fireclient.module.kit.generic.create_failure.title", kitName),
                Component.translatable("fireclient.module.kit.load.generic.invalid_player.contents"));
            }
        }
    }
    
    private void uploadKitButtonPressed(Button button, String kitName) {
        var kitContents = KitManager.getKitFromName(kitName);
        KitManager.uploadKit(kitName, kitContents, (status) -> {
            switch(status) {
                case SUCCESS -> { }
                
                case INVALID_KIT -> {
                    
                    RooHelper.sendNotification(
                        Component.translatable("fireclient.module.kit.share.failure.generic", kitName),
                        Component.translatable("fireclient.module.kit.generic.invalid_kit.contents")
                    );
                }
                
                case TOO_LARGE -> {
                    RooHelper.sendNotification(
                        Component.translatable("fireclient.module.kit.share.failure.generic", kitName),
                        Component.translatable("fireclient.module.kit.share.failure.too_large")
                    );
                }
                
                case FAILURE -> {
                    RooHelper.sendNotification(
                        Component.translatable("fireclient.module.kit.share.failure.generic", kitName),
                        Component.translatable("fireclient.module.kit.failure.generic_fail")
                    );
                }
                
                case RATE_LIMITED -> {
                    RooHelper.sendNotification(
                        Component.translatable("fireclient.module.kit.share.failure.generic", kitName),
                        Component.translatable("fireclient.module.kit.server.fail.rate_limit")
                    );
                }
            }
        });
    }
    
    private void folderButtonPressed(Button button) {
        Util.getPlatform().openFile(new File(KitManager.KIT_BASE_PATH));
    }
    
    private void undoButtonPressed(Button widget) {
        undo(true);
    }
    
    public KitManager.KitLoadStatus undo(boolean notify) {
        var previousContents = KitManager.getPlayerInventoryString();
        
        var status = loadKitString("__previous", previousInventory, notify);
        previousInventory = previousContents;
        
        return status;
    }
    
    private void createFromClipboard(Button widget) {
        var client = Minecraft.getInstance();
        
        if(!checkField()) {
            return;
        }
        
        var kitName = kitNameField.getValue();
        
        var kitContents = GLFW.glfwGetClipboardString(client.getWindow().handle());
        if(kitContents == null) {
            kitContents = "";
        }
        
        var createStatus = KitManager.createKit(kitName, kitContents);
        
        switch(createStatus) {
            case SUCCESS -> {
                RooHelper.sendNotification(
                Component.translatable("fireclient.module.kit.create_clipboard.success.title", kitName),
                Component.translatable("fireclient.module.kit.create_clipboard.success.contents"));
            }
            
            case ALREADY_EXISTS -> {
                RooHelper.sendNotification(
                Component.translatable("fireclient.module.kit.create_clipboard.failure.title", kitName),
                Component.translatable("fireclient.module.kit.generic.already_exists.contents"));
            }
            
            case INVALID_KIT -> {
                RooHelper.sendNotification(
                Component.translatable("fireclient.module.kit.create_clipboard.failure.title", kitName),
                Component.translatable("fireclient.module.kit.generic.invalid_kit.contents"));
            }
            
            case WRITE_FAIL -> {
                RooHelper.sendNotification(
                Component.translatable("fireclient.module.kit.create_clipboard.failure.title", kitName),
                Component.translatable("fireclient.module.kit.generic.write_failure.contents"));
            }
        }
        
        reloadScreen();
    }
    
    private boolean checkField() {
        if(kitNameField == null || kitNameField.getValue().isEmpty()) {
            RooHelper.sendNotification(
                Component.translatable("fireclient.module.kit.empty_name.title"),
                Component.translatable("fireclient.module.kit.empty_name.contents"));
            
            return false;
        }
        
        return true;
    }
    
    private void deleteButtonPressed(Button button, String kitName) {
        if(isFavorited(kitName)) {
            return;
        }
        
        if(!aboutToDelete.equals(kitName)) {
            if(lastPressed != null) {
                lastPressed.setMessage(Component.translatable("fireclient.module.kit.delete.name"));
                lastPressed.setTooltip(Tooltip.create(Component.translatable("fireclient.module.kit.delete.tooltip", aboutToDelete)));
            }
            
            aboutToDelete = kitName;
            lastPressed = button;
            
            button.setMessage(Component.translatable("fireclient.module.kit.delete.name").withColor(0xD63C3C));
            button.setTooltip(Tooltip.create(Component.translatable("fireclient.module.kit.delete.confirm", kitName).withColor(0xD63C3C)));
            
            return;
        }
        
        var deleteStatus = KitManager.deleteKit(kitName);
        
        switch(deleteStatus) {
            case SUCCESS -> {
                RooHelper.sendNotification(
                Component.translatable("fireclient.module.kit.recycle.success.title", kitName),
                Component.translatable("fireclient.module.kit.recycle.success.contents"));
            }
            
            case FAILURE -> {
                RooHelper.sendNotification(
                Component.translatable("fireclient.module.kit.recycle.failure.title", kitName),
                Component.translatable("fireclient.module.kit.recycle.failure.contents"));
            }
        }
        
        FireClientside.getKeybindManager().unregisterKeybind(getKitKeyName(kitName));
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
                
                case NO_FILE -> {
                    RooHelper.sendNotification(
                    Component.translatable("fireclient.module.kit.drag_and_drop.failure.title", file.getName()),
                    Component.translatable("fireclient.module.kit.drag_and_drop.no_file.contents"));
                }
                
                case INVALID_KIT -> {
                    RooHelper.sendNotification(
                    Component.translatable("fireclient.module.kit.drag_and_drop.failure.title", file.getName()),
                    Component.translatable("fireclient.module.kit.generic.invalid_kit.contents"));
                }
            }
            
            if(validationStatus != KitManager.KitValidationStatus.SUCCESS) {
                continue;
            }
            
            var kitName = file.getName().split("\\.")[0];
            var kitContents = KitManager.getKitFromFile(file);
            
            var createStatus = KitManager.createKit(kitName, kitContents);
            
            switch(createStatus) {
                case SUCCESS -> {
                    RooHelper.sendNotification(
                    Component.translatable("fireclient.module.kit.drag_and_drop.success.title", kitName),
                    Component.translatable("fireclient.module.kit.drag_and_drop.success.contents"));
                }
                
                case ALREADY_EXISTS -> {
                    RooHelper.sendNotification(
                    Component.translatable("fireclient.module.kit.generic.create_failure.title", kitName),
                    Component.translatable("fireclient.module.kit.generic.already_exists.contents"));
                }
                
                case INVALID_KIT -> {
                    RooHelper.sendNotification(
                    Component.translatable("fireclient.module.kit.generic.create_failure.title", kitName),
                    Component.translatable("fireclient.module.kit.generic.invalid_kit.contents"));
                }
                
                case WRITE_FAIL -> {
                    RooHelper.sendNotification(
                    Component.translatable("fireclient.module.kit.generic.create_failure.title", kitName),
                    Component.translatable("fireclient.module.kit.generic.write_failure.contents"));
                }
            }
        }
        
        reloadScreen();
    }
    
    private void createKeybindFromKit(String kitName) {
        var keyName = getKitKeyName(kitName);
        if(FireClientside.getKeybindManager().hasKey(keyName)) {
            return;
        }
        
        var keybind = new Keybind(keyName,
        Component.translatable("fireclient.module.kit.load_keybind.name"),
        Component.translatable("fireclient.module.kit.load_keybind.tooltip", kitName),
        true, null,
        () -> loadKit(kitName, true), null);
        
        keybind.setShortName(true);
        FireClientside.getKeybindManager().registerKeybind(keybind);
    }
    
    private String getKitKeyName(String kitName) {
        return "use_kit_" + kitName;
    }
}