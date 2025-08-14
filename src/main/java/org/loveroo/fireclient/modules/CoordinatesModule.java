package org.loveroo.fireclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.loot.function.SetFireworksLootFunction;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import java.awt.*;

public class CoordinatesModule extends ModuleBase {

    public static final Color xColor1 = new Color(247, 33, 33, 255);
    public static final Color xColor2 = new Color(176, 18, 18, 255);

    public static final Color yColor1 = new Color(47, 216, 39, 255);
    public static final Color yColor2 = new Color(28, 158, 21, 255);

    public static final Color zColor1 = new Color(76, 194, 224, 255);
    public static final Color zColor2 = new Color(40, 131, 180, 255);

    public static final Color netherColor1 = new Color(199, 57, 202, 255);
    public static final Color netherColor2 = new Color(152, 33, 149, 255);

    private boolean showOther = false;
    private boolean windowMode = false;

    private final int windowSizeX = 480;
    private final int windowSizeY = 80;

    @Nullable
    private JFrame window;

    @Nullable
    private Font font;

    @Nullable
    private JLabel coordinatesText;

    @Nullable
    private ButtonWidget windowModeButton;

    public CoordinatesModule() {
        super(new ModuleData("coordinates", "\uD83E\uDDED Coordinates", "Shows your in game coordinates, coordinates for the other dimension, and supports a separate window"));
        getData().setShownName(generateDisplayName(0x59D93F));

        getData().setHeight(8);
        getData().setWidth(110);

        getData().setPosX(2);
        getData().setPosY(14);

        try {
            System.setProperty("awt.useSystemAAFontSettings", "off");
            System.setProperty("swing.aatext", "false");

            var fontStream = getClass().getResourceAsStream("/assets/fireclient/font/font.ttf");
            if(fontStream != null) {
                font = Font.createFonts(fontStream)[0];
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            }
            else {
                FireClient.LOGGER.info("Failed to load Coordinates Module's font! Stream is null!");
            }
        }
        catch(Exception e) {
            FireClient.LOGGER.info("Failed to load Coordinates Module's font!", e);
        }

        FireClientside.getKeybindManager().registerKeybind(
                new Keybind("toggle_coordinates", Text.of("Toggle"), Text.of("Toggle ").copy().append(getData().getShownName()).append("'s visibility"), true, null,
                        () -> getData().setVisible(!getData().isVisible()), null)
        );
    }

    @Override
    public void update(MinecraftClient client) {
        if(showOther) {
            getData().setHeight(16);
        }
        else {
            getData().setHeight(8);
        }

        if(windowMode && (window == null || !window.isVisible())) {
            windowMode = false;
            closeCoordsWindow();

            if(windowModeButton != null) {
                windowModeButton.setMessage(getToggleText(Text.of("Windowed Mode"), windowMode));
            }
        }
    }

    @Override
    public void draw(DrawContext context, RenderTickCounter ticks) {
        if(!canDraw() && (window == null || !window.isVisible())) {
            return;
        }

        transform(context.getMatrices());

        if(!showOther) {
            drawNormal(context);
        }
        else {
            drawWithOther(context);
        }

        endTransform(context.getMatrices());
    }

    private void drawNormal(DrawContext context) {
        var client = MinecraftClient.getInstance();
        var text = client.textRenderer;

        var xText = String.format("X: %.2f ", client.player.getX());
        var yText = String.format("Y: %.2f ", client.player.getY());
        var zText = String.format("Z: %.2f", client.player.getZ());

        setCoordinatesText(xText, yText, zText);

        if(!canDraw()) {
            return;
        }

        var x = RooHelper.gradientText(xText, xColor1, xColor2);
        var y = RooHelper.gradientText(yText, yColor1, yColor2);
        var z = RooHelper.gradientText(zText, zColor1, zColor2);

        var coordsText = x.append(y).append(z);
        getData().setWidth(text.getWidth(xText + yText + zText));

        context.drawText(text, coordsText, 0, 0, 0xFFFFFFFF, true);
    }

    private void drawWithOther(DrawContext context) {
        var client = MinecraftClient.getInstance();
        var dimension = client.player.getWorld().getDimensionEntry().getIdAsString();

        if(!dimension.equals("minecraft:overworld") && !dimension.equals("minecraft:the_nether")) {
            drawNormal(context);
            return;
        }

        var text = client.textRenderer;

        var xPos = client.player.getX();
        var yPos = client.player.getY();
        var zPos = client.player.getZ();

        var xText = String.format("X: %.2f ", xPos);
        var yText = String.format("Y: %.2f ", yPos);
        var zText = String.format("Z: %.2f", zPos);

        var order = false;

        switch(dimension) {
            case "minecraft:overworld" -> {
                xPos /= 8.0;
                zPos /= 8.0;
                order = true;
            }

            case "minecraft:the_nether" -> {
                xPos *= 8.0;
                zPos *= 8.0;
                order = false;
            }
        }

        var otherXText = String.format("X: %.2f ", xPos);
        var otherYText = String.format("Y: %.2f ", yPos);
        var otherZText = String.format("Z: %.2f", zPos);

        var finalNormal = xText + yText + zText;
        var finalOther = otherXText + otherYText + otherZText;

        setCoordinatesText(finalNormal, finalOther, order);

        if(!canDraw()) {
            return;
        }

        MutableText normal;
        MutableText other;

        if(order) {
            normal = RooHelper.gradientText(finalNormal, yColor1, yColor2);
            other = RooHelper.gradientText(finalOther, netherColor1, netherColor2);
        }
        else {
            normal = RooHelper.gradientText(finalNormal, netherColor1, netherColor2);
            other = RooHelper.gradientText(finalOther, yColor1, yColor2);
        }

        getData().setWidth(Math.max(text.getWidth(finalNormal), text.getWidth(finalOther)));

        context.drawText(text, normal, 0, 0, 0xFFFFFFFF, true);
        context.drawText(text, other, 0, 10, 0xFFFFFFFF, true);
    }

    @Override
    public JSONObject saveJson() throws JSONException {
        var json = super.saveJson();
        json.put("show_other", showOther);
        json.put("window_mode", windowMode);

        return json;
    }

    @Override
    public void loadJson(JSONObject json) throws JSONException {
        super.loadJson(json);
        showOther = json.optBoolean("show_other", showOther);
        windowMode = json.optBoolean("show_other", windowMode);
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("toggle_coordinates").getRebindButton(5, base.height - 25, 120,20));

        widgets.add(getToggleVisibleButton(base.width/2 - 60, base.height/2 - 20));
        widgets.add(ButtonWidget.builder(getToggleText(Text.of("Other Dimension"), showOther), this::showOtherButtonPressed)
                .dimensions(base.width/2 - 60,base.height / 2 + 10, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.coordinates.other_dimension")))
                .build());

        windowModeButton = ButtonWidget.builder(getToggleText(Text.of("Windowed Mode"), windowMode), this::windowModeButtonPressed)
                .dimensions(base.width/2 - 60,base.height / 2 + 40, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.coordinates.window_mode")))
                .build();

        widgets.add(windowModeButton);
        return widgets;
    }

    @Override
    public void closeScreen(Screen screen) {
        windowModeButton = null;
    }

    public void showOtherButtonPressed(ButtonWidget button) {
        showOther = !showOther;
        button.setMessage(getToggleText(Text.of("Other Dimension"), showOther));
    }

    public void windowModeButtonPressed(ButtonWidget button) {
        windowMode = !windowMode;
        button.setMessage(getToggleText(Text.of("Windowed Mode"), windowMode));

        if(windowMode) {
            openCoordsWindow();
        }
        else {
            closeCoordsWindow();
        }
    }

    private void tryWorkaround() {
        try {
            var setDefaultHeadless = java.awt.GraphicsEnvironment.class.getDeclaredField("defaultHeadless");
            setDefaultHeadless.setAccessible(true);
            setDefaultHeadless.set(null, Boolean.FALSE);

            var setHeadlessField = java.awt.GraphicsEnvironment.class.getDeclaredField("headless");
            setHeadlessField.setAccessible(true);
            setHeadlessField.set(null, Boolean.FALSE);
        }
        catch(Exception e) {
            FireClient.LOGGER.error("Failed to apply headless workaround! Coordinates window will not work!", e);
            RooHelper.sendNotification("Failed to open window!", "Please use a different JRE!");
        }
    }

    public void openCoordsWindow() {
        if(GraphicsEnvironment.isHeadless()) {
            tryWorkaround();

            if(GraphicsEnvironment.isHeadless()) {
                return;
            }
        }

        if(window != null) {
            window.setVisible(true);
            return;
        }

        window = new JFrame();
        window.setTitle("Coordinates");
        window.getContentPane().setBackground(new java.awt.Color(43, 43, 43));
        window.setLayout(null);

        window.setBounds(0, 0, windowSizeX, windowSizeY);

        coordinatesText = new JLabel();
        coordinatesText.setFont(font);

        window.add(coordinatesText);
        window.setVisible(true);
    }

    public void closeCoordsWindow() {
        if(window == null) {
            return;
        }

//        var position = window.getLocation();
//        lastWindowX = position.x;
//        lastWindowY = position.y;

        window.setVisible(false);
    }

    // do not ask me whose idea it was
    // to have html be used in something like this
    // please don't hate me forever :c

    // (kinda cool tho :P)

    private void setCoordinatesText(String normal, String other, boolean order) {
        if(coordinatesText == null || window == null || !window.isVisible()) {
            return;
        }

        var text = new StringBuilder();
        text.append("<html> <head> <style type=\"text/css\">body { font-size: 14px; } </style> </head> <body>");

        var normalText = new StringBuilder();
        normalText.append("<p style=\"color: rgb(47, 216, 39);\">");
        normalText.append((order) ? normal : other);
        normalText.append("</p>");

        var otherText = new StringBuilder();
        otherText.append("<p style=\"color: rgb(199, 57, 202);\">");
        otherText.append((order) ? other : normal);
        otherText.append("</p>");

        if(order) {
            text.append(normalText);
            text.append(otherText);
        }
        else {
            text.append(otherText);
            text.append(normalText);
        }

        text.append("</body> </html>");
        coordinatesText.setText(text.toString());
        coordinatesText.setBounds(4, -18, windowSizeX, windowSizeY);

        Toolkit.getDefaultToolkit().sync();
    }

    private void setCoordinatesText(String x, String y, String z) {
        if(coordinatesText == null || window == null || !window.isVisible()) {
            return;
        }

        var text = new StringBuilder();
        text.append("<html> <head> <style type=\"text/css\">body { font-size: 14px; } </style> </head> <body> <p>");
        text.append("<span style=\"color: rgb(247, 33, 33);\">");
        text.append(x);
        text.append("</span>");

        text.append("<span style=\"color: rgb(47, 216, 39);\">");
        text.append(y);
        text.append("</span>");

        text.append("<span style=\"color: rgb(76, 194, 224);\">");
        text.append(z);
        text.append("</span>");

        text.append("</p> </body> </html>");
        coordinatesText.setBounds(4, -30, windowSizeX, windowSizeY);
        coordinatesText.setText(text.toString());

        Toolkit.getDefaultToolkit().sync();
    }
}
