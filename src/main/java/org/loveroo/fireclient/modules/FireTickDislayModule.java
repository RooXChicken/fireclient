package org.loveroo.fireclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;

public class FireTickDislayModule extends ModuleBase {

    private final Color color1 = new Color(230, 137, 25, 255);
    private final Color color2 = new Color(240, 204, 79, 255);

    private int fireTicks = 0;
    private int maxFire = 0;

    public FireTickDislayModule() {
        super(new ModuleData("FireTickDisplay", "fire_tick_display"));

        getData().setHeight(8);

        getData().setPosX(4);
        getData().setPosY(18);
    }

    @Override
    public void update(MinecraftClient client) {
        if(client.player == null) {
            return;
        }

        if(client.player.isOnFire()) {
            fireTicks++;
        }
        else {
            if(fireTicks != 0) {
                maxFire = fireTicks;
            }

            fireTicks = 0;
        }
    }

    @Override
    public void draw(DrawContext context, RenderTickCounter ticks) {
        if(!getData().isVisible()) {
            return;
        }

        transform(context.getMatrices());

        var client = MinecraftClient.getInstance();
        var text = client.textRenderer;

        var msg = ((fireTicks != 0) ? fireTicks : maxFire) + " ticks";
        var fireText = RooHelper.gradientText(msg, color1, color2);

        getData().setWidth(text.getWidth(fireText));

        context.drawText(text, fireText, 0, 0, 0xFFFFFFFF, true);

        endTransform(context.getMatrices());
    }
}
