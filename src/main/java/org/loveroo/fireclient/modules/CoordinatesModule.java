package org.loveroo.fireclient.modules;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.jetbrains.annotations.Nullable;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.JsonOption;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;
import org.loveroo.fireclient.screen.widgets.ToggleButtonWidget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.dimension.DimensionTypes;

public class CoordinatesModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0x59D93F);

    public static final Color xColor1 = new Color(247, 33, 33, 255);
    public static final Color xColor2 = new Color(176, 18, 18, 255);

    public static final Color yColor1 = new Color(47, 216, 39, 255);
    public static final Color yColor2 = new Color(28, 158, 21, 255);

    public static final Color zColor1 = new Color(76, 194, 224, 255);
    public static final Color zColor2 = new Color(40, 131, 180, 255);

    public static final Color netherColor1 = new Color(199, 57, 202, 255);
    public static final Color netherColor2 = new Color(152, 33, 149, 255);

    @JsonOption(name = "show_other")
    private boolean showOther = false;

    @JsonOption(name = "window_mode")
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
    private ToggleButtonWidget windowModeButton;

    public CoordinatesModule() {
        super(new ModuleData("coordinates", "\uD83E\uDDED", color));

        getData().setHeight(8);
        getData().setWidth(110);

        getData().setDefaultPosX(2, 640);
        getData().setDefaultPosY(13, 360);

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

        var toggleBind = new Keybind("toggle_coordinates",
                Text.translatable("fireclient.keybind.generic.toggle.name"),
                Text.translatable("fireclient.keybind.generic.toggle_visibility.description", getData().getShownName()),
                true, null,
                () -> getData().setVisible(!getData().isVisible()), null);

        FireClientside.getKeybindManager().registerKeybind(toggleBind);
    }

    @Override
    public void update(MinecraftClient client) {
        if(showOther) {
            getData().setHeight(18);
        }
        else {
            getData().setHeight(8);
        }

        if(windowMode && (window == null || !window.isVisible())) {
            if(windowModeButton != null) {
                windowModeButton.onPress();
            }
        }
    }

    @Override
    public void draw(DrawContext context, RenderTickCounter ticks) {
        if(!canDraw() && (window == null || !window.isVisible())) {
            return;
        }

        var client = MinecraftClient.getInstance();
        if(client.player == null) {
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
        if(client.player == null) {
            return;
        }

        var dimensionEntry = client.player.getWorld().getDimensionEntry().getKey();
        if(dimensionEntry.isEmpty()) {
            return;
        }

        var dimension = dimensionEntry.get();

        if(dimension != DimensionTypes.OVERWORLD && dimension != DimensionTypes.THE_NETHER) {
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

        if(dimension == DimensionTypes.OVERWORLD) {
            xPos /= 8.0;
            zPos /= 8.0;
            order = true;
        }
        else {
            xPos *= 8.0;
            zPos *= 8.0;
            order = false;
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
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("toggle_coordinates").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleVisibleButton(base.width/2 - 60, base.height/2 - 10));

        widgets.add(new ToggleButtonWidget.ToggleButtonBuilder(Text.translatable("fireclient.module.coordinates.other_dimension.name"))
            .getValue(() -> { return showOther; })
            .setValue((value) -> { showOther = value; })
            .position(base.width/2 - 60,base.height / 2 + 20)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.coordinates.other_dimension.tooltip")))
            .build());

        windowModeButton = new ToggleButtonWidget.ToggleButtonBuilder(Text.translatable("fireclient.module.coordinates.windowed_mode.name"))
            .getValue(() -> { return windowMode; })
            .setValue(this::windowModeChanged)
            .position(base.width/2 - 60,base.height / 2 + 50)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.coordinates.windowed_mode.tooltip")))
            .build();

        widgets.add(windowModeButton);
        return widgets;
    }

    @Override
    public void closeScreen(Screen screen) {
        windowModeButton = null;
    }
    
    public void windowModeChanged(boolean value) {
        windowMode = value;

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

            RooHelper.sendNotification(
                Text.translatable("fireclient.module.coordinates.window_fail.name"),
                Text.translatable("fireclient.module.coordinates.window_fail.contents"));
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
