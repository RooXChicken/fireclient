package org.loveroo.fireclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.JsonOption;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;

import java.util.ArrayList;
import java.util.List;

public class RenderWorldModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0x589157);

    @JsonOption(name = "toggled")
    private boolean toggled = false;

    public RenderWorldModule() {
        super(new ModuleData("render_world", "\uD83C\uDF0D", color));

        getData().setGuiElement(false);

        var useBind = new Keybind("use_render_world",
                Text.translatable("fireclient.keybind.generic.use.name"),
                Text.translatable("fireclient.keybind.generic.use.description", getData().getShownName()),
                true, null,
                this::useKey, null);

        FireClientside.getKeybindManager().registerKeybind(useBind);
    }

    private void useKey() {
        toggled = !toggled;
    }

    @Override
    public void update(MinecraftClient client) {
        if(!getData().isEnabled()) {
            toggled = false;
            return;
        }
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("use_render_world").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 - 10));

        return widgets;
    }

    public boolean isToggled() {
        return toggled;
    }
}
