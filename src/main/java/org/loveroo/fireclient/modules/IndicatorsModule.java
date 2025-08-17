package org.loveroo.fireclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.modules.indicators.*;
import org.loveroo.fireclient.screen.config.ModuleConfigScreen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class IndicatorsModule extends ModuleBase {

    private final ArrayList<Indicator> indicators = new ArrayList<>();

    public IndicatorsModule() {
        super(new ModuleData("indicators", "★ Indicators", "Shows various effect indicators"));
        getData().setShownName(generateDisplayName(0xD7D9B2));

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

        client.setScreen(new ModuleConfigScreen("Indicators", "Configure various on screen indicators", indicatorModules));
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        for(var i = 0; i < indicators.size(); i++) {
            var indicator = indicators.get(i);

            var x = base.width/2 - 90;
            var y = base.height/2 + (i*30) - 10;

            if(indicator.hasOverlay()) {
                widgets.add(ButtonWidget.builder(getToggleText(Text.of("Overlay"), indicator.doesShowOverlay()), (button) -> overlayToggled(button, indicator))
                        .dimensions(x + 100, y, 80, 20)
                        .tooltip(Tooltip.of(Text.of("Shows the on screen overlay for ").copy().append(indicator.getData().getShownName())))
                        .build()
                );
            }

            var text = new TextWidget(indicator.getData().getShownName(), base.getTextRenderer());
            text.setX(x - 70);
            text.setY(y + 8);

            widgets.add(text);

            widgets.add(ButtonWidget.builder(getToggleText(Text.of("Indicator"), indicator.getData().isVisible()), (button) -> indicatorToggled(button, indicator))
                    .dimensions(x, y, 80, 20)
                    .tooltip(Tooltip.of(Text.of("Shows the indicator for ").copy().append(indicator.getData().getShownName())))
                    .build()
            );
        }

        return widgets;
    }

    private void indicatorToggled(ButtonWidget button, Indicator indicator) {
        indicator.getData().setVisible(!indicator.getData().isVisible());
        button.setMessage(getToggleText(Text.of("Indicator"), indicator.getData().isVisible()));
    }

    private void overlayToggled(ButtonWidget button, Indicator indicator) {
        indicator.setShowOverlay(!indicator.doesShowOverlay());
        button.setMessage(getToggleText(Text.of("Overlay"), indicator.doesShowOverlay()));
    }
}
