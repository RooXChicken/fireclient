package org.loveroo.fireclient.modules;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.mixin.registry.sync.RegistriesAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.screen.config.ConfigScreenBase;
import org.loveroo.fireclient.screen.config.ModuleConfigScreen;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KitModule extends ModuleBase {

    private static final String KIT_BASE_PATH = "fireclient/kits/";

    private final HashMap<Integer, String> buttonToKit = new HashMap<>();
    private String kitToLoad = "";

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
        new File(KIT_BASE_PATH).mkdirs();
    }

    @Override
    public void update(MinecraftClient client) {
        if(!kitToLoad.isEmpty()) {
            if(client.player != null && client.player.isInCreativeMode()) {
                loadKit(kitToLoad);
                kitToLoad = "";
            }
        }
    }

    private void saveKit(String kitName) {
        var client = MinecraftClient.getInstance();
        if(client.player == null) {
            return;
        }

        var invNbt = client.player.getInventory().writeNbt(new NbtList());
        var kit = "{\"inv\":" + invNbt.asString() + "}";

        try {
            var writer = new FileWriter(getKitPath(kitName));

            writer.write(kit);
            writer.close();
        }
        catch(Exception e) {
            FireClient.LOGGER.error("Failed to save kit {}!", kitName, e);
        }
    }

    private String getKitPath(String kitName) {
        return KIT_BASE_PATH + kitName + ".json";
    }

    private void loadKit(String kitName) {
        var client = MinecraftClient.getInstance();
        if(client.player == null) {
            return;
        }

        if(!client.player.isInCreativeMode()) {
            if(client.player.getPermissionLevel() < 2) {
                client.player.sendMessage(MutableText.of(new PlainTextContent.Literal("You do not have permission for creative mode!")).withColor(0xD63C3C), false);
                return;
            }

            RooHelper.sendChatCommand("gamemode creative");
            client.player.sendMessage(MutableText.of(new PlainTextContent.Literal("Waiting for creative mode...")).withColor(0xB0B0B0), false);

            kitToLoad = kitName;
            return;
        }

        try {
            var from = NbtHelper.fromNbtProviderString(loadKitFile(kitName));
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
            FireClient.LOGGER.error("Failed to load kit {}!", kitName, e);
        }
    }

    private String loadKitFile(String kitName) {
        var path = getKitPath(kitName);
        var file = new File(path);

        try {
            if(file.exists()) {
                return Files.readString(Paths.get(path));
            }
        }
        catch(Exception e) {
            FireClient.LOGGER.error("Failed to create empty kit file!", e);
        }

        return "{\"inv\":[]}";
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(ButtonWidget.builder(Text.of("+"), this::addKitButtonPressed)
                .dimensions(base.width/2 + 80, base.height/2 - 20, 20, 15)
                .tooltip(Tooltip.of(Text.of("Create kit")))
                .build());

        widgets.add(ButtonWidget.builder(Text.of("\uD83D\uDCC2"), this::folderButtonPressed)
                .tooltip(Tooltip.of(Text.of("Open kits folder (any kit can be shared and loaded with the .json file)")))
                .dimensions(base.width/2 + 105, base.height/2 - 20, 20, 15)
                .build());

        widgets.add(ButtonWidget.builder(Text.of("\uD83D\uDCCB"), this::createFromClipboard)
                .tooltip(Tooltip.of(Text.of("Create kit from clipboard")))
                .dimensions(base.width/2 + 130, base.height/2 - 20, 20, 15)
                .build());

        var client = MinecraftClient.getInstance();

        kitNameField = new TextFieldWidget(client.textRenderer, base.width/2 - 70, base.height/2 - 20, 140, 15, Text.of(""));
        kitNameField.setMaxLength(64);

        widgets.add(kitNameField);

        buttonToKit.clear();

        aboutToDelete = "";
        lastPressed = null;

        var index = 0;
        for(var kit : getKits()) {
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
                        StringSelection stringSelection = new StringSelection(loadKitFile(kit));

                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(stringSelection, null);
                    })
                    .tooltip(Tooltip.of(Text.of("Copy " + kit + " to your clipboard")))
                    .dimensions(base.width/2 + 105, y, 20, 20)
                    .build());

            index++;
        }

        return widgets;
    }

    private void addKitButtonPressed(ButtonWidget button) {
        if(kitNameField == null || kitNameField.getText().isEmpty()) {
            return;
        }

        var kitName = kitNameField.getText();
        saveKit(kitName);

        reloadScreen();
    }

    private void folderButtonPressed(ButtonWidget button) {
        try {
            Desktop.getDesktop().open(new File(KIT_BASE_PATH));
        }
        catch(Exception e) {
            FireClient.LOGGER.error("Failed to navigate to kit folder!", e);
        }
    }

    private void createFromClipboard(ButtonWidget widget) {
        var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        try {
            var kit = "";
            kit = (String)clipboard.getData(DataFlavor.stringFlavor);

            if(kit.isEmpty()) {
                return;
            }

            if(kitNameField == null || kitNameField.getText().isEmpty()) {
                return;
            }

            var kitName = kitNameField.getText();
            var writer = new FileWriter(getKitPath(kitName));

            writer.write(kit);
            writer.close();

            reloadScreen();
        }
        catch(Exception e) {
            FireClient.LOGGER.error("Failed to create kit from clipboard!", e);
        }
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
        }
        else {
            deleteKit(kit);
        }
    }

    private List<String> getKits() {
        var kits = new ArrayList<String>();

        var kitsFolder = new File(KIT_BASE_PATH);
        kitsFolder.mkdirs();

        for(var kit : kitsFolder.listFiles()) {
            kits.add(kit.getName().split("\\.json")[0]);
        }

        return kits;
    }

    private void deleteKit(String kitName) {
        new File(getKitPath(kitName)).delete();

        reloadScreen();
    }

    private void reloadScreen() {
        var client = MinecraftClient.getInstance();
        client.setScreen(new ModuleConfigScreen(this));
    }
}
