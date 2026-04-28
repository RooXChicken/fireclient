package org.loveroo.fireclient.modules;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.util.ARGB;
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
    public List<AbstractWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<AbstractWidget>();

        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 + 30));

        var colorPicker = new ColorPickerWidget(base.width/2 - 36, base.height/2 + 60, getHitColor(hitColor), this::hitColorChanged);
        widgets.add(colorPicker);

        colorPicker.registerWidgets(base);
        return widgets;
    }

    @Override
    public void closeScreen(Screen screen) {
        var client = Minecraft.getInstance();
        if(client.player != null) {
            client.player.hurtTime = 0;
        }

        FireClientside.saveConfig();
    }

    @Override
    public void drawScreen(Screen base, GuiGraphicsExtractor context, float delta) {
        super.drawScreenHeader(context, base.width/2, base.height/2 - 100);

        var client = Minecraft.getInstance();
        if(client.player == null) {
            return;
        }
        
        client.player.hurtTime = 11;

        int i = base.width/4;
        int j = base.height/4;

        float scale = 1.4f;
        int off = 50;

        InventoryScreen.extractEntityInInventoryFollowsMouse(context, (i+26-off)*2, (j-8-off)*2, (i+75-off)*2, (j+78-off)*2, (int)(30*scale), 0.0625F, ((ConfigScreenBase)base).getMouseX(), ((ConfigScreenBase)base).getMouseY(), client.player);
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
        var client = Minecraft.getInstance();

        var overlayTexture = ((OverlayTextureAccessor)client.gameRenderer.overlayTexture()).getTexture();
        var nativeImage = overlayTexture.getPixels();

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (i < 8) {
                    nativeImage.setPixel(j, i, color);
                } else {
                    int k = (int)((1.0F - j / 15.0F * 0.75F) * 255.0F);
                    nativeImage.setPixel(j, i, ARGB.color(k, CommonColors.WHITE));
                }
            }
        }

//        overlayTexture(false, false);
//        overlayTexture.setClamp(true);
        overlayTexture.upload();
    }
}
