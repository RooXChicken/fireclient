package org.loveroo.fireclient.modules;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.mojang.blaze3d.systems.RenderSystem;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.JsonOption;
import org.loveroo.fireclient.data.KitManager;
import org.loveroo.fireclient.data.KitManager.KitValidationStatus;
import org.loveroo.fireclient.mixin.modules.localskin.RemapTextureAccessor;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.screen.base.ConfigScreenBase;
import org.loveroo.fireclient.screen.base.ScrollableWidget;
import org.loveroo.fireclient.screen.widgets.CustomDrawWidget;
import org.loveroo.fireclient.screen.widgets.CustomDrawWidget.CustomDrawBuilder;
import org.loveroo.fireclient.screen.widgets.PlayerHeadWidget;
import org.loveroo.fireclient.screen.widgets.ToggleButtonWidget;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class LocalSkinModule extends ModuleBase {

    private static final String SKIN_PATH = "fireclient/skins/";
    private static final String SKIN_ID = "fireclient_skin_";
    
    private static final Color color = Color.fromRGB(0xEBEDA6);
    
    @JsonOption(name = "skin")
    private String skin = "";

    private static final HashMap<String, Boolean> slimSkins = new HashMap<>();
    private static final HashSet<String> cachedSkins = new HashSet<>();

    private final int skinWidgetWidth = 300;
    private final int skinWidgetHeight = 100;

    @Nullable
    private ScrollableWidget scroll;

    @Nullable
    private WatchService fileWatch = null;

    public LocalSkinModule() {
        super(new ModuleData("local_skin", "✨", color));

        getData().setGuiElement(false);

        ClientLifecycleEvents.CLIENT_STARTED.register((client) -> {
            if(getData().isEnabled() && !skin.isBlank()) {
                uploadSkin(skin);
            }

            initializeDirectories();

            try {
                fileWatch = FileSystems.getDefault().newWatchService();
                var path = Paths.get(SKIN_PATH);

                path.register(fileWatch, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);
            }
            catch(Exception e) {
                FireClient.LOGGER.error("Failed to initialize file watch!", e);
            }
        });
    }

    @Override
    public void update(MinecraftClient client) {
        if(!getData().isEnabled() || fileWatch == null) {
            return;
        }

        try {
            var key = fileWatch.poll();
            if(key == null) {
                return;
            }
    
            for(var event : key.pollEvents()) {
                if(event.kind() == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                var path = (Path) event.context();
                key.reset();

                if(!skin.equals(filterName(path.toString()))) {
                    continue;
                }
    
                uploadSkin(skin);
            }

        }
        catch(Exception e) {
            FireClient.LOGGER.error("Failed to poll for skin changes!", e);
        }
    }

    @Override
    public void loadJson(JSONObject json) throws Exception {
        super.loadJson(json);

        var slimList = json.optJSONArray("slim_list");
        if(slimList == null) {
            slimList = new JSONArray();
        }

        for(var i = 0; i < slimList.length(); i++) {
            var slimEntry = slimList.optJSONObject(i);
            if(slimEntry == null) {
                continue;
            }

            var skinName = slimEntry.optString("skin", "");
            if(skinName.isEmpty()) {
                continue;
            }

            var slim = slimEntry.optBoolean("slim", false);
            slimSkins.put(skinName, slim);
        }
    }

    @Override
    public JSONObject saveJson() throws Exception {
        var json = super.saveJson();
        var slimList = new JSONArray();

        for(var entry : slimSkins.entrySet()) {
            var slimEntry = new JSONObject();

            slimEntry.put("skin", entry.getKey());
            slimEntry.put("slim", entry.getValue());

            slimList.put(slimEntry);
        }

        json.put("slim_list", slimList);

        return json;
    }

    private void uploadSkin(String path) {
        if(path.isBlank()) {
            return;
        }

        initializeDirectories();

        RenderSystem.queueFencedTask(() -> {
            var id = getSkinIdentifier(path);

            try {
                var data = Files.readAllBytes(Paths.get(SKIN_PATH + path + ".png"));
                var image = NativeImage.read(data);
                var remapped = RemapTextureAccessor.invokeRemapTexture(image, path);

                var texture = new NativeImageBackedTexture(() -> { return path; }, remapped);

                var client = MinecraftClient.getInstance();
                client.getTextureManager().registerTexture(id, texture);

                cachedSkins.add(path);
            }
            catch(Exception e) {
                FireClient.LOGGER.error("Failed to load skin {}!", path, e);
            }
        });
    }

    public void initializeDirectories() {
        try {
            new File(SKIN_PATH).mkdirs();
        }
        catch(Exception ignored) { }
    }

    public List<String> getSkins() {
        var skins = new ArrayList<String>();
        initializeDirectories();

        try {
            var skinFolder = new File(SKIN_PATH);

            for(var skinFile : skinFolder.listFiles()) {
                var skin = skinFile.getName();
                var skinName = filterName(skin);

                if(skinName.isEmpty()) {
                    continue;
                }

                skins.add(skinName);
            }
        }
        catch(Exception e) {
            FireClient.LOGGER.error("Failed to gather skin list!", e);
        }

        skins.sort(Comparator.comparing(String::toLowerCase));

        return skins;
    }

    private String filterName(String fileName) {
        if(!fileName.endsWith(".png")) {
            return "";
        }

        return fileName.substring(0, fileName.length() - 4);
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 + 95));

        widgets.add(new ButtonWidget.Builder(Text.translatable("fireclient.module.local_skin.open_folder.name"), this::folderButtonPressed)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.local_skin.open_folder.tooltip")))
            .dimensions(base.width/2 - 147, base.height/2 - 30, 20, 15)
            .build());

        var skins = getSkins();

        var entries = new ArrayList<ScrollableWidget.ElementEntry>();
        for(var skin : skins) {
            var entryWidgets = new ArrayList<ClickableWidget>();

            if(!cachedSkins.contains(skin)) {
                uploadSkin(skin);
            }

            var selectedSkin = new CustomDrawBuilder()
                .position(base.width/2, 0)
                .onDraw((context, mx, my, d) -> {
                    if(!this.skin.equals(skin)) {
                        return;
                    }

                    context.fill(-142, 0, 68, 16, 0x55FFFFFF);
                })
                .build();

            entryWidgets.add(selectedSkin);

            var head = new PlayerHeadWidget(skin, getSkinIdentifier(skin), base.width/2 - 140, 2);
            entryWidgets.add(head);

            var text = new TextWidget(Text.literal(skin), base.getTextRenderer());
            text.setPosition(base.width/2 - 120, 4);

            entryWidgets.add(text);

            entryWidgets.add(new ButtonWidget.Builder(Text.translatable("fireclient.module.local_skin.apply.name"), (button) -> applySkin(skin))
                .dimensions(base.width/2 + 115, 0, 20, 16)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.local_skin.apply.tooltip", skin)))
                .build());

            entryWidgets.add(new ToggleButtonWidget.ToggleButtonBuilder(Text.translatable("fireclient.module.local_skin.slim.name"))
                .getValue(() -> { return slimSkins.getOrDefault(skin, false); })
                .setValue((value) -> { slimSkins.put(skin, value); })
                .dimensions(base.width/2 + 70, 0, 40, 16)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.local_skin.slim.tooltip", skin)))
                .build());

            entries.add(new ScrollableWidget.ElementEntry(entryWidgets));
        }

        scroll = new ScrollableWidget(base, skinWidgetWidth, skinWidgetHeight, 0, 20, entries);
        scroll.setPosition(base.width/2 - (skinWidgetWidth/2), base.height/2 - 10);

        widgets.add(scroll);
        return widgets;
    }

    private void folderButtonPressed(ButtonWidget button) {
        Util.getOperatingSystem().open(new File(SKIN_PATH));
    }

    private void applySkin(String skin) {
        if(this.skin.equals(skin)) {
            this.skin = "";
        }
        else {
            this.skin = skin;
        }
    }

    @Nullable
    public static Identifier getSkinIdentifier(String filePath) {
        if(filePath.isBlank()) {
            return null;
        }

        return Identifier.of(FireClient.MOD_ID, pathToSkin(filePath));
    }

    public static String pathToSkin(String filePath) {
        return SKIN_ID + RooHelper.filterIdInput(filePath.toLowerCase());
    }

    @Nullable
    public Identifier getSkin() {
        return getSkinIdentifier(skin);
    }

    @Nullable
    public String getModel() {
        if(getSkin() == null) {
            return null;
        }

        var slim = slimSkins.getOrDefault(skin, false);
        return (slim) ? "SLIM" : "WIDE";
    }

    @Override
    public void drawScreen(Screen base, DrawContext context, float delta) {
        super.drawScreenHeader(context, base.width/2, base.height/2 - 100);

        var client = MinecraftClient.getInstance();
        if(client.player == null) {
            return;
        }

        int i = base.width/4;
        int j = base.height/4 - 7;

        float scale = 1.0f;
        int off = 50;

        InventoryScreen.drawEntity(context, (i+26-off)*2, (j-8-off)*2, (i+75-off)*2, (j+78-off)*2, (int)(30*scale), 0.0625F, ((ConfigScreenBase)base).getMouseX(), ((ConfigScreenBase)base).getMouseY(), client.player);
    }
}
