package org.loveroo.fireclient.modules;

import java.util.ArrayList;
import java.util.List;

import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.JsonOption;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;
import org.loveroo.fireclient.mixin.modules.reachdisplay.FindCrosshairTargetAccessor;
import org.loveroo.fireclient.screen.widgets.ToggleButtonWidget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.DeltaTracker;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.HitResult;

public class ReachDisplayModule extends ModuleBase {

    private static final Color color1 = Color.fromRGB(0xED9380);
    private static final Color color2 = Color.fromRGB(0xC47254);

    private static final Color missColor1 = Color.fromRGB(0x827D7C);
    private static final Color missColor2 = Color.fromRGB(0x4A3F3D);

    private static final Color hitColor1 = Color.fromRGB(0xF0644A);
    private static final Color hitColor2 = Color.fromRGB(0xFA4B2F);

    private double reach = 0.0;

    private Color reachColor1 = missColor1;
    private Color reachColor2 = missColor2;

    @JsonOption(name = "hit_only")
    private boolean hitOnly = false;

    public ReachDisplayModule() {
        super(new ModuleData("reach_display", "🗡", color1));

        getData().setHeight(8);
        getData().setWidth(30);

        getData().setDefaultPosX(2, 640);
        getData().setDefaultPosY(90, 360);

        getData().setVisible(false);

        var toggleBind = new Keybind("toggle_reach_display",
            Component.translatable("fireclient.keybind.generic.toggle.name"),
            Component.translatable("fireclient.keybind.generic.toggle_visibility.description", getData().getShownName()),
            true, null,
            () -> getData().setVisible(!getData().isVisible()), null);

        FireClientside.getKeybindManager().registerKeybind(toggleBind);
    }

    @Override
    public List<AbstractWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<AbstractWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("toggle_reach_display").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleVisibleButton(base.width/2 - 60, base.height/2 - 10));

        widgets.add(new ToggleButtonWidget.ToggleButtonBuilder(Component.translatable("fireclient.module.reach_display.hit_only.name"))
            .getValue(() -> { return hitOnly; })
            .setValue((value) -> { hitOnly = value; })
            .position(base.width/2 - 60, base.height/2 + 20)
            .tooltip(Tooltip.create(Component.translatable("fireclient.module.reach_display.hit_only.tooltip")))
            .build());

        return widgets;
    }

    @Override
    public void draw(GuiGraphicsExtractor graphics, DeltaTracker ticks) {
        if(!canDraw()) {
            return;
        }

        var client = Minecraft.getInstance();
        if(client.player == null || client.getCameraEntity() == null) {
            return;
        }

        transform(graphics.pose());

        var text = client.font;
        
        if(!hitOnly) {
            var crosshairAccessor = (FindCrosshairTargetAccessor) client.player;
            var result = crosshairAccessor.findCrosshairTargetInvoker(client.getCameraEntity(), 100, 100, ticks.getGameTimeDeltaPartialTick(false));

            calculateReach(result);
        }

        var msg = String.format("🗡 %.2f", reach);
        var reachText = RooHelper.gradientText(msg, reachColor1, reachColor2);

        getData().setWidth(text.width(reachText));

        graphics.text(text, reachText, 0, 0, 0xFFFFFFFF, true);

        endTransform(graphics.pose());
    }

    public boolean isHitOnly() {
        return hitOnly;
    }

    public void calculateReach(HitResult result) {
        var client = Minecraft.getInstance();

        if(result != null && result.getType() == HitResult.Type.ENTITY) {
            reach = client.player.getEyePosition().distanceTo(result.getLocation());

            double maxReach = client.player.entityInteractionRange();
            if(reach < maxReach) {
                reachColor1 = hitColor1;
                reachColor2 = hitColor2;
            }
            else {
                reachColor1 = color1;
                reachColor2 = color2;
            }
        }
        else {
            reachColor1 = missColor1;
            reachColor2 = missColor2;
        }
    }
}
