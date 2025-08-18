package org.loveroo.fireclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;
import org.loveroo.fireclient.mixin.modules.WorldRendererAccessor;

import java.util.ArrayList;
import java.util.List;

public class EntityCountModule extends ModuleBase {

    private final Color color1 = Color.fromRGB(0xD4D4D4);
    private final Color color2 = Color.fromRGB(0xDECEB4);

    public EntityCountModule() {
        super(new ModuleData("entity_count", "\uD83D\uDC64 Entity Count", "Shows the world's entity information"));
        getData().setShownName(generateDisplayName(0xD4D4D4));

        getData().setHeight(8);
        getData().setWidth(30);

        getData().setPosX(2, 640);
        getData().setPosY(50, 360);

        getData().setVisible(false);

        FireClientside.getKeybindManager().registerKeybind(
                new Keybind("toggle_entity_count", Text.of("Toggle"), Text.of("Toggle ").copy().append(getData().getShownName()).append("'s visibility"), true, null,
                        () -> getData().setVisible(!getData().isVisible()), null)
        );
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("toggle_entity_count").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleVisibleButton(base.width/2 - 60, base.height/2 - 10));

        return widgets;
    }

    @Override
    public void draw(DrawContext context, RenderTickCounter ticks) {
        if(!canDraw()) {
            return;
        }

        transform(context.getMatrices());

        var client = MinecraftClient.getInstance();
        var text = client.textRenderer;

        var accessor = (WorldRendererAccessor)client.worldRenderer;
        var msg = "E: " + accessor.getRenderedEntitiesCount() + "/" + accessor.getWorld().getRegularEntityCount();

        var entityText = RooHelper.gradientText(msg, color1, color2);

        getData().setWidth(text.getWidth(entityText));

        context.drawText(text, entityText, 0, 0, 0xFFFFFFFF, true);

        endTransform(context.getMatrices());
    }
}
