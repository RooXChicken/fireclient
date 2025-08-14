package org.loveroo.fireclient.modules;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.ColorHelper;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.mixin.OverlayTextureAccessor;
import org.loveroo.fireclient.screen.config.ConfigScreenBase;

import java.util.ArrayList;
import java.util.List;

public class HitColorModule extends ModuleBase {

    private final String defaultColor = "B2FF0000";
    private String hitColor = "B2FF0000";

    public HitColorModule() {
        super(new ModuleData("hit_color", "✦ Hit Color", "Changes the hit color | Format: ARGB HEX"));
        getData().setShownName(generateDisplayName(0xFF0000));

        getData().setGuiElement(false);

        ClientLifecycleEvents.CLIENT_STARTED.register((client) -> {
            if(getData().isEnabled()) {
                changeColor(hitColor);
            }
        });
    }

    @Override
    public void loadJson(JSONObject json) throws JSONException {
        hitColor = json.optString("hit_color", hitColor);
        getData().setEnabled(json.optBoolean("enabled", getData().isEnabled()));
    }

    @Override
    public JSONObject saveJson() throws JSONException {
        var json = new JSONObject();

        json.put("hit_color", hitColor);
        json.put("enabled", getData().isEnabled());
        
        return json;
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var client = MinecraftClient.getInstance();
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 + 30));

        var colorField = new TextFieldWidget(client.textRenderer, base.width/2 - 36, base.height/2 + 60, 72, 15, Text.of(""));
        colorField.setMaxLength(8);

        colorField.setText(hitColor);
        colorField.setChangedListener(this::colorFieldChanged);

        widgets.add(colorField);
        return widgets;
    }

    @Override
    public void enableButtonPressed(ButtonWidget button) {
        super.enableButtonPressed(button);

        changeColor((getData().isEnabled()) ? hitColor : defaultColor);
    }

    public void colorFieldChanged(String text) {
        hitColor = text;
        hitColor += ("0".repeat(Math.max(0, 8 - text.length())));

        if(getData().isEnabled()) {
            changeColor(hitColor);
        }
    }

    @Override
    public void closeScreen(Screen screen) {
        var client = MinecraftClient.getInstance();
        client.player.hurtTime = 0;

        FireClientside.saveConfig();
    }

    @Override
    public void drawScreen(Screen base, DrawContext context) {
        var client = MinecraftClient.getInstance();
        client.player.hurtTime = 11;

        int i = base.width/4;
        int j = base.height/4;

        float scale = 1.4f;
        int off = 50;

        InventoryScreen.drawEntity(context, (int)((i+26-off)*2), (int)((j-8-off)*2), (int)((i+75-off)*2), (int)((j+78-off)*2), (int)(30*scale), 0.0625F, ((ConfigScreenBase)base).getMouseX(), ((ConfigScreenBase)base).getMouseY(), client.player);
    }

    public void changeColor(String color) {
        changeColor((int)Long.parseLong(color.toLowerCase(), 16));
    }

    public void changeColor(int color) {
        var client = MinecraftClient.getInstance();

        var overlayTexture = ((OverlayTextureAccessor)client.gameRenderer.getOverlayTexture()).getTexture();
        var nativeImage = overlayTexture.getImage();

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (i < 8) {
                    nativeImage.setColorArgb(j, i, color);
                } else {
                    int k = (int)((1.0F - j / 15.0F * 0.75F) * 255.0F);
                    nativeImage.setColorArgb(j, i, ColorHelper.withAlpha(k, Colors.WHITE));
                }
            }
        }

        RenderSystem.activeTexture(GlConst.GL_TEXTURE1);
        overlayTexture.bindTexture();
        overlayTexture.setFilter(false, false);
        overlayTexture.setClamp(true);
        nativeImage.upload(0, 0, 0, 0, 0, nativeImage.getWidth(), nativeImage.getHeight(), false);
        RenderSystem.activeTexture(GlConst.GL_TEXTURE0);
    }
}
