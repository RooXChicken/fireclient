package org.loveroo.fireclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;

public class FPSDisplayModule extends ModuleBase {

    private final Color color1 = Color.fromARGB(0xD3FFBF);
    private final Color color2 = Color.fromARGB(0xE8EBE6);

    public FPSDisplayModule() {
        super(new ModuleData("FPSDisplay", "fps_display"));

        getData().setHeight(8);

        getData().setPosX(4);
        getData().setPosY(30);
    }

    @Override
    public void draw(DrawContext context, RenderTickCounter ticks) {
        if(!getData().isVisible()) {
            return;
        }

        transform(context.getMatrices());

        var client = MinecraftClient.getInstance();
        var text = client.textRenderer;

        var msg = client.getCurrentFps() + " FPS";
        var fpsText = RooHelper.gradientText(msg, color1, color2);

        getData().setWidth(text.getWidth(fpsText));

        context.drawText(text, fpsText, 0, 0, 0xFFFFFFFF, true);

        endTransform(context.getMatrices());
    }
}
