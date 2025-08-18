package org.loveroo.fireclient.modules.indicators;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.modules.ModuleBase;

import java.util.List;

public abstract class Indicator extends ModuleBase {

    private final MutableText emoji;

    private boolean hasOverlay = false;
    private boolean showOverlay = true;

    public Indicator(String id, String emoji, String name, Color emojiColor, boolean hasOverlay, int index) {
        super(new ModuleData(id, emoji + " " + name, name + " indicator"));
        this.emoji = MutableText.of(new PlainTextContent.Literal(emoji)).setStyle(Style.EMPTY.withColor(emojiColor.toInt()));

        getData().setShownName(generateDisplayName(emojiColor.toInt()));
        this.hasOverlay = hasOverlay;

        getData().setWidth(6);
        getData().setHeight(6);

        getData().setPosX(2, 640);
        getData().setPosY(100 + (index * 10), 360);

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
        context.drawText(text, emoji, 0, 0, 0xFFFFFFFF, true);

        endTransform(context.getMatrices());
    }

    @Override
    public void loadJson(JSONObject json) throws JSONException {
        super.loadJson(json);

        if(hasOverlay) {
            showOverlay = json.optBoolean("show_overlay", showOverlay);
        }
    }

    @Override
    public JSONObject saveJson() throws JSONException {
        var json = super.saveJson();

        if(hasOverlay) {
            json.put("show_overlay", showOverlay);
        }

        return json;
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        return List.of();
    }

    @Override
    public void drawScreen(Screen base, DrawContext context) { }

    public boolean hasOverlay() {
        return hasOverlay;
    }

    public boolean doesShowOverlay() {
        return showOverlay;
    }

    public void setShowOverlay(boolean showOverlay) {
        this.showOverlay = showOverlay;
    }
}
