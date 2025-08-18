package org.loveroo.fireclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.world.Heightmap;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;
import org.loveroo.fireclient.mixin.modules.WorldRendererAccessor;

import java.util.ArrayList;
import java.util.List;

public class HighestBlockModule extends ModuleBase {

    private final Color color1 = Color.fromRGB(0xD4D4D4);
    private final Color color2 = Color.fromRGB(0xC0DEB4);

    public HighestBlockModule() {
        super(new ModuleData("highest_block", "⬆ Highest Block", "Shows the world's highest block at your location"));
        getData().setShownName(generateDisplayName(0xD4D4D4));

        getData().setHeight(8);
        getData().setWidth(30);

        getData().setPosX(2, 640);
        getData().setPosY(62, 360);

        getData().setVisible(false);

        FireClientside.getKeybindManager().registerKeybind(
                new Keybind("highest_block", Text.of("Toggle"), Text.of("Toggle ").copy().append(getData().getShownName()).append("'s visibility"), true, null,
                        () -> getData().setVisible(!getData().isVisible()), null)
        );
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("highest_block").getRebindButton(5, base.height - 25, 120,20));
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
        if(client.player == null) {
            return;
        }

        var text = client.textRenderer;

        var pos = client.player.getBlockPos();
        var chunk = client.player.clientWorld.getChunk(pos);

        var msg = "Height: " + chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, pos.getX(), pos.getZ());
        var heightText = RooHelper.gradientText(msg, color1, color2);

        getData().setWidth(text.getWidth(heightText));

        context.drawText(text, heightText, 0, 0, 0xFFFFFFFF, true);

        endTransform(context.getMatrices());
    }
}
