package org.loveroo.fireclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;

import java.util.ArrayList;
import java.util.List;

public class SubtitlesModule extends ModuleBase {

    public SubtitlesModule() {
        super(new ModuleData("subtitles", "\uD83D\uDCC4 Subtitles", "Modifies the subtitles hud"));
        getData().setShownName(generateDisplayName(0xDEDEDE));

        getData().setGuiElement(false);

        FireClientside.getKeybindManager().registerKeybind(
                new Keybind("toggle_subtitles", Text.of("Toggle"), Text.of("Toggle ").copy().append(getData().getShownName()).append("'s visibility"), true, null,
                        this::subtitlesToggled, null)
        );
    }

    @Override
    public void loadJson(JSONObject json) throws JSONException {

    }

    @Override
    public JSONObject saveJson() throws JSONException {
        return new JSONObject();
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("toggle_subtitles").getRebindButton(5, base.height - 25, 120,20));

        widgets.add(ButtonWidget.builder(getToggleText(Text.of("Visible"), isEnabled()), this::subtitlesButtonPressed)
                .dimensions(base.width/2 - 60, base.height/2 - 10, 120, 20)
                .build());

        return widgets;
    }

    private void subtitlesToggled() {
        var client = MinecraftClient.getInstance();
        client.options.getShowSubtitles().setValue(!isEnabled());
    }

    private boolean isEnabled() {
        return MinecraftClient.getInstance().options.getShowSubtitles().getValue();
    }

    private void subtitlesButtonPressed(ButtonWidget button) {
        subtitlesToggled();
        button.setMessage(getToggleText(Text.of("Visible"), isEnabled()));
    }
}
