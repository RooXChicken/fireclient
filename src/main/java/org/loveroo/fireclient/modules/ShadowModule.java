package org.loveroo.fireclient.modules;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.JsonOption;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.screen.widgets.ToggleButtonBuilder;

import java.util.ArrayList;
import java.util.List;

public class ShadowModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0x1A042E);

    @JsonOption(name = "distance_effect")
    private boolean distanceEffect = true;
    
    @JsonOption(name = "increase_height")
    private boolean increaseHeight = false;

    @JsonOption(name = "fullbright")
    private boolean fullbright = false;

    @JsonOption(name = "render_on_all")
    private boolean renderOnAll = false;

    public static boolean drawingShadow = false;

    public ShadowModule() {
        super(new ModuleData("shadow", "\uD83D\uDD73", color));

        getData().setGuiElement(false);
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(new ToggleButtonBuilder(Text.translatable("fireclient.module.shadow.distance_effect.name"))
            .getValue(() -> { return distanceEffect; })
            .setValue((value) -> { distanceEffect = value; })
            .position(base.width/2 - 130, base.height/2 - 10)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.shadow.distance_effect.tooltip")))
            .build());

        widgets.add(new ToggleButtonBuilder(Text.translatable("fireclient.module.shadow.increase_height.name"))
            .getValue(() -> { return increaseHeight; })
            .setValue((value) -> { increaseHeight = value; })
            .position(base.width/2 + 10, base.height/2 - 10)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.shadow.increase_height.tooltip")))
            .build());

        widgets.add(new ToggleButtonBuilder(Text.translatable("fireclient.module.shadow.fullbright.name"))
            .getValue(() -> { return fullbright; })
            .setValue((value) -> { fullbright = value; })
            .position(base.width/2 - 130, base.height/2 + 20)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.shadow.fullbright.tooltip")))
            .build());

        widgets.add(new ToggleButtonBuilder(Text.translatable("fireclient.module.shadow.render_on_all.name"))
            .getValue(() -> { return renderOnAll; })
            .setValue((value) -> { renderOnAll = value; })
            .position(base.width/2 + 10, base.height/2 + 20)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.shadow.render_on_all.tooltip")))
            .build());

        return widgets;
    }

    public boolean isDistanceEffect() {
        return distanceEffect;
    }

    public boolean isIncreaseHeight() {
        return increaseHeight;
    }

    public boolean isFullbright() {
        return fullbright;
    }

    public boolean isRenderOnAll() {
        return renderOnAll;
    }
}
