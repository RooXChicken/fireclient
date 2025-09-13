package org.loveroo.fireclient.modules;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;
import org.loveroo.fireclient.mixin.modules.mutesounds.GetSuggestionAccessor;
import org.loveroo.fireclient.screen.base.ScrollableWidget;
import org.loveroo.fireclient.screen.widgets.ToggleButtonWidget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

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
        super(new ModuleData("particles", "\uD83C\uDF1F", color));

        getData().setEnabled(true);
        getData().setGuiElement(false);

        var toggleBind = new Keybind("toggle_particles",
                Text.translatable("fireclient.keybind.generic.toggle.name"),
                Text.translatable("fireclient.keybind.generic.toggle.description", getData().getShownName()),
                true, null,
                () -> getData().setEnabled(!getData().isEnabled()), null);

        FireClientside.getKeybindManager().registerKeybind(toggleBind);
    }

    @Override
    public void loadJson(JSONObject json) throws Exception {
        super.loadJson(json);

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
    }

    @Override
    public JSONObject saveJson() throws Exception {
        var json = super.saveJson();

        var soundList = new JSONArray();

        for(var sound : hiddenParticles) {
            var soundJson = new JSONObject();

            soundJson.put("id", sound.getParticle());
            soundJson.put("enabled", sound.isEnabled());

            soundList.put(soundJson);
        }

        json.put("particles", soundList);

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

        widgets.add(ButtonWidget.builder(Text.translatable("fireclient.module.sounds.add_sound.name"), (button) -> addParticleButton(particleField))
            .dimensions(base.width/2 + 115, base.height/2 - 40, 20, 15)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.sounds.add_sound.tooltip")))
            .build());

        var entries = new ArrayList<ScrollableWidget.ElementEntry>();

        for(var sound : hiddenParticles) {
            var entryWidgets = new ArrayList<ClickableWidget>();

            var text = new TextWidget(Text.literal(sound.getParticle()), base.getTextRenderer());
            text.setPosition(base.width/2 - 140, 4);

            entryWidgets.add(text);

            entryWidgets.add(new ToggleButtonWidget.ToggleButtonBuilder(null)
                .getValue(sound::isEnabled)
                .setValue(sound::setEnabled)
                .dimensions(base.width/2 + 90, 0,20,15)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.sounds.toggle_sound.tooltip", sound.getParticle())))
                .build());

            entryWidgets.add(ButtonWidget.builder(Text.translatable("fireclient.module.sounds.remove_sound.name").withColor(0xD63C3C), (button) -> removeParticle(sound))
                .dimensions(base.width/2 + 115, 0,20,15)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.sounds.remove_sound.tooltip", sound.getParticle())))
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

        var input = RooHelper.filterIdInput(text);

        if(hiddenParticles.stream().noneMatch((hiddenParticle) -> { return hiddenParticle.getParticle().equalsIgnoreCase(input); })) {
            var particleId = Identifier.ofVanilla(input);
            if(Registries.PARTICLE_TYPE.containsId(particleId)) {
                return "";
            }
        }

        var filteredParticles = Registries.PARTICLE_TYPE.getIds().stream()
        .filter((id) -> {
            var startsWith = id.getPath().startsWith(input);
            var exists = hiddenParticles.stream()
                .anyMatch((hiddenParticle -> hiddenParticle.getParticle().equalsIgnoreCase(id.getPath())));

            return (startsWith && !exists);
        })
        .toList();

        if(filteredParticles.isEmpty()) {
            return "";
        }

        var sound = filteredParticles.getFirst().getPath();
        return sound.substring(input.length());
    }

    private void addParticleButton(TextFieldWidget text) {
        var suggestion = ((GetSuggestionAccessor)text).getSuggestion();
        if(suggestion == null) {
            suggestion = "";
        }

        var particle = RooHelper.filterIdInput(text.getText()) + suggestion;
        if(particle.isEmpty()) {
            return;
        }

        if(hiddenParticles.stream().anyMatch((hiddenParticle -> hiddenParticle.getParticle().equalsIgnoreCase(particle)))) {
            RooHelper.sendNotification(
                Text.translatable("fireclient.module.sounds.add_sound.failure.title"),
                Text.translatable("fireclient.module.sounds.add_sound.already_exists.contents")
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
            .anyMatch((hidden) -> (hidden.isEnabled() && hidden.getParticleType() != null && hidden.getParticleType().equals(particle)));
    }

    static class HiddenParticle {

        private final String particle;

        @Nullable
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

        @Nullable
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
