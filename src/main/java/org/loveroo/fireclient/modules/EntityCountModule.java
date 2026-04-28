package org.loveroo.fireclient.modules;

import java.util.ArrayList;
import java.util.List;

import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;
import org.loveroo.fireclient.mixin.WorldAccessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.DeltaTracker;
import net.minecraft.network.chat.Component;

public class EntityCountModule extends ModuleBase {

    private static final Color color1 = Color.fromRGB(0xD4D4D4);
    private static final Color color2 = Color.fromRGB(0xDECEB4);

    public EntityCountModule() {
        super(new ModuleData("entity_count", "\uD83D\uDC64", color1));

        getData().setHeight(8);
        getData().setWidth(30);

        getData().setDefaultPosX(2, 640);
        getData().setDefaultPosY(46, 360);

        getData().setVisible(false);

        var toggleBind = new Keybind("toggle_entity_count",
                Component.translatable("fireclient.keybind.generic.toggle.name"),
                Component.translatable("fireclient.keybind.generic.toggle_visibility.description", getData().getShownName()),
                true, null,
                () -> getData().setVisible(!getData().isVisible()), null);

        FireClientside.getKeybindManager().registerKeybind(toggleBind);
    }

    @Override
    public List<AbstractWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<AbstractWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("toggle_entity_count").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleVisibleButton(base.width/2 - 60, base.height/2 - 10));

        return widgets;
    }

    @Override
    public void draw(GuiGraphicsExtractor graphics, DeltaTracker ticks) {
        if(!canDraw()) {
            return;
        }

        var client = Minecraft.getInstance();
        if(client.levelRenderer == null) {
            return;
        }
        
        var text = client.font;

        var accessor = (WorldAccessor)client.levelRenderer;
        if(accessor.getLevel() == null) {
            return;
        }
        
        var drawnCount = accessor.getLevelRenderState().entityRenderStates.size();
        var total = accessor.getLevel().getEntityCount();
        var msg = "E: " + drawnCount + "/" + total;
        
        var entityText = RooHelper.gradientText(msg, color1, color2);
        
        transform(graphics.pose());

        getData().setWidth(text.width(entityText));

        graphics.text(text, entityText, 0, 0, 0xFFFFFFFF, true);

        endTransform(graphics.pose());
    }
}
