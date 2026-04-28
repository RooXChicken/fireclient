package org.loveroo.fireclient.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;
import org.loveroo.fireclient.screen.base.ScrollableWidget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class CommandKeysModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0x78B388);

    private final List<CommandKeybind> commandKeys = new ArrayList<>();

    private static double scrollPos = 0.0;
    private final int commandsWidgetWidth = 300;
    private final int commandsWidgetHeight = 140;

    @Nullable
    private ScrollableWidget scroll;

    public CommandKeysModule() {
        super(new ModuleData("command_keys", "🛠", color));

        getData().setEnabled(true);
        getData().setGuiElement(false);
    }

    @Override
    public void loadJson(JSONObject json) throws Exception {
        super.loadJson(json);

        var keyList = json.optJSONArray("commands");
        if(keyList == null) {
            keyList = new JSONArray();
        }

        for(var i = 0; i < keyList.length(); i++) {
            var commandJson = keyList.optJSONObject(i);
            if(commandJson == null) {
                continue;
            }

            var id = commandJson.optString("id", UUID.randomUUID().toString());
            var command = commandJson.optString("command", "");

            var commandKey = new CommandKeybind(id, command);

            createCommandKeybind(commandKey);
            commandKeys.add(commandKey);
        }
    }

    @Override
    public JSONObject saveJson() throws Exception {
        var json = super.saveJson();

        var keyList = new JSONArray();

        for(var command : commandKeys) {
            var commandJson = new JSONObject();

            commandJson.put("id", command.getId());
            commandJson.put("command", command.getCommand());

            keyList.put(commandJson);
        }

        json.put("commands", keyList);

        return json;
    }

    @Override
    public void moduleConfigPressed(Button button) {
        scrollPos = 0.0;
        super.moduleConfigPressed(button);
    }

    @Override
    public List<AbstractWidget> getConfigScreen(Screen base) {
        var client = Minecraft.getInstance();
        var widgets = new ArrayList<AbstractWidget>();

        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 + 95));

        var entries = new ArrayList<ScrollableWidget.ElementEntry>();

        for(var command : commandKeys) {
            var entryWidgets = new ArrayList<AbstractWidget>();

            var removeButton = Button.builder(Component.translatable("fireclient.module.command_keys.remove_command.name").withColor(0xD63C3C), (button) -> removeCommand(command))
                .bounds(base.width/2 + 115, 2,20,15)
                .tooltip(Tooltip.create(Component.translatable("fireclient.module.command_keys.remove_command.tooltip", command.getCommand())))
                .build();
            
            createCommandKeybind(command);

            var keybind =  FireClientside.getKeybindManager().getKeybind(getCommandKeyName(command));
            var keybindButton = keybind.getRebindButton(base.width/2 + 30, 0, 80, 20);

            var text = new EditBox(client.font, 160, 15, Component.literal(""));
            text.setValue(command.getCommand());
            text.setPosition(base.width/2 - 140, 2);
            text.setResponder((input) -> {
                command.setCommand(input);
                removeButton.setTooltip(Tooltip.create(Component.translatable("fireclient.module.command_keys.remove_command.tooltip", command.getCommand())));

                keybind.setDescription(Component.translatable("fireclient.module.command_keys.run_command.tooltip", command.getCommand()));
                keybindButton.setTooltip(Tooltip.create(keybind.getDescription()));
            });

            entryWidgets.add(text);
            entryWidgets.add(keybindButton);
            entryWidgets.add(removeButton);

            entries.add(new ScrollableWidget.ElementEntry(entryWidgets));
        }

        var addButton = Button.builder(Component.translatable("fireclient.module.command_keys.add_command.name"), (button) -> addCommand())
            .bounds(base.width/2 - 60, 0, 120, 20)
            .tooltip(Tooltip.create(Component.translatable("fireclient.module.command_keys.add_command.tooltip")))
            .build();

        entries.add(new ScrollableWidget.ElementEntry(List.of(addButton)));

        scroll = new ScrollableWidget(base, commandsWidgetWidth, commandsWidgetHeight, 0, 25, entries);
        scroll.setScrollAmount(scrollPos);
        scroll.setPosition(base.width/2 - (commandsWidgetWidth/2), base.height/2 - 50);

        widgets.add(scroll);
        return widgets;
    }

    private void addCommand() {
        var command = new CommandKeybind(UUID.randomUUID().toString(), "");
        createCommandKeybind(command);
        commandKeys.add(command);

        reloadScreen();
    }

    private void removeCommand(CommandKeybind command) {
        FireClientside.getKeybindManager().unregisterKeybind(getCommandKeyName(command));
        commandKeys.remove(command);

        reloadScreen();
    }

    private void createCommandKeybind(CommandKeybind command) {
        var keyId = getCommandKeyName(command);
        if(FireClientside.getKeybindManager().hasKey(keyId)) {
            return;
        }
        
        var keybind = new Keybind(keyId,
            Component.translatable("fireclient.module.command_keys.run_command.name"),
            Component.translatable("fireclient.module.command_keys.run_command.tooltip", command.getCommand()),
        true, null,
        () -> useCommandKey(command), null);
        
        keybind.setShortName(true);
        FireClientside.getKeybindManager().registerKeybind(keybind);
    }

    private void useCommandKey(CommandKeybind command) {
        if(!getData().isEnabled()) {
            return;
        }

        RooHelper.sendChatCommand(command.getCommand());
    }

    private String getCommandKeyName(CommandKeybind command) {
        return "use_key_" + command.getId();
    }

    @Override
    public void drawScreen(Screen base, GuiGraphicsExtractor context, float delta) {
        if(scroll != null) {
            scrollPos = scroll.scrollAmount();
        }

        drawScreenHeader(context, base.width/2, base.height/2 - 70);
    }

    @Override
    public void closeScreen(Screen screen) {
        FireClientside.saveConfig();
    }

    static class CommandKeybind {

        private final String id;
        private String command;

        public CommandKeybind(String id, String command) {
            this.id = id;
            this.command = command;
        }

        public String getId() {
            return id;
        }

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }
    }
}
