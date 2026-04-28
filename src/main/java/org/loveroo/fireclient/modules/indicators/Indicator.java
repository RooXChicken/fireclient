package org.loveroo.fireclient.modules.indicators;

import java.util.List;

import org.json.JSONObject;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.modules.ModuleBase;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.DeltaTracker;

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

    protected abstract boolean doesDraw(Minecraft client);

    @Override
    public void draw(GuiGraphicsExtractor graphics, DeltaTracker ticks) {
        if(!canDraw()) {
            return;
        }

        var client = Minecraft.getInstance();
        if(client.player == null || !doesDraw(client)) {
            return;
        }

        transform(graphics.pose());

        var text = client.font;
        graphics.text(text, getData().getEmoji(), 0, 0, 0xFFFFFFFF, true);

        endTransform(graphics.pose());
    }

    @Override
    public List<AbstractWidget> getConfigScreen(Screen base) {
        return List.of();
    }

    @Override
    public void drawScreen(Screen base, GuiGraphicsExtractor context, float delta) { }

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
