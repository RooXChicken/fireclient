package org.loveroo.fireclient.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.JsonOption;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;
import org.loveroo.fireclient.screen.base.ScrollableWidget;
import org.loveroo.fireclient.screen.widgets.CustomDrawWidget;
import org.loveroo.fireclient.screen.widgets.ToggleButtonWidget.ToggleButtonBuilder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SoundsModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0xFA8A73);

    private final List<MutedSound> mutedSounds = new ArrayList<>();

    private static double scrollPos = 0.0;
    private final int soundsWidgetWidth = 300;
    private final int soundsWidgetHeight = 100;

    @JsonOption(name = "use_human_names")
    private boolean useHumanNames = true;

    @Nullable
    private ScrollableWidget scroll;

    @Nullable
    private TextFieldWidget soundField;

    @Nullable
    private TextWidget suggestionField;

    private static final HashMap<String, String> idToHuman = new HashMap<>();
    private static final HashMap<String, String> humanToId = new HashMap<>();

    static {
        // makes sound ids into human understandable names
        for(var sound : Registries.SOUND_EVENT) {
            var path = sound.id().getPath();
            var spaced = path.replaceAll("[\\s.-_]", " ");

            // var firstSpaceIndex = spaced.indexOf(" ");
            // if(firstSpaceIndex == -1) {
            //     firstSpaceIndex = 0;
            // }

            var name = new StringBuilder();

            var words = spaced.split(" ");
            for(var word : words) {
                if(word.isBlank()) {
                    continue;
                }

                name.append(Character.toUpperCase(word.charAt(0)));
                name.append(word.substring(1));

                name.append(" ");
            }

            idToHuman.put(path, name.toString());
            humanToId.put(name.toString().toLowerCase(), path);
        }
    }

    public SoundsModule() {
        super(new ModuleData("sounds", "\uD83D\uDD07", color));

        getData().setEnabled(true);
        getData().setGuiElement(false);

        var toggleBind = new Keybind("toggle_sounds",
                Text.translatable("fireclient.keybind.generic.toggle.name"),
                Text.translatable("fireclient.keybind.generic.toggle.description", getData().getShownName()),
                true, null,
                () -> getData().setEnabled(!getData().isEnabled()), null);

        FireClientside.getKeybindManager().registerKeybind(toggleBind);
    }

    @Override
    public void loadJson(JSONObject json) throws Exception {
        super.loadJson(json);

        var soundList = json.optJSONArray("sounds");
        if(soundList == null) {
            soundList = new JSONArray();
        }

        for(var i = 0; i < soundList.length(); i++) {
            var sound = soundList.optJSONObject(i);
            if(sound == null) {
                continue;
            }

            var volume = sound.optDouble("volume", 1.0);
            if(sound.optBoolean("enabled", false)) {
                volume = 0.0;
            }

            mutedSounds.add(new MutedSound(sound.optString("id", ""), volume));
        }
    }

    @Override
    public JSONObject saveJson() throws Exception {
        var json = super.saveJson();

        var soundList = new JSONArray();

        for(var sound : mutedSounds) {
            var soundJson = new JSONObject();

            soundJson.put("id", sound.getSound());
            soundJson.put("volume", sound.getVolume());

            soundList.put(soundJson);
        }

        json.put("sounds", soundList);

        return json;
    }

    @Override
    public void moduleConfigPressed(ButtonWidget button) {
        scrollPos = 0.0;
        super.moduleConfigPressed(button);
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var client = MinecraftClient.getInstance();
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("toggle_sounds").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 + 95));

        if(soundField == null) {
            soundField = new TextFieldWidget(client.textRenderer, 0, 0, soundsWidgetWidth - 50, 15, Text.of(""));
            soundField.setMaxLength(128);
            soundField.setChangedListener((text) -> soundTextChanged(soundField, text));
        }

        soundField.setPosition(base.width/2 - 140, base.height/2 - 40);
        
        widgets.add(soundField);

        var background = new CustomDrawWidget.CustomDrawBuilder()
            .position(base.width/2 - 138, base.height/2 - 52)
            .onDraw((context, mouseX, mouseY, ticks) -> {
                context.fill(-2, -7, 248, 6, 0x66000000);
            })
            .build();
        
        widgets.add(background);

        if(suggestionField == null) {
            suggestionField = new TextWidget(Text.literal(""), client.textRenderer);
            suggestionField.alignLeft();
            suggestionField.setWidth(240);
        }

        suggestionField.setPosition(base.width/2 - 138, base.height/2 - 56);
        
        widgets.add(suggestionField);

        widgets.add(ButtonWidget.builder(Text.translatable("fireclient.module.sounds.add_sound.name"), (button) -> addSoundButtonPressed(soundField))
            .dimensions(base.width/2 + 115, base.height/2 - 40, 20, 15)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.sounds.add_sound.tooltip")))
            .build());

        widgets.add(new ToggleButtonBuilder(null)
            .getValue(() -> { return useHumanNames; })
            .setValue((value) -> {
                useHumanNames = value;

                soundField.setText("");
                suggestionField.setMessage(Text.literal(""));

                reloadScreen();
            })
            .trueText(Text.translatable("fireclient.module.sounds.technical_names.name").setStyle(Style.EMPTY.withColor(0x57D647)))
            .falseText(Text.translatable("fireclient.module.sounds.technical_names.name").setStyle(Style.EMPTY.withColor(0xD63C3C)))
            .dimensions(base.width/2 + 115, base.height/2 - 60, 20, 15)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.sounds.technical_names.tooltip")))
            .build());

        var entries = new ArrayList<ScrollableWidget.ElementEntry>();
        for(var sound : mutedSounds) {
            var entryWidgets = new ArrayList<ClickableWidget>();

            var text = new TextWidget(Text.literal(toHuman(sound.getSound())), base.getTextRenderer());
            text.setPosition(base.width/2 - 140, 4);

            entryWidgets.add(text);

            var slider = new SliderWidget(base.width / 2 + 60, 0, 50, 15, getVolumeText(sound.volume), (sound.volume)/2) {
                @Override
                protected void updateMessage() {
                    setMessage(getVolumeText(sound.volume));
                }

                @Override
                protected void applyValue() {
                    sound.volume = value * 2;
                }
            };

            entryWidgets.add(slider);

            entryWidgets.add(ButtonWidget.builder(Text.translatable("fireclient.module.sounds.remove_sound.name").withColor(0xD63C3C), (button) -> removeSound(sound))
                .dimensions(base.width/2 + 115, 0,20,15)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.sounds.remove_sound.tooltip", sound.getSound())))
                .build());

            entries.add(new ScrollableWidget.ElementEntry(entryWidgets));
        }

        scroll = new ScrollableWidget(base, soundsWidgetWidth, soundsWidgetHeight, 0, 20, entries);
        scroll.setScrollY(scrollPos);
        scroll.setPosition(base.width/2 - (soundsWidgetWidth/2), base.height/2 - 10);

        widgets.add(scroll);
        return widgets;
    }

    @Override
    public void openScreen(Screen base) {
        base.setFocused(soundField);
    }

    private Text getVolumeText(double volume) {
        return Text.translatable("fireclient.module.sounds.volume.message", (int)Math.round(volume*100) + "%");
    }

    private void soundTextChanged(TextFieldWidget widget, String text) {
        if(!text.isEmpty()) {
            var check = text.substring(text.length()-1);
            if(",|".contains(check)) {
                widget.setText(text.substring(0, text.length()-1));
                addSoundButtonPressed(widget);
                return;
            }
        }

        suggestionField.setMessage(Text.literal(getSuggestion(text)));
    }

    private String getSuggestion(String text) {
        if(text.isEmpty()) {
            return "";
        }

        var id = toId(RooHelper.filterIdInput(text));

        if(mutedSounds.stream().noneMatch((mutedSound) -> { return mutedSound.getSound().equalsIgnoreCase(id); })) {
            var soundId = Identifier.ofVanilla(id);
            if(Registries.SOUND_EVENT.containsId(soundId)) {
                return "";
            }
        }

        var filteredSounds = getSoundList().filter((soundId) ->
            soundId.toLowerCase().contains(text.toLowerCase()) && mutedSounds.stream()
                .noneMatch((mutedSound -> mutedSound.getSound().equalsIgnoreCase(toId(soundId))))
        )
        .sorted()
        .toList();

        if(filteredSounds.isEmpty()) {
            return "";
        }

        var sound = filteredSounds.getFirst();
        return sound;
    }

    private void addSoundButtonPressed(TextFieldWidget text) {
        var suggestion = suggestionField.getMessage().getString();
        var sound = RooHelper.filterIdInput(toId(suggestion));

        if(sound.isEmpty()) {
            return;
        }

        if(mutedSounds.stream().anyMatch((mutedSound -> mutedSound.getSound().equalsIgnoreCase(sound)))) {
            RooHelper.sendNotification(
                Text.translatable("fireclient.module.sounds.add_sound.failure.title"),
                Text.translatable("fireclient.module.sounds.add_sound.already_exists.contents")
            );

            return;
        }

        mutedSounds.add(new MutedSound(sound, 1.0));
        text.setSuggestion("");
        
        reloadScreen();
    }

    private Stream<String> getSoundList() {
        if(useHumanNames) {
            return idToHuman.values().stream();
        }
        else {
            return Registries.SOUND_EVENT.getIds().stream().map(sound -> sound.getPath());
        }
    }

    private String toId(String value) {
        return humanToId.getOrDefault(value.toLowerCase(), value);
    }
    
    private String toHuman(String value) {
        if(!useHumanNames) {
            return value;
        }

        return idToHuman.getOrDefault(value.toLowerCase(), value);
    }
    
    private void removeSound(MutedSound sound) {
        mutedSounds.remove(sound);
        reloadScreen();
    }

    @Override
    public void drawScreen(Screen base, DrawContext context, float delta) {
        if(scroll != null) {
            scrollPos = scroll.getScrollY();
        }

        drawScreenHeader(context, base.width/2, base.height/2 - 70);
    }

    @Override
    public void closeScreen(Screen screen) {
        suggestionField = null;
        soundField = null;

        FireClientside.saveConfig();
    }

    public double getVolume(SoundEvent sound) {
        for(var muted : mutedSounds) {
            if(muted.getSoundEvent() != null && muted.getSoundEvent().id().equals(sound.id())) {
                return muted.volume;
            }
        }

        return 1.0;
    }

    static class MutedSound {

        private final String sound;

        @Nullable
        private final SoundEvent soundEvent;
        private double volume;

        public MutedSound(String sound, double volume) {
            this.sound = sound;
            this.volume = volume;

            this.soundEvent = SoundEvent.of(Identifier.ofVanilla(this.sound));
        }

        public String getSound() {
            return sound;
        }

        @Nullable
        public SoundEvent getSoundEvent() {
            return soundEvent;
        }

        public double getVolume() {
            return this.volume;
        }

        public void setVolume(double volume) {
            this.volume = volume;
        }
    }
}
