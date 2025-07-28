package org.loveroo.fireclient.modules;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ToggleToggleSneakModule extends ModuleBase {

    private final Color offColor1 = new Color(247, 33, 33, 255);
    private final Color offColor2 = new Color(176, 18, 18, 255);
    private final Color onColor1 = new Color(47, 216, 39, 255);
    private final Color onColor2 = new Color(28, 158, 21, 255);

    private final MutableText onText = RooHelper.gradientText("Sneak Toggled: On", onColor1, onColor2);
    private final MutableText offText = RooHelper.gradientText("Sneak Toggled: Off", offColor1, offColor2);

    private final KeyBinding toggleButton = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.fireclient.toggle_toggle_sneak", GLFW.GLFW_KEY_L, FireClient.KEYBIND_CATEGORY));

    public ToggleToggleSneakModule() {
        super(new ModuleData("Toggle ToggleSneak", "toggle_toggle_sneak"));
        getData().setSelectable(false);
    }

    @Override
    public void update(MinecraftClient client) {
        if(!getData().isEnabled()) {
            return;
        }

        if(toggleButton.wasPressed()) {
            var toggled = !client.options.getSneakToggled().getValue();
            client.options.getSneakToggled().setValue(toggled);

            client.player.sendMessage((toggled ? onText : offText), true);
        }
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(ButtonWidget.builder(Text.of("Enabled: " + getData().isEnabled()), this::enableButtonPressed)
                .dimensions(base.width/2 - 60, base.height/2 - 10, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.generic.enabled_toggle")))
                .build());

        return widgets;
    }

    public void enableButtonPressed(ButtonWidget button) {
        getData().setEnabled(!getData().isEnabled());
        button.setMessage(Text.of("Enabled: " + getData().isEnabled()));
    }
}
