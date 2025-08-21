package org.loveroo.fireclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Key;
import org.loveroo.fireclient.keybind.Keybind;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ToggleToggleSneakModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0x7D6476);

    private static final Color offColor1 = new Color(247, 33, 33, 255);
    private static final Color offColor2 = new Color(176, 18, 18, 255);
    private static final Color onColor1 = new Color(47, 216, 39, 255);
    private static final Color onColor2 = new Color(28, 158, 21, 255);

    private final MutableText onText = RooHelper.gradientText("Sneak Toggled: On", onColor1, onColor2);
    private final MutableText offText = RooHelper.gradientText("Sneak Toggled: Off", offColor1, offColor2);

    public ToggleToggleSneakModule() {
        super(new ModuleData("toggle_toggle_sneak", "\uD83D\uDC5F", color));

        getData().setGuiElement(false);

        var useBind = new Keybind("use_toggle_toggle_sneak",
                Text.translatable("fireclient.keybind.generic.use.name"),
                Text.translatable("fireclient.keybind.generic.use.description", getData().getShownName()),
                true, null,
                this::useKey, null);

        FireClientside.getKeybindManager().registerKeybind(useBind);
    }

    private void useKey() {
        if(!getData().isEnabled()) {
            return;
        }

        var client = MinecraftClient.getInstance();

        var toggled = !client.options.getSneakToggled().getValue();
        client.options.getSneakToggled().setValue(toggled);

        client.player.sendMessage((toggled ? onText : offText), true);
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("use_toggle_toggle_sneak").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 - 10));

        return widgets;
    }
}
