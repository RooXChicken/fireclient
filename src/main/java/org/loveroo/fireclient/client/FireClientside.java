package org.loveroo.fireclient.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.FireClientOption;
import org.loveroo.fireclient.modules.*;
import org.loveroo.fireclient.screen.config.MainConfigScreen;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class FireClientside implements ClientModInitializer {

    public static final Color mainColor1 = new Color(213, 61, 49, 255);
    public static final Color mainColor2 = new Color(225, 166, 55, 255);

    private static final String FIRECLIENT_CONFIG_PATH = "fireclient.json";
    private static final HashMap<FireClientOption, Integer> settings = new HashMap<>();

    private static final HashMap<String, ModuleBase> modules = new HashMap<>();
    private final KeyBinding moduleConfigKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.fireclient.module_config", GLFW.GLFW_KEY_RIGHT_SHIFT, FireClient.KEYBIND_CATEGORY));

    @Override
    public void onInitializeClient() {
        initModules();
        loadConfig();

        ClientTickEvents.END_CLIENT_TICK.register(this::update);
    }

    private void initModules() {
        registerModule(new ArmorDisplayModule());
        registerModule(new CoordinatesModule());
        registerModule(new ToggleToggleSneakModule());
        registerModule(new FPSDisplayModule());
        registerModule(new LocalDifficultyFinderModule());
        registerModule(new RenderWorldModule());
        registerModule(new CoordsChatModule());
        registerModule(new NametagModule());
        registerModule(new AutoMessageModule());
        registerModule(new DeathInfoModule());
    }

    private void registerModule(ModuleBase module) {
        modules.put(module.getData().getId(), module);
    }

    private void loadConfig() {
        try {
            var config = new File(FIRECLIENT_CONFIG_PATH);

            if(!config.exists()) {
                saveConfig();
            }

            var reader = new FileReader(config);
            var scanner = new Scanner(reader);

            var data = new StringBuilder();
            while(scanner.hasNext()) {
                data.append(scanner.next());
            }

            var json = new JSONObject(data.toString());

            var settings = json.optJSONObject("settings");
            if(settings == null) {
                settings = new JSONObject();
            }

            for(FireClientOption option : FireClientOption.values()) {
                getSettings().put(option, settings.optInt(option.name().toLowerCase(), 0));
            }

            var modules = json.optJSONObject("modules");
            if(modules == null) {
                modules = new JSONObject();
            }

            for(ModuleBase module : getModules()) {
                var moduleJson = modules.optJSONObject(module.getData().getId());
                if(moduleJson == null) {
                    moduleJson = new JSONObject();
                }

                module.loadJson(moduleJson);
            }
        }
        catch(Exception e) {
            FireClient.LOGGER.error("Failed to load config file!", e);
        }
    }

    public static void saveConfig() {
        try {
            var config = new File(FIRECLIENT_CONFIG_PATH);
            var writer = new FileWriter(config);

            var json = new JSONObject();

            var settings = new JSONObject();
            for(FireClientOption option : FireClientOption.values()) {
                settings.put(option.name().toLowerCase(), getSettings().getOrDefault(option, option.getDefaultValue()));
            }

            var modules = new JSONObject();
            for(ModuleBase module : getModules()) {
                modules.put(module.getData().getId(), module.saveJson());
            }

            json.put("settings", settings);
            json.put("modules", modules);

            writer.write(json.toString());
            writer.close();
        }
        catch(Exception e) {
            FireClient.LOGGER.error("Failed to save config file!", e);
        }
    }

    private void update(MinecraftClient client) {
        if(moduleConfigKey.wasPressed()) {
            client.setScreen(new MainConfigScreen());
        }

        for(var module : modules.values()) {
            module.update(client);
        }
    }

    public static List<ModuleBase> getModules() {
        return modules.values().stream().toList();
    }

    @Nullable
    public static ModuleBase getModule(String id) {
        return modules.getOrDefault(id, null);
    }

    private static HashMap<FireClientOption, Integer> getSettings() {
        return settings;
    }

    public static int getSetting(FireClientOption option) {
        return getSettings().getOrDefault(option, 0);
    }

    public static void setSetting(FireClientOption option, int value) {
        getSettings().put(option, value);
    }
}
