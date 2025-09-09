package org.loveroo.fireclient.modules.indicators;

import java.util.List;

import org.json.JSONObject;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.modules.ModuleBase;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderTickCounter;

public abstract class Indicator extends ModuleBase {

    private boolean hasOverlay = false;
    private boolean showOverlay = true;

    public Indicator(String id, String emoji, Color color, boolean hasOverlay, int index) {
        super(new ModuleData(id, emoji, color));
        this.hasOverlay = hasOverlay;

        getData().setWidth(6);
        getData().setHeight(6);

        getData().setDefaultPosX(2, 640);
        getData().setDefaultPosY(120 + (index * 9), 360);

        getData().setSkip(true);

        FireClientside.registerModule(this);
    }

    protected abstract boolean doesDraw(MinecraftClient client);

    @Override
    public void draw(DrawContext context, RenderTickCounter ticks) {
        if(!canDraw()) {
            return;
        }

        var client = MinecraftClient.getInstance();
        if(client.player == null || !doesDraw(client)) {
            return;
        }

        transform(context.getMatrices());

        var text = client.textRenderer;
        context.drawText(text, getData().getEmoji(), 0, 0, 0xFFFFFFFF, true);

        endTransform(context.getMatrices());
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        return List.of();
    }

    @Override
    public void drawScreen(Screen base, DrawContext context, float delta) { }

    public boolean hasOverlay() {
        return hasOverlay;
    }

    public boolean doesShowOverlay() {
        return showOverlay;
    }

    public void setShowOverlay(boolean showOverlay) {
        this.showOverlay = showOverlay;
    }

    @Override
    public void loadJson(JSONObject json) throws Exception {
        super.loadJson(json);

        showOverlay = json.optBoolean("show_overlay", showOverlay);
    }

    @Override
    public JSONObject saveJson() throws Exception {
        var json = super.saveJson();

        json.put("show_overlay", showOverlay);

        return json;
    }
}
