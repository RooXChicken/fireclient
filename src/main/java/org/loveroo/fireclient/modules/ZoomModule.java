package org.loveroo.fireclient.modules;

import java.util.ArrayList;
import java.util.List;

import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.JsonOption;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class ZoomModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0x7D6476);

    @JsonOption(name = "scroll_to_zoom")
    private boolean scrollToZoom = true;

    private boolean zooming = false;
    private int zoomLevel = 0;

    public ZoomModule() {
        super(new ModuleData("zoom", "📷", color));

        getData().setGuiElement(false);

        var useBind = new Keybind("use_zoom",
            Text.translatable("fireclient.keybind.generic.use.name"),
            Text.translatable("fireclient.keybind.generic.use.description", getData().getShownName()),
            true, null,
            this::useKey, () -> { zooming = false; });

        FireClientside.getKeybindManager().registerKeybind(useBind);
    }

    private void useKey() {
        if(!getData().isEnabled()) {
            return;
        }

        zooming = true;
        zoomLevel = 20;
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("use_zoom").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 - 10));

        // TODO: add zoom scroll toggle

        return widgets;
    }

    public boolean isZooming() {
        return zooming;
    }

    public int getZoomLevel() {
        return zoomLevel;
    }

    public boolean doesScrollToZoom() {
        return scrollToZoom;
    }

    public void incrementZoom(int value) {
        if(!doesScrollToZoom() || !isZooming()) {
            return;
        }

        zoomLevel = Math.max(3, value);
    }
}
