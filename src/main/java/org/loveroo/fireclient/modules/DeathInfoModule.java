package org.loveroo.fireclient.modules;

import java.util.ArrayList;
import java.util.List;

import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;

public class DeathInfoModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0xFFFFFF);

    public DeathInfoModule() {
        super(new ModuleData("death_info", "☠", color));

        getData().setGuiElement(false);
        getData().setEnabled(true);
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 - 10));

        return widgets;
    }
}
