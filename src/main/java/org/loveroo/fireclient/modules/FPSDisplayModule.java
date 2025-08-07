package org.loveroo.fireclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;

import java.util.ArrayList;
import java.util.List;

public class FPSDisplayModule extends ModuleBase {

    private final Color color1 = Color.fromARGB(0xD3FFBF);
    private final Color color2 = Color.fromARGB(0xE8EBE6);

    public FPSDisplayModule() {
        super(new ModuleData("fps_display", "\uD83D\uDCCA FPSDisplay", "Shows your framerate"));

        getData().setHeight(8);
        getData().setWidth(40);

        getData().setPosX(4);
        getData().setPosY(30);
    }

    @Override
    public void loadJson(JSONObject json) throws JSONException {
        getData().setVisible(json.optBoolean("visible", getData().isVisible()));
    }

    @Override
    public JSONObject saveJson() throws JSONException {
        var json = new JSONObject();

        json.put("visible", getData().isVisible());

        return json;
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(getToggleVisibleButton(base.width/2 - 60, base.height/2 - 10));

        return widgets;
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
