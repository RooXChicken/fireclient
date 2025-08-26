package org.loveroo.fireclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.*;
import org.loveroo.fireclient.keybind.Keybind;
import org.loveroo.fireclient.screen.base.ScrollableWidget;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class KitModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0x9C9C7C);

    private GameMode previousGameMode = GameMode.SURVIVAL;
    private String kitToLoadName = "";
    private String kitToLoad = "";

    private String previousInventory = "";

    @Nullable
    private ButtonWidget lastPressed = null;
    private String aboutToDelete = "";

    @Nullable
    private TextFieldWidget kitNameField;

    @Nullable
    private ScrollableWidget scrollable;

    private double scroll = 0.0;

    private final int kitWidgetWidth = 330;
    private final int kitWidgetHeight = 140;

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
    public void update(MinecraftClient client) {
        if(!kitToLoad.isEmpty()) {
            if(client.player != null && client.player.isInCreativeMode()) {
                loadKitString(kitToLoadName, kitToLoad, true);

                RooHelper.sendChatCommand("gamemode " + previousGameMode.getName());
            }
        }
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(ButtonWidget.builder(Text.translatable("fireclient.module.kit.create.name"), this::addKitButtonPressed)
                .dimensions(base.width/2 + 80, base.height/2 - 80, 20, 15)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.kit.create.tooltip")))
                .build());

        widgets.add(ButtonWidget.builder(Text.translatable("fireclient.module.kit.create_clipboard.name"), this::createFromClipboard)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.kit.create_clipboard.tooltip")))
                .dimensions(base.width/2 + 105, base.height/2 - 80, 20, 15)
                .build());

        widgets.add(ButtonWidget.builder(Text.translatable("fireclient.module.kit.undo.name"), this::undoButtonPressed)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.kit.undo.tooltip")))
                .dimensions(base.width/2 - 100, base.height/2 - 80, 20, 15)
                .build());

        widgets.add(ButtonWidget.builder(Text.translatable("fireclient.module.kit.open_folder.name"), this::folderButtonPressed)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.kit.open_folder.tooltip")))
                .dimensions(base.width/2 - 125, base.height/2 - 80, 20, 15)
                .build());

        var client = MinecraftClient.getInstance();

        kitNameField = new TextFieldWidget(client.textRenderer, base.width/2 - 70, base.height/2 - 80, 140, 15, Text.of(""));
        kitNameField.setSuggestion(Text.translatable("fireclient.module.kit.name_suggestion").getString());
        kitNameField.setMaxLength(32);

        kitNameField.setChangedListener((text) -> {
            if(text.isEmpty()) {
                kitNameField.setSuggestion(Text.translatable("fireclient.module.kit.name_suggestion").getString());
            }
            else {
                kitNameField.setSuggestion("");
            }
        });

        widgets.add(kitNameField);

        aboutToDelete = "";
        lastPressed = null;

        var elements = new ArrayList<ScrollableWidget.ElementEntry>();

        for(var kit : KitManager.getKits()) {
            var elementWidgets = new ArrayList<ClickableWidget>();
            createKeybindFromKit(kit);

            var loadKeybindButton = FireClientside.getKeybindManager().getKeybind(getKitKeyName(kit));
            elementWidgets.add(loadKeybindButton.getRebindButton(base.width / 2 - 155, 0, 50, 20));

            elementWidgets.add(ButtonWidget.builder(Text.of(kit), (button) -> loadKit(kit, true))
                    .tooltip(Tooltip.of(Text.translatable("fireclient.module.kit.load.tooltip", kit)))
                    .dimensions(base.width/2 - 70, 0, 140, 20)
                    .build());

            elementWidgets.add(ButtonWidget.builder(Text.translatable("fireclient.module.kit.delete.name"), (button) -> deleteButtonPressed(button, kit))
                    .tooltip(Tooltip.of(Text.translatable("fireclient.module.kit.delete.tooltip", kit)))
                    .dimensions(base.width/2 + 80, 0, 20, 20)
                    .build());

            elementWidgets.add(ButtonWidget.builder(Text.translatable("fireclient.module.kit.copy_to_clipboard.name"), (button) -> {
                        GLFW.glfwSetClipboardString(client.getWindow().getHandle(), KitManager.getKitFromName(kit));
                    })
                    .tooltip(Tooltip.of(Text.translatable("fireclient.module.kit.copy_to_clipboard.tooltip", kit)))
                    .dimensions(base.width/2 + 105, 0, 20, 20)
                    .build());

            elementWidgets.add(ButtonWidget.builder(Text.translatable("fireclient.module.kit.edit.name"), (button -> editButtonPressed(button, kit)))
                    .tooltip(Tooltip.of(Text.translatable("fireclient.module.kit.edit.tooltip", kit)))
                    .dimensions(base.width/2 + 130, 0, 20, 20)
                    .build());

            elementWidgets.add(ButtonWidget.builder(Text.translatable("fireclient.module.kit.share.name"), (button -> uploadKitButtonPressed(button, kit)))
                    .tooltip(Tooltip.of(Text.translatable("fireclient.module.kit.share.tooltip", kit)))
                    .dimensions(base.width/2 - 100, 0, 20, 20)
                    .build());

            elements.add(new ScrollableWidget.ElementEntry(elementWidgets));
        }

        scrollable = new ScrollableWidget(base, kitWidgetWidth, kitWidgetHeight, 0, 25, elements);
        scrollable.setPosition(base.width/2 - (kitWidgetWidth/2), base.height/2 - 50);
        scrollable.setScrollY(scroll);

        widgets.add(scrollable);
        return widgets;
    }

    @Override
    public void moduleConfigPressed(ButtonWidget button) {
        scroll = 0.0;
        super.moduleConfigPressed(button);
    }

    @Override
    public void drawScreen(Screen base, DrawContext context, float delta) {
        drawScreenHeader(context, base.width/2, base.height/2 - 95);

        if(scrollable != null) {
            scroll = scrollable.getScrollY();
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
                            Text.translatable("fireclient.module.kit.generic.load_failure.title", kitName),
                            Text.translatable("fireclient.module.kit.load.generic.invalid_player.contents"));
                }
            }
            case INVALID_PERMS -> {
                if(notify) {
                    RooHelper.sendNotification(
                            Text.translatable("fireclient.module.kit.generic.load_failure.title", kitName),
                            Text.translatable("fireclient.module.kit.load.failure.invalid_permission.contents"));
                }
            }

            case NEEDS_GMC -> {
                previousGameMode = MinecraftClient.getInstance().interactionManager.getCurrentGameMode();

                kitToLoadName = kitName;
                kitToLoad = kitContents;

                if(notify) {
                    RooHelper.sendNotification(
                            Text.translatable("fireclient.module.kit.load.waiting_gmc.title", kitName),
                            Text.translatable("fireclient.module.kit.load.waiting_gmc.contents"));
                }

                RooHelper.sendChatCommand("gamemode creative");
            }

            case INVALID_KIT -> {
                if(notify) {
                    RooHelper.sendNotification(
                            Text.translatable("fireclient.module.kit.generic.load_failure.title", kitName),
                            Text.translatable("fireclient.module.kit.generic.invalid_kit.contents"));
                }
            }
        }

        return loadStatus;
    }

    private void addKitButtonPressed(ButtonWidget button) {
        if(!checkField()) {
            return;
        }

        var kitName = kitNameField.getText();
        var createStatus = KitManager.createKit(kitName, KitManager.getPlayerInventoryString());

        switch(createStatus) {
            case SUCCESS -> { }

            case ALREADY_EXISTS -> {
                RooHelper.sendNotification(
                        Text.translatable("fireclient.module.kit.create.failure.title", kitName),
                        Text.translatable("fireclient.module.kit.generic.already_exists.contents"));

                return;
            }

            case INVALID_KIT -> {
                RooHelper.sendNotification(
                        Text.translatable("fireclient.module.kit.create.failure.title", kitName),
                        Text.translatable("fireclient.module.kit.generic.invalid_kit.contents"));
            }

            case WRITE_FAIL -> {
                RooHelper.sendNotification(
                        Text.translatable("fireclient.module.kit.create.failure.title", kitName),
                        Text.translatable("fireclient.module.kit.generic.write_failure.contents"));
            }
        }

        reloadScreen();
    }

    private void editButtonPressed(ButtonWidget button, String kitName) {
        var status = KitManager.editKit(kitName, false);

        switch(status) {
            case SUCCESS -> {}

            case INVALID_KIT -> {
                RooHelper.sendNotification(
                        Text.translatable("fireclient.module.kit.preview.failure.title", kitName),
                        Text.translatable("fireclient.module.kit.generic.invalid_kit.contents"));
            }

            case INVALID_PLAYER -> {
                RooHelper.sendNotification(
                        Text.translatable("fireclient.module.kit.create.failure.title", kitName),
                        Text.translatable("fireclient.module.kit.load.generic.invalid_player.contents"));
            }
        }
    }

    private void uploadKitButtonPressed(ButtonWidget button, String kitName) {
        var kitContents = KitManager.getKitFromName(kitName);
        KitManager.uploadKit(kitName, kitContents, (status) -> {
            switch(status) {
                case SUCCESS -> { }

                case INVALID_KIT -> {

                    RooHelper.sendNotification(
                            Text.translatable("fireclient.module.kit.share.failure.generic", kitName),
                            Text.translatable("fireclient.module.kit.generic.invalid_kit.contents")
                    );
                }

                case TOO_LARGE -> {
                    RooHelper.sendNotification(
                            Text.translatable("fireclient.module.kit.share.failure.generic", kitName),
                            Text.translatable("fireclient.module.kit.share.failure.too_large")
                    );
                }

                case FAILURE -> {
                    RooHelper.sendNotification(
                            Text.translatable("fireclient.module.kit.share.failure.generic", kitName),
                            Text.translatable("fireclient.module.kit.failure.generic_fail")
                    );
                }

                case RATE_LIMITED -> {
                    RooHelper.sendNotification(
                            Text.translatable("fireclient.module.kit.share.failure.generic", kitName),
                            Text.translatable("fireclient.module.kit.server.fail.rate_limit")
                    );
                }
            }
        });
    }

    private void folderButtonPressed(ButtonWidget button) {
        Util.getOperatingSystem().open(new File(KitManager.KIT_BASE_PATH));
    }

    private void undoButtonPressed(ButtonWidget widget) {
        undo(true);
    }

    public KitManager.KitLoadStatus undo(boolean notify) {
        var previousContents = KitManager.getPlayerInventoryString();

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
            case SUCCESS -> {
                RooHelper.sendNotification(
                        Text.translatable("fireclient.module.kit.create_clipboard.success.title", kitName),
                        Text.translatable("fireclient.module.kit.create_clipboard.success.contents"));
            }

            case ALREADY_EXISTS -> {
                RooHelper.sendNotification(
                        Text.translatable("fireclient.module.kit.create_clipboard.failure.title", kitName),
                        Text.translatable("fireclient.module.kit.generic.already_exists.contents"));
            }

            case INVALID_KIT -> {
                RooHelper.sendNotification(
                        Text.translatable("fireclient.module.kit.create_clipboard.failure.title", kitName),
                        Text.translatable("fireclient.module.kit.generic.invalid_kit.contents"));
            }

            case WRITE_FAIL -> {
                RooHelper.sendNotification(
                        Text.translatable("fireclient.module.kit.create_clipboard.failure.title", kitName),
                        Text.translatable("fireclient.module.kit.generic.write_failure.contents"));
            }
        }

        reloadScreen();
    }

    private boolean checkField() {
        if(kitNameField == null || kitNameField.getText().isEmpty()) {
            RooHelper.sendNotification(
                    Text.translatable("fireclient.module.kit.empty_name.title"),
                    Text.translatable("fireclient.module.kit.empty_name.contents"));

            return false;
        }

        return true;
    }

    private void deleteButtonPressed(ButtonWidget button, String kitName) {
        if(!aboutToDelete.equals(kitName)) {
            if(lastPressed != null) {
                lastPressed.setMessage(Text.translatable("fireclient.module.kit.delete.name"));
                lastPressed.setTooltip(Tooltip.of(Text.translatable("fireclient.module.kit.delete.tooltip", aboutToDelete)));
            }

            aboutToDelete = kitName;
            lastPressed = button;

            button.setMessage(Text.translatable("fireclient.module.kit.delete.name").withColor(0xD63C3C));
            button.setTooltip(Tooltip.of(Text.translatable("fireclient.module.kit.delete.confirm", kitName).withColor(0xD63C3C)));

            return;
        }

        var deleteStatus = KitManager.deleteKit(kitName);

        switch(deleteStatus) {
            case SUCCESS -> {
                RooHelper.sendNotification(
                        Text.translatable("fireclient.module.kit.recycle.success.title", kitName),
                        Text.translatable("fireclient.module.kit.recycle.success.contents"));
            }

            case FAILURE -> {
                RooHelper.sendNotification(
                        Text.translatable("fireclient.module.kit.recycle.failure.title", kitName),
                        Text.translatable("fireclient.module.kit.recycle.failure.contents"));
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
                            Text.translatable("fireclient.module.kit.drag_and_drop.failure.title", file.getName()),
                            Text.translatable("fireclient.module.kit.drag_and_drop.no_file.contents"));
                }

                case INVALID_KIT -> {
                    RooHelper.sendNotification(
                            Text.translatable("fireclient.module.kit.drag_and_drop.failure.title", file.getName()),
                            Text.translatable("fireclient.module.kit.generic.invalid_kit.contents"));
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
                            Text.translatable("fireclient.module.kit.drag_and_drop.success.title", kitName),
                            Text.translatable("fireclient.module.kit.drag_and_drop.success.contents"));
                }

                case ALREADY_EXISTS -> {
                    RooHelper.sendNotification(
                            Text.translatable("fireclient.module.kit.generic.create_failure.title", kitName),
                            Text.translatable("fireclient.module.kit.generic.already_exists.contents"));
                }

                case INVALID_KIT -> {
                    RooHelper.sendNotification(
                            Text.translatable("fireclient.module.kit.generic.create_failure.title", kitName),
                            Text.translatable("fireclient.module.kit.generic.invalid_kit.contents"));
                }

                case WRITE_FAIL -> {
                    RooHelper.sendNotification(
                            Text.translatable("fireclient.module.kit.generic.create_failure.title", kitName),
                            Text.translatable("fireclient.module.kit.generic.write_failure.contents"));
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
                Text.translatable("fireclient.module.kit.load_keybind.name"),
                Text.translatable("fireclient.module.kit.load_keybind.tooltip", kitName),
                true, null,
                () -> loadKit(kitName, true), null);

        keybind.setShortName(true);
        FireClientside.getKeybindManager().registerKeybind(keybind);
    }

    private String getKitKeyName(String kitName) {
        return "use_kit_" + kitName;
    }
}