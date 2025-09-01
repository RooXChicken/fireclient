package org.loveroo.fireclient.modules;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;
import org.loveroo.fireclient.mixin.modules.mutesounds.GetSuggestionAccessor;
import org.loveroo.fireclient.screen.base.ScrollableWidget;
import org.loveroo.fireclient.screen.widgets.ToggleButtonWidget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class MuteSoundsModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0xFA8A73);

    private final List<MutedSound> mutedSounds = new ArrayList<>();

    private static double scrollPos = 0.0;
    private final int soundsWidgetWidth = 300;
    private final int soundsWidgetHeight = 100;

    @Nullable
    private ScrollableWidget scroll;

    @Nullable
    private TextFieldWidget soundField;

    public MuteSoundsModule() {
        super(new ModuleData("mute_sounds", "\uD83D\uDD07", color));

        getData().setGuiElement(false);

        var toggleBind = new Keybind("toggle_mute_sounds",
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

            mutedSounds.add(new MutedSound(sound.optString("id", ""), sound.optBoolean("enabled", true)));
        }
    }

    @Override
    public JSONObject saveJson() throws Exception {
        var json = super.saveJson();

        var soundList = new JSONArray();

        for(var sound : mutedSounds) {
            var soundJson = new JSONObject();

            soundJson.put("id", sound.getSound());
            soundJson.put("enabled", sound.isEnabled());

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

        widgets.add(FireClientside.getKeybindManager().getKeybind("toggle_mute_sounds").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 + 95));

        soundField = new TextFieldWidget(client.textRenderer, base.width/2 - 140, base.height/2 - 40, soundsWidgetWidth - 50, 15, Text.of(""));
        soundField.setMaxLength(128);
        soundField.setChangedListener((text) -> soundTextChanged(soundField, text));

        widgets.add(soundField);

        widgets.add(ButtonWidget.builder(Text.translatable("fireclient.module.mute_sounds.add_sound.name"), (button) -> addSoundButtonPressed(soundField))
            .dimensions(base.width/2 + 115, base.height/2 - 40, 20, 15)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.mute_sounds.add_sound.tooltip")))
            .build());

        var entries = new ArrayList<ScrollableWidget.ElementEntry>();
        for(var sound : mutedSounds) {
            var entryWidgets = new ArrayList<ClickableWidget>();

            var text = new TextWidget(Text.literal(sound.getSound()), base.getTextRenderer());
            text.setPosition(base.width/2 - 140, 4);

            entryWidgets.add(text);

            entryWidgets.add(new ToggleButtonWidget.ToggleButtonBuilder(null)
                .getValue(sound::isEnabled)
                .setValue(sound::setEnabled)
                .dimensions(base.width/2 + 90, 0, 20, 15)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.mute_sounds.toggle_sound.tooltip", sound.getSound())))
                .build());

            entryWidgets.add(ButtonWidget.builder(Text.translatable("fireclient.module.mute_sounds.remove_sound.name").withColor(0xD63C3C), (button) -> removeSound(sound))
                .dimensions(base.width/2 + 115, 0,20,15)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.mute_sounds.remove_sound.tooltip", sound.getSound())))
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

    private void soundTextChanged(TextFieldWidget widget, String text) {
        if(!text.isEmpty()) {
            var check = text.substring(text.length()-1);
            if(" ,|".contains(check)) {
                widget.setText(text.substring(0, text.length()-1));
                addSoundButtonPressed(widget);
                return;
            }
        }

        widget.setSuggestion(getSuggestion(text));
    }

    private String getSuggestion(String text) {
        if(text.isEmpty()) {
            return "";
        }

        var input = RooHelper.filterIdInput(text);

        if(mutedSounds.stream().noneMatch((mutedSound) -> { return mutedSound.getSound().equalsIgnoreCase(input); })) {
            var soundId = Identifier.ofVanilla(input);
            if(Registries.SOUND_EVENT.containsId(soundId)) {
                return "";
            }
        }

        var filteredSounds = Registries.SOUND_EVENT.getIds().stream().filter((id) ->
            id.getPath().startsWith(input) && mutedSounds.stream().noneMatch((mutedSound -> mutedSound.getSound().equalsIgnoreCase(id.getPath())))
        ).toList();

        if(filteredSounds.isEmpty()) {
            return "";
        }

        var sound = filteredSounds.getFirst().getPath();
        return sound.substring(input.length());
    }

    private void addSoundButtonPressed(TextFieldWidget text) {
        var suggestion = ((GetSuggestionAccessor)text).getSuggestion();
        if(suggestion == null) {
            suggestion = "";
        }

        var sound = RooHelper.filterIdInput(text.getText()) + suggestion;

        if(mutedSounds.stream().anyMatch((mutedSound -> mutedSound.getSound().equalsIgnoreCase(sound)))) {
            RooHelper.sendNotification(
                Text.translatable("fireclient.module.mute_sounds.add_sound.failure.title"),
                Text.translatable("fireclient.module.mute_sounds.add_sound.already_exists.contents")
            );

            return;
        }

        mutedSounds.add(new MutedSound(sound, true));
        text.setSuggestion("");
        
        reloadScreen();
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
        FireClientside.saveConfig();
    }

    public boolean isMute(SoundEvent sound) {
        return mutedSounds.stream().anyMatch((muted) -> (muted.isEnabled() && muted.getSoundEvent().id().equals(sound.id())));
    }

    static class MutedSound {

        private final String sound;
        private final SoundEvent soundEvent;
        private boolean enabled;

        public MutedSound(String sound, boolean enabled) {
            this.sound = sound;
            this.enabled = enabled;

            this.soundEvent = SoundEvent.of(Identifier.ofVanilla(this.sound));
        }

        public String getSound() {
            return sound;
        }

        public SoundEvent getSoundEvent() {
            return soundEvent;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
