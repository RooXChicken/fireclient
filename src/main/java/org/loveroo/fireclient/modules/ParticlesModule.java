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
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.item.Item;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ParticlesModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0xFA8A73);

    private final List<HiddenParticle> hiddenParticles = new ArrayList<>();

    private static double scrollPos = 0.0;
    private final int soundsWidgetWidth = 300;
    private final int soundsWidgetHeight = 100;

    @Nullable
    private ScrollableWidget scroll;

    @Nullable
    private TextFieldWidget particleField;

    public ParticlesModule() {
        super(new ModuleData("particles", "\uD83D\uDD07", color));

        getData().setGuiElement(false);

        var toggleBind = new Keybind("toggle_particles",
                Text.translatable("fireclient.keybind.generic.toggle.name"),
                Text.translatable("fireclient.keybind.generic.toggle.description", getData().getShownName()),
                true, null,
                () -> getData().setEnabled(!getData().isEnabled()), null);

        FireClientside.getKeybindManager().registerKeybind(toggleBind);
    }

    @Override
    public void loadJson(JSONObject json) throws JSONException {
        var soundList = json.optJSONArray("particles");
        if(soundList == null) {
            soundList = new JSONArray();
        }

        for(var i = 0; i < soundList.length(); i++) {
            var sound = soundList.optJSONObject(i);
            if(sound == null) {
                continue;
            }

            hiddenParticles.add(new HiddenParticle(sound.optString("id", ""), sound.optBoolean("enabled", true)));
        }

        getData().setEnabled(json.optBoolean("enabled", getData().isEnabled()));
    }

    @Override
    public JSONObject saveJson() throws JSONException {
        var json = new JSONObject();

        var soundList = new JSONArray();

        for(var sound : hiddenParticles) {
            var soundJson = new JSONObject();

            soundJson.put("id", sound.getParticle());
            soundJson.put("enabled", sound.isEnabled());

            soundList.put(soundJson);
        }

        json.put("particles", soundList);
        json.put("enabled", getData().isEnabled());

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

        widgets.add(FireClientside.getKeybindManager().getKeybind("toggle_particles").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 + 95));

        particleField = new TextFieldWidget(client.textRenderer, base.width/2 - 140, base.height/2 - 40, soundsWidgetWidth - 50, 15, Text.of(""));
        particleField.setMaxLength(128);
        particleField.setChangedListener((text) -> particleTextChanged(particleField, text));

        widgets.add(particleField);

        widgets.add(ButtonWidget.builder(Text.translatable("fireclient.module.mute_sounds.add_sound.name"), (button) -> addParticleButton(particleField))
                .dimensions(base.width/2 + 115, base.height/2 - 40, 20, 15)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.mute_sounds.add_sound.tooltip")))
                .build());

        var entries = new ArrayList<ScrollableWidget.ElementEntry>();

        for(var sound : hiddenParticles) {
            var entryWidgets = new ArrayList<ClickableWidget>();

            var text = new TextWidget(Text.literal(sound.getParticle()), base.getTextRenderer());
            text.setPosition(base.width/2 - 140, 4);

            entryWidgets.add(text);

            entryWidgets.add(ButtonWidget.builder(getToggleText(null, sound.isEnabled()), (button) -> toggleParticleButton(button, sound))
                    .dimensions(base.width/2 + 90, 0,20,15)
                    .tooltip(Tooltip.of(Text.translatable("fireclient.module.mute_sounds.toggle_sound.tooltip", sound.getParticle())))
                    .build());

            entryWidgets.add(ButtonWidget.builder(Text.translatable("fireclient.module.mute_sounds.remove_sound.name").withColor(0xD63C3C), (button) -> removeParticle(sound))
                    .dimensions(base.width/2 + 115, 0,20,15)
                    .tooltip(Tooltip.of(Text.translatable("fireclient.module.mute_sounds.remove_sound.tooltip", sound.getParticle())))
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
        base.setFocused(particleField);
    }

    private void particleTextChanged(TextFieldWidget widget, String text) {
        if(!text.isEmpty()) {
            var check = text.substring(text.length()-1);
            if(" ,|".contains(check)) {
                widget.setText(text.substring(0, text.length()-1));
                addParticleButton(widget);
                return;
            }
        }

        widget.setSuggestion(getSuggestion(text));
    }

    private String getSuggestion(String text) {
        if(text.isEmpty()) {
            return "";
        }

        var filteredSounds = Registries.PARTICLE_TYPE.getIds().stream()
        .filter((id) -> {
            var startsWith = id.getPath().startsWith(text);
            var exists = hiddenParticles.stream()
                .anyMatch((hiddenParticle -> hiddenParticle.getParticle().equalsIgnoreCase(id.getPath())));

            return (startsWith && !exists);
        })
        .toList();

        if(filteredSounds.isEmpty()) {
            return "";
        }

        var sound = filteredSounds.getFirst().getPath();
        return sound.substring(text.length());
    }

    private void addParticleButton(TextFieldWidget text) {
        var suggestion = ((GetSuggestionAccessor)text).getSuggestion();
        if(suggestion == null) {
            suggestion = "";
        }

        var particle = text.getText() + suggestion;

        if(!particle.matches("[a-z0-9/._-]+")) {
            RooHelper.sendNotification(
                    Text.translatable("fireclient.module.mute_sounds.add_sound.failure.title"),
                    Text.translatable("fireclient.module.mute_sounds.add_sound.invalid_id.contents")
            );

            return;
        }

        if(hiddenParticles.stream().anyMatch((hiddenParticle -> hiddenParticle.getParticle().equalsIgnoreCase(particle)))) {
            RooHelper.sendNotification(
                    Text.translatable("fireclient.module.mute_sounds.add_sound.failure.title"),
                    Text.translatable("fireclient.module.mute_sounds.add_sound.already_exists.contents")
            );

            return;
        }

        hiddenParticles.add(new HiddenParticle(particle, true));
        text.setSuggestion("");
        
        reloadScreen();
    }

    private void removeParticle(HiddenParticle sound) {
        hiddenParticles.remove(sound);
        reloadScreen();
    }

    private void toggleParticleButton(ButtonWidget button, HiddenParticle sound) {
        sound.setEnabled(!sound.isEnabled());
        button.setMessage(getToggleText(null, sound.isEnabled()));
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

    public boolean isHidden(ParticleType<?> particle) {
        return hiddenParticles.stream()
            .anyMatch((hidden) -> (hidden.isEnabled() && hidden.getParticleType().equals(particle)));
    }

    static class HiddenParticle {

        private final String particle;
        private final ParticleType<?> particleType;
        private boolean enabled;

        public HiddenParticle(String particle, boolean enabled) {
            this.particle = particle;
            this.enabled = enabled;

            this.particleType = Registries.PARTICLE_TYPE.get(Identifier.ofVanilla(this.particle));
        }

        public String getParticle() {
            return particle;
        }

        public ParticleType<?> getParticleType() {
            return particleType;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
