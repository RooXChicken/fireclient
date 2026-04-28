package org.loveroo.fireclient.modules;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.JsonOption;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.mixin.modules.localskin.RemapTextureAccessor;
import org.loveroo.fireclient.screen.base.ConfigScreenBase;
import org.loveroo.fireclient.screen.base.ScrollableWidget;
import org.loveroo.fireclient.screen.widgets.CapeRenderWidget;
import org.loveroo.fireclient.screen.widgets.CustomDrawWidget.CustomDrawBuilder;
import org.loveroo.fireclient.screen.widgets.PlayerHeadWidget;
import org.loveroo.fireclient.screen.widgets.ToggleButtonWidget;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StringWidget;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.ClientAsset.ResourceTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public class LocalSkinModule extends ModuleBase {

    private static final HashMap<TextureType, String> TEXTURE_PATHS = new HashMap<>();
    private static final HashMap<TextureType, String> TEXTURE_IDS = new HashMap<>();

    private static final HashMap<TextureType, WatchService> FILE_WATCHES = new HashMap<>();
    
    private static final Color color = Color.fromRGB(0xEBEDA6);
    
    @JsonOption(name = "skin")
    private String skin = "";

    @JsonOption(name = "cape")
    private String cape = "";

    private Optional<ClientAsset.Texture> skinAsset = Optional.empty();
    private Optional<ClientAsset.Texture> capeAsset = Optional.empty();

    // @JsonOption(name = "elytra")
    // private String elytra = "";

    private static final HashMap<String, Boolean> slimSkins = new HashMap<>();
    private static final HashMap<TextureType, HashSet<String>> cache = new HashMap<>();

    private final int skinWidgetWidth = 300;
    private final int skinWidgetHeight = 100;

    private TextureType selectedMenu = TextureType.SKIN;

    @Nullable
    private ScrollableWidget scroll;

    static {
        // init for all textures
        for(var type : TextureType.values()) {
            cache.put(type, new HashSet<>());
        }

        TEXTURE_PATHS.put(TextureType.SKIN, "fireclient/skins/");
        TEXTURE_PATHS.put(TextureType.CAPE, "fireclient/skins/capes/");
        // TEXTURE_PATHS.put(TextureType.ELYTRA, "fireclient/skins/elytras/");

        TEXTURE_IDS.put(TextureType.SKIN, "fireclient_skin_");
        TEXTURE_IDS.put(TextureType.CAPE, "fireclient_cape_");
        // TEXTURE_IDS.put(TextureType.ELYTRA, "fireclient_elytra_");
    }

    public LocalSkinModule() {
        super(new ModuleData("local_skin", "✨", color));

        getData().setGuiElement(false);

        ClientLifecycleEvents.CLIENT_STARTED.register((client) -> {
            if(getData().isEnabled()) {
                for(var type : TextureType.values()) {
                    var texture = getTextureName(type);
                    if(texture.isBlank()) {
                        continue;
                    }
                    
                    uploadTexture(type, texture);
                }
            }

            initializeDirectories();

            for(var type : TextureType.values()) {
                try {
                    var watch = FileSystems.getDefault().newWatchService();
                    var path = Paths.get(TEXTURE_PATHS.get(type));
    
                    path.register(watch, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);

                    FILE_WATCHES.put(type, watch);
                }
                catch(Exception e) {
                    FireClient.LOGGER.error("Failed to initialize file watch for {}!", type.name(), e);
                }
            }
        });
    }

    @Override
    public void update(Minecraft client) {
        if(!getData().isEnabled()) {
            return;
        }

        for(var type : TextureType.values()) {
            pollWatch(type, FILE_WATCHES.get(type));
        }
    }

    private void pollWatch(TextureType type, @Nullable WatchService watch) {
        if(watch == null) {
            return;
        }

        try {
            var key = watch.poll();
            if(key == null) {
                return;
            }
    
            for(var event : key.pollEvents()) {
                if(event.kind() == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                var path = (Path) event.context();
                key.reset();

                var texture = getTextureName(type);

                if(!texture.equals(filterName(path.toString()))) {
                    continue;
                }
    
                uploadTexture(type, texture);
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

    private void uploadTexture(TextureType type, String path) {
        if(path.isBlank()) {
            return;
        }

        initializeDirectories();

        RenderSystem.queueFencedTask(() -> {
            var id = getIdentifier(type, path);

            try {
                var data = Files.readAllBytes(Paths.get(TEXTURE_PATHS.get(type) + path + ".png"));
                var image = NativeImage.read(data);

                if(type == TextureType.SKIN) {
                    image = RemapTextureAccessor.invokeProcessLegacySkin(image, path);
                }

                var texture = new DynamicTexture(() -> { return path; }, image);

                var client = Minecraft.getInstance();
                client.getTextureManager().register(id, texture);

                cache.get(type).add(path);
                applyTexture(type, path);
            }
            catch(Exception e) {
                FireClient.LOGGER.error("Failed to load {} {}!", type.name(), path, e);
            }
        });

    }

    public void initializeDirectories() {
        try {
            for(var type : TextureType.values()) {
                new File(TEXTURE_PATHS.get(type)).mkdirs();
            }
        }
        catch(Exception ignored) { }
    }

    public List<String> getTextures(TextureType type) {
        var textures = new ArrayList<String>();
        initializeDirectories();

        try {
            var textureFolder = new File(TEXTURE_PATHS.get(type));

            for(var textureFile : textureFolder.listFiles()) {
                var texture = textureFile.getName();
                var textureName = filterName(texture);

                if(textureName.isEmpty()) {
                    continue;
                }

                textures.add(textureName);
            }
        }
        catch(Exception e) {
            FireClient.LOGGER.error("Failed to gather {} list!", type.name(), e);
        }

        textures.sort(Comparator.comparing(String::toLowerCase));

        return textures;
    }

    private String filterName(String fileName) {
        if(!fileName.endsWith(".png")) {
            return "";
        }

        return fileName.substring(0, fileName.length() - 4);
    }

    @Override
    public List<AbstractWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<AbstractWidget>();

        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 + 95));

        widgets.add(new Button.Builder(Component.translatable("fireclient.module.local_skin.open_folder.name"), this::folderButtonPressed)
            .tooltip(Tooltip.create(Component.translatable("fireclient.module.local_skin.open_folder.tooltip")))
            .bounds(base.width/2 - 147, base.height/2 - 30, 20, 15)
            .build());

        var index = 0;
        for(var type : TextureType.values()) {
            var selectButton = new Button.Builder(Component.translatable(String.format("fireclient.module.local_skin.select_%s.name", type.name().toLowerCase())), (button) -> setSubMenu(type))
                .tooltip(Tooltip.create(Component.translatable(String.format("fireclient.module.local_skin.select_%s.tooltip", type.name().toLowerCase()))))
                .bounds(base.width/2 - 122 + (index * 45), base.height/2 - 30, 40, 15)
                .build();
            
            if(type == selectedMenu) {
                selectButton.active = false;
            }

            widgets.add(selectButton);

            index++;
        }

        var entries = new ArrayList<ScrollableWidget.ElementEntry>();
        var textures = getTextures(selectedMenu);
        
        for(var texture : textures) {
            var entryWidgets = new ArrayList<AbstractWidget>();

            if(!cache.get(selectedMenu).contains(texture)) {
                uploadTexture(selectedMenu, texture);
            }

            var selectedSkin = new CustomDrawBuilder()
                .position(base.width/2, 0)
                .onDraw((context, mx, my, d) -> {
                    if(!getTextureName(selectedMenu).equals(texture)) {
                        return;
                    }

                    var x2 = switch(selectedMenu) {
                        case TextureType.SKIN -> 68;
                        case TextureType.CAPE -> 113;
                    };

                    context.fill(-142, 0, x2, 16, 0x55FFFFFF);
                })
                .build();

            entryWidgets.add(selectedSkin);

            switch(selectedMenu) {
                case TextureType.SKIN -> {
                    var head = new PlayerHeadWidget(texture, getIdentifier(selectedMenu, texture), base.width/2 - 140, 2);
                    entryWidgets.add(head);
                }

                case TextureType.CAPE -> {
                    var cape = new CapeRenderWidget(texture, getIdentifier(selectedMenu, texture), base.width/2 - 136, 2, 0.7f);
                    entryWidgets.add(cape);
                }
            }

            var text = new StringWidget(Component.literal(texture), base.getFont());
            text.setPosition(base.width/2 - 120, 4);

            entryWidgets.add(text);

            entryWidgets.add(new Button.Builder(Component.translatable("fireclient.module.local_skin.apply.name"), (button) -> applyTexture(selectedMenu, texture))
                .bounds(base.width/2 + 115, 0, 20, 16)
                .tooltip(Tooltip.create(Component.translatable("fireclient.module.local_skin.apply.tooltip", texture)))
                .build());

            if(selectedMenu == TextureType.SKIN) {
                entryWidgets.add(new ToggleButtonWidget.ToggleButtonBuilder(Component.translatable("fireclient.module.local_skin.slim.name"))
                    .getValue(() -> { return slimSkins.getOrDefault(texture, false); })
                    .setValue((value) -> { slimSkins.put(texture, value); })
                    .dimensions(base.width/2 + 70, 0, 40, 16)
                    .tooltip(Tooltip.create(Component.translatable("fireclient.module.local_skin.slim.tooltip", texture)))
                    .build());
            }

            entries.add(new ScrollableWidget.ElementEntry(entryWidgets));
        }

        scroll = new ScrollableWidget(base, skinWidgetWidth, skinWidgetHeight, 0, 20, entries);
        scroll.setPosition(base.width/2 - (skinWidgetWidth/2), base.height/2 - 10);

        widgets.add(scroll);
        return widgets;
    }

    private void folderButtonPressed(Button button) {
        Util.getPlatform().openFile(new File(TEXTURE_PATHS.get(selectedMenu)));
    }

    private void setSubMenu(TextureType type) {
        selectedMenu = type;
        reloadScreen();
    }

    private void applyTexture(TextureType type, String texture) {
        var current = getTextureName(type);
        if(current.equals(texture)) {
            switch(type) {
                case TextureType.SKIN -> { skin = ""; skinAsset = Optional.empty(); }
                case TextureType.CAPE -> { cape = ""; capeAsset = Optional.empty(); }
            }
        }
        else {
            switch(type) {
                case TextureType.SKIN -> {
                    skin = texture;

                    var id = getIdentifier(type, texture);
                    skinAsset = Optional.of(new ResourceTexture(
                        id,
                        id
                    ));
                }
                
                case TextureType.CAPE -> {
                    cape = texture;

                    var id = getIdentifier(type, texture);
                    capeAsset = Optional.of(new ResourceTexture(
                        id,
                        id
                    ));
                }
            }
        }
    }

    public static Identifier getIdentifier(TextureType type, String filePath) {
        if(filePath.isBlank()) {
            return null;
        }

        return Identifier.fromNamespaceAndPath(FireClient.MOD_ID, pathToTexture(type, filePath));
    }

    public static String pathToTexture(TextureType type, String filePath) {
        return TEXTURE_IDS.get(type) + RooHelper.filterIdInput(filePath.toLowerCase());
    }

    public Optional<ClientAsset.Texture> getAsset(TextureType type) {
        return switch(type) {
            case SKIN -> skinAsset;
            case CAPE -> capeAsset;
        };
    }

    private String getTextureName(TextureType type) {
        return switch(type) {
            case TextureType.SKIN -> skin;
            case TextureType.CAPE -> cape;
            // case TextureType.ELYTRA -> elytra;
        };
    }

    @Nullable
    public String getModel() {
        if(getAsset(TextureType.SKIN).isEmpty()) {
            return null;
        }

        var slim = slimSkins.getOrDefault(skin, false);
        return (slim) ? "SLIM" : "WIDE";
    }

    @Override
    public void drawScreen(Screen base, GuiGraphicsExtractor context, float delta) {
        super.drawScreenHeader(context, base.width/2, base.height/2 - 100);

        var client = Minecraft.getInstance();
        if(client.player == null) {
            return;
        }

        int i = base.width/4;
        int j = base.height/4 - 7;

        float pitchOffset;
        float mouseX;
        if(selectedMenu == TextureType.SKIN) {
            pitchOffset = 0.0f;
            mouseX = ((ConfigScreenBase)base).getMouseX();
        }
        else {
            pitchOffset = 180.0f;
            mouseX = base.width-((ConfigScreenBase)base).getMouseX();
        }

        // TODO: fix model not updating in 1.21.6-1.21.8
        RooHelper.drawPlayer(context, i, j, 0.0625F, mouseX, ((ConfigScreenBase)base).getMouseY(), pitchOffset, 0f);
    }

    public static enum TextureType {
        
        SKIN,
        CAPE,
        // ELYTRA,
    }
}
