package org.loveroo.fireclient.modules;

import java.util.ArrayList;
import java.util.List;

import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.DeltaTracker;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.levelgen.Heightmap;

public class HighestBlockModule extends ModuleBase {

    private static final Color color1 = Color.fromRGB(0xD4D4D4);
    private static final Color color2 = Color.fromRGB(0xC0DEB4);

    public HighestBlockModule() {
        super(new ModuleData("highest_block", "⬆", color1));

        getData().setHeight(8);
        getData().setWidth(30);

        getData().setDefaultPosX(2, 640);
        getData().setDefaultPosY(57, 360);

        getData().setVisible(false);

        var toggleBind = new Keybind("toggle_highest_block",
                Component.translatable("fireclient.keybind.generic.toggle.name"),
                Component.translatable("fireclient.keybind.generic.toggle_visibility.description", getData().getShownName()),
                true, null,
                () -> getData().setVisible(!getData().isVisible()), null);

        FireClientside.getKeybindManager().registerKeybind(toggleBind);
    }

    @Override
    public List<AbstractWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<AbstractWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("toggle_highest_block").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleVisibleButton(base.width/2 - 60, base.height/2 - 10));

        return widgets;
    }

    @Override
    public void draw(GuiGraphicsExtractor graphics, DeltaTracker ticks) {
        if(!canDraw()) {
            return;
        }

        var client = Minecraft.getInstance();
        if(client.player == null || client.player.level() == null) {
            return;
        }

        
        var text = client.font;

        var pos = client.player.blockPosition();
        var chunk = client.player.level().getChunk(pos);
        if(chunk == null) {
            return;
        
        }
        transform(graphics.pose());

        var msg = "Height: " + chunk.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ());
        var heightText = RooHelper.gradientText(msg, color1, color2);

        getData().setWidth(text.width(heightText));

        graphics.text(text, heightText, 0, 0, 0xFFFFFFFF, true);

        endTransform(graphics.pose());
    }
}
