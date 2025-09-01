package org.loveroo.fireclient.client;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.sun.jdi.connect.Connector;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.commands.FCalcCommand;
import org.loveroo.fireclient.commands.FKitCommand;
import org.loveroo.fireclient.commands.FTextCommand;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.FireClientOption;
import org.loveroo.fireclient.keybind.KeybindManager;
import org.loveroo.fireclient.modules.*;
import org.loveroo.fireclient.screen.config.MainConfigScreen;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;

public class FireClientside implements ClientModInitializer {

    public static final Color mainColor1 = new Color(213, 61, 49, 255);
    public static final Color mainColor2 = new Color(225, 166, 55, 255);

    private static final String FIRECLIENT_OLD_CONFIG_PATH = "fireclient.json";
    
    private static final String FIRECLIENT_PATH = "fireclient/";
    private static final String FIRECLIENT_CONFIG_FILE = "config.json";
    private static final String FIRECLIENT_CONFIG_BACKUP_FILE = "config_bk.json";

    private static final HashMap<String, ModuleBase> modules = new HashMap<>();
    private static final HashMap<FireClientOption, Integer> settings = new HashMap<>();

    private static final KeybindManager keybindManager = new KeybindManager();
    private final KeyBinding moduleConfigKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.fireclient.module_config", GLFW.GLFW_KEY_RIGHT_SHIFT, FireClient.KEYBIND_CATEGORY));

    @Override
    public void onInitializeClient() {
        initModules();
        loadConfig();

        ClientTickEvents.END_CLIENT_TICK.register(this::update);

        for(var module : getModules()) {
            module.postLoad();
        }

        registerCommands();
//        new RecipeManager();
    }

    private void initModules() {
        registerModule(new ArmorDisplayModule());
        registerModule(new CoordinatesModule());
        registerModule(new ToggleToggleSneakModule());
        registerModule(new FPSDisplayModule());
//        registerModule(new LocalDifficultyFinderModule());
//        registerModule(new RenderWorldModule());
        registerModule(new CoordsChatModule());
        registerModule(new NametagModule());
        registerModule(new AutoMessageModule());
        registerModule(new DeathInfoModule());
        registerModule(new FlightSpeedModule());
        registerModule(new HitColorModule());
        registerModule(new ShadowModule());
        registerModule(new BigItemsModule());
        registerModule(new IndicatorsModule());
        registerModule(new KitModule());
        registerModule(new ScrollClickModule());
        registerModule(new ElytraSwapModule());
        registerModule(new AngleDisplayModule());
        registerModule(new HealthDisplayModule());
        registerModule(new BlockOutlineModule());
        registerModule(new EntityCountModule());
        registerModule(new HighestBlockModule());
        registerModule(new SubtitlesModule());
        registerModule(new FullbrightModule());
        registerModule(new PerspectiveModule());
        registerModule(new MuteSoundsModule());
        registerModule(new SignModule());
        registerModule(new ParticlesModule());
    }

    public static void registerModule(ModuleBase module) {
        modules.put(module.getData().getId(), module);
    }

    private void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register(new FCalcCommand()::register);
        ClientCommandRegistrationCallback.EVENT.register(new FKitCommand()::register);
        ClientCommandRegistrationCallback.EVENT.register(new FTextCommand()::register);
    }

    private void loadConfig() {
        try {
            new File(FIRECLIENT_PATH).mkdir();

            var config = new File(FIRECLIENT_PATH + FIRECLIENT_CONFIG_FILE);
            var oldConfig = new File(FIRECLIENT_OLD_CONFIG_PATH);

            if(oldConfig.exists()) {
                oldConfig.renameTo(config);
            }

            var data = "{}";

            // we still load even if the save file is empty because all load functions are safe
            // this is actually preferred so we can use default values
            if(config.exists()) {
                createBackup();
                data = Files.readString(Paths.get(FIRECLIENT_PATH + FIRECLIENT_CONFIG_FILE));
            }

            var json = new JSONObject(data);

            var settings = json.optJSONObject("settings");
            if(settings == null) {
                settings = new JSONObject();
            }

            for(var option : FireClientOption.values()) {
                getSettings().put(option, settings.optInt(option.name().toLowerCase(), option.getDefaultValue()));
            }

            var modules = json.optJSONObject("modules");
            if(modules == null) {
                modules = new JSONObject();
            }

            for(var module : getModules()) {
                try {
                    var moduleJson = modules.optJSONObject(module.getData().getId());
                    if(moduleJson == null) {
                        moduleJson = new JSONObject();
                    }

                    module.loadJson(moduleJson);
                }
                catch(Exception e) {
                    FireClient.LOGGER.error("Failed to load module {}!", module.getData().getId(), e);
                }
            }

            var keybinds = json.optJSONObject("keybinds");
            if(keybinds == null) {
                keybinds = new JSONObject();
            }

            for(var keybind : keybindManager.getKeybinds()) {
                try {
                    var keybindJson = keybinds.optJSONArray(keybind.getId());
                    if(keybindJson == null) {
                        keybindJson = new JSONArray();
                    }

                    keybind.loadJson(keybindJson);
                }
                catch(Exception e) {
                    FireClient.LOGGER.error("Failed to load keybind {}!", keybind.getId(), e);
                }
            }
        }
        catch(Exception e) {
            FireClient.LOGGER.error("Failed to load config file!", e);
        }
    }

    public static void saveConfig() {
        try {
            var json = new JSONObject();

            var settings = new JSONObject();
            for(var option : FireClientOption.values()) {
                settings.put(option.name().toLowerCase(), getSettings().getOrDefault(option, option.getDefaultValue()));
            }

            var modules = new JSONObject();
            for(var module : getModules()) {
                try {
                    modules.put(module.getData().getId(), module.saveJson());
                }
                catch(Exception e) {
                    FireClient.LOGGER.error("Failed to save module {}!", module.getData().getId(), e);
                }
            }

            var keybinds = new JSONObject();
            for(var keybind : keybindManager.getKeybinds()) {
                try {
                    keybinds.put(keybind.getId(), keybind.saveJson());
                }
                catch(Exception e) {
                    FireClient.LOGGER.error("Failed to save keybind {}!", keybind.getId(), e);
                }
            }

            json.put("settings", settings);
            json.put("modules", modules);
            json.put("keybinds", keybinds);

            new File(FIRECLIENT_PATH).mkdir();

            var config = new File(FIRECLIENT_PATH + FIRECLIENT_CONFIG_FILE);
            var writer = new FileWriter(config);

            writer.write(json.toString());
            writer.close();
        }
        catch(Exception e) {
            FireClient.LOGGER.error("Failed to save config file!", e);
        }
    }

    private static void createBackup() throws IOException {
        Files.copy(Path.of(FIRECLIENT_PATH + FIRECLIENT_CONFIG_FILE), Path.of(FIRECLIENT_PATH + FIRECLIENT_CONFIG_BACKUP_FILE), StandardCopyOption.REPLACE_EXISTING);
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
        return getSettings().getOrDefault(option, option.getDefaultValue());
    }

    public static void setSetting(FireClientOption option, int value) {
        getSettings().put(option, value);
    }

    public static KeybindManager getKeybindManager() {
        return keybindManager;
    }
}
