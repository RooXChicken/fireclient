package org.loveroo.fireclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.modules.indicators.*;
import org.loveroo.fireclient.screen.base.ScrollableWidget;
import org.loveroo.fireclient.screen.config.ModuleConfigScreen;
import org.loveroo.fireclient.screen.widgets.ToggleButtonWidget;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class IndicatorsModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0xD7D9B2);

    private final ArrayList<Indicator> indicators = new ArrayList<>();

    private final int indicatorWidgetWidth = 300;
    private final int indicatorWidgetHeight = 100;

    public IndicatorsModule() {
        super(new ModuleData("indicators", "★", color));

        getData().setGuiElement(false);

        var index = 0;
        indicators.add(new FireIndicator(index++));
        indicators.add(new FrostIndicator(index++));
        indicators.add(new NauseaIndicator(index++));
        indicators.add(new PortalIndicator(index++));
        indicators.add(new BlindnessIndicator(index++));
//        indicators.add(new InWallIndicator(index++));
    }

    public void moduleConfigPressed(ButtonWidget button) {
        var client = MinecraftClient.getInstance();

        var indicatorModules = new ArrayList<ModuleBase>(indicators);
        indicatorModules.add(this);

        client.setScreen(new ModuleConfigScreen(Text.translatable("fireclient.module.indicators.name"), Text.translatable("fireclient.module.indicators.description"), indicatorModules));
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        var entries = new ArrayList<ScrollableWidget.ElementEntry>();

        for(var i = 0; i < indicators.size(); i++) {
            var indicatorWidgets = new ArrayList<ClickableWidget>();
            var indicator = indicators.get(i);

            var x = base.width/2 - 60;

            if(indicator.hasOverlay()) {
                indicatorWidgets.add(new ToggleButtonWidget.ToggleButtonBuilder(Text.translatable("fireclient.module.indicators.overlay.name"))
                    .getValue(indicator::doesShowOverlay)
                    .setValue(indicator::setShowOverlay)
                    .dimensions(x + 100, 0, 80, 20)
                    .tooltip(Tooltip.of(Text.translatable("fireclient.module.indicators.overlay.tooltip", indicator.getData().getShownName())))
                    .build()
                );
            }

            var text = new TextWidget(indicator.getData().getShownName(), base.getTextRenderer());
            text.setX(x - 70);
            text.setY(8);

            indicatorWidgets.add(text);

            indicatorWidgets.add(new ToggleButtonWidget.ToggleButtonBuilder(Text.translatable("fireclient.module.indicators.indicator.name"))
                .getValue(indicator.getData()::isVisible)
                .setValue(indicator.getData()::setVisible)
                .dimensions(x, 0, 80, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.indicators.indicator.tooltip", indicator.getData().getShownName())))
                .build()
            );

            var entry = new ScrollableWidget.ElementEntry(indicatorWidgets);
            entries.add(entry);
        }

        var scrollable = new ScrollableWidget(base, indicatorWidgetWidth, indicatorWidgetHeight, 0, 25, entries);
        scrollable.setPosition(base.width/2 - (indicatorWidgetWidth/2), base.height/2 - 10);

        widgets.add(scrollable);
        return widgets;
    }
}
