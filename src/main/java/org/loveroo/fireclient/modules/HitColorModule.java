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
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.JsonOption;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.mixin.OverlayTextureAccessor;
import org.loveroo.fireclient.screen.base.ConfigScreenBase;
import org.loveroo.fireclient.screen.widgets.ColorPickerWidget;

import java.util.ArrayList;
import java.util.List;

public class HitColorModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0xFF3333);

    private final String defaultColor = "B2FF0000";

    @JsonOption(name = "hit_color")
    private String hitColor = "B2FF0000";

    public HitColorModule() {
        super(new ModuleData("hit_color", "✦", color));

        getData().setGuiElement(false);

        ClientLifecycleEvents.CLIENT_STARTED.register((client) -> {
            if(getData().isEnabled()) {
                changeColor(hitColor);
            }

            getData().setOnEnableChanged(() -> {
                changeColor((getData().isEnabled()) ? hitColor : defaultColor);
            });
        });
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 + 30));

        var colorPicker = new ColorPickerWidget(base.width/2 - 36, base.height/2 + 60, getHitColor(hitColor), this::hitColorChanged);
        widgets.add(colorPicker);

        colorPicker.registerWidgets(base);
        return widgets;
    }

    @Override
    public void closeScreen(Screen screen) {
        var client = MinecraftClient.getInstance();
        if(client.player != null) {
            client.player.hurtTime = 0;
        }

        FireClientside.saveConfig();
    }

    @Override
    public void drawScreen(Screen base, DrawContext context, float delta) {
        super.drawScreenHeader(context, base.width/2, base.height/2 - 100);

        var client = MinecraftClient.getInstance();
        if(client.player == null) {
            return;
        }
        
        client.player.hurtTime = 11;

        int i = base.width/4;
        int j = base.height/4;

        float scale = 1.4f;
        int off = 50;

        InventoryScreen.drawEntity(context, (i+26-off)*2, (j-8-off)*2, (i+75-off)*2, (j+78-off)*2, (int)(30*scale), 0.0625F, ((ConfigScreenBase)base).getMouseX(), ((ConfigScreenBase)base).getMouseY(), client.player);
    }

    private void hitColorChanged(int color) {
        hitColor = Integer.toHexString(color).toUpperCase();

        if(getData().isEnabled()) {
            changeColor(hitColor);
        }
    }

    public void changeColor(String color) {
        changeColor(getHitColor(color));
    }

    public int getHitColor(String color) {
        return (int)Long.parseLong(color, 16);
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
