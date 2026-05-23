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
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.input.MouseInput;
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

    public static final Color diagonalColor1 = new Color(255, 165, 0, 255);
    public static final Color diagonalColor2 = new Color(200, 100, 0, 255);

    @JsonOption(name = "show_other")
    private boolean showOther = false;

    @JsonOption(name = "window_mode")
    private boolean windowMode = false;

    @JsonOption(name = "show_directional_signs")
    private boolean showDirectionalSigns = true;

    @JsonOption(name = "show_compass_direction")
    private boolean showCompassDirection = true;

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
                windowModeButton.onPress(new Click(0, 0, new MouseInput(0, 0)));
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

        var xValue = client.player.getX();
        var yValue = client.player.getY();
        var zValue = client.player.getZ();
        var yaw = client.player.getYaw();

        var xSign = showDirectionalSigns ? getDirectionalSign(yaw, 'X') : "";
        var zSign = showDirectionalSigns ? getDirectionalSign(yaw, 'Z') : "";

        var xText = showDirectionalSigns ? String.format("X: %.2f (%s) ", xValue, xSign) : String.format("X: %.2f ", xValue);
        var yText = String.format("Y: %.2f ", yValue);
        var zText = showDirectionalSigns ? String.format("Z: %.2f (%s)", zValue, zSign) : String.format("Z: %.2f", zValue);

        String directionIndicator = showCompassDirection ? getCompassDirection(yaw) : "";

        setCoordinatesText(xText, yText, zText, directionIndicator);

        if(!canDraw()) {
            return;
        }

        var x = RooHelper.gradientText(xText, xColor1, xColor2);
        var y = RooHelper.gradientText(yText, yColor1, yColor2);
        var z = RooHelper.gradientText(zText, zColor1, zColor2);

        var coordsText = x.append(y).append(z);

        if (showCompassDirection) {
            var direction = RooHelper.gradientText(" " + directionIndicator, diagonalColor1, diagonalColor2);
            coordsText = coordsText.append(direction);
            getData().setWidth(text.getWidth(xText + yText + zText + " " + directionIndicator));
        } else {
            getData().setWidth(text.getWidth(xText + yText + zText));
        }

        context.drawText(text, coordsText, 0, 0, 0xFFFFFFFF, true);
    }

    private void drawWithOther(DrawContext context) {
        var client = MinecraftClient.getInstance();
        if(client.player == null) {
            return;
        }

        var dimensionEntry = client.player.getEntityWorld().getDimensionEntry().getKey();
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
        var yaw = client.player.getYaw();

        var xSign = showDirectionalSigns ? getDirectionalSign(yaw, 'X') : "";
        var zSign = showDirectionalSigns ? getDirectionalSign(yaw, 'Z') : "";

        var xText = showDirectionalSigns ? String.format("X: %.2f (%s) ", xPos, xSign) : String.format("X: %.2f ", xPos);
        var yText = String.format("Y: %.2f ", yPos);
        var zText = showDirectionalSigns ? String.format("Z: %.2f (%s)", zPos, zSign) : String.format("Z: %.2f", zPos);

        var order = false;

        if(dimension == DimensionTypes.OVERWORLD) {
            xPos /= 8.0;
            zPos /= 8.0;
            order = true;
        }
        else {
            xPos *= 8.0;
            zPos *= 8.0;
        }

        var otherXText = showDirectionalSigns ? String.format("X: %.2f (%s) ", xPos, xSign) : String.format("X: %.2f ", xPos);
        var otherYText = String.format("Y: %.2f ", yPos);
        var otherZText = showDirectionalSigns ? String.format("Z: %.2f (%s)", zPos, zSign) : String.format("Z: %.2f", zPos);

        var finalNormal = xText + yText + zText;
        var finalOther = otherXText + otherYText + otherZText;
        String directionIndicator = showCompassDirection ? getCompassDirection(yaw) : "";

        setCoordinatesText(finalNormal, finalOther, order, directionIndicator);

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

        if (showCompassDirection) {
            var direction = RooHelper.gradientText(" " + directionIndicator, diagonalColor1, diagonalColor2);
            var normalWidth = text.getWidth(finalNormal + " " + directionIndicator);
            var otherWidth = text.getWidth(finalOther + " " + directionIndicator);
            getData().setWidth(Math.max(normalWidth, otherWidth));
            context.drawText(text, normal.append(direction), 0, 0, 0xFFFFFFFF, true);
        } else {
            getData().setWidth(Math.max(text.getWidth(finalNormal), text.getWidth(finalOther)));
            context.drawText(text, normal, 0, 0, 0xFFFFFFFF, true);
        }

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

        widgets.add(new ToggleButtonWidget.ToggleButtonBuilder(Text.literal("Show Directional Signs"))
            .getValue(() -> { return showDirectionalSigns; })
            .setValue((value) -> { showDirectionalSigns = value; })
            .position(base.width/2 - 60, base.height / 2 + 50)
            .tooltip(Tooltip.of(Text.literal("Show +/- signs next to coordinates")))
            .build());

        widgets.add(new ToggleButtonWidget.ToggleButtonBuilder(Text.literal("Show Compass Direction"))
            .getValue(() -> { return showCompassDirection; })
            .setValue((value) -> { showCompassDirection = value; })
            .position(base.width/2 - 60, base.height / 2 + 80)
            .tooltip(Tooltip.of(Text.literal("Show N/S/E/W direction indicator")))
            .build());

        windowModeButton = new ToggleButtonWidget.ToggleButtonBuilder(Text.translatable("fireclient.module.coordinates.windowed_mode.name"))
            .getValue(() -> { return windowMode; })
            .setValue(this::windowModeChanged)
            .position(base.width/2 - 60,base.height / 2 + 110)
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

    private void setCoordinatesText(String normal, String other, boolean order, String directionIndicator) {
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

    private void setCoordinatesText(String x, String y, String z, String directionIndicator) {
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

        text.append("<span style=\"color: rgb(255, 165, 0);\">");
        text.append(" ").append(directionIndicator);
        text.append("</span>");

        text.append("</p> </body> </html>");
        coordinatesText.setBounds(4, -30, windowSizeX, windowSizeY);
        coordinatesText.setText(text.toString());

        Toolkit.getDefaultToolkit().sync();
    }


    private String getCompassDirection(float yaw) {
        float normalizedYaw = (yaw % 360 + 360) % 360;

        if (normalizedYaw >= 337.5 || normalizedYaw < 22.5) {
            return "S";  // +Z
        } else if (normalizedYaw >= 22.5 && normalizedYaw < 67.5) {
            return "SW";
        } else if (normalizedYaw >= 67.5 && normalizedYaw < 112.5) {
            return "W";  // -X
        } else if (normalizedYaw >= 112.5 && normalizedYaw < 157.5) {
            return "NW";
        } else if (normalizedYaw >= 157.5 && normalizedYaw < 202.5) {
            return "N";  // -Z
        } else if (normalizedYaw >= 202.5 && normalizedYaw < 247.5) {
            return "NE";
        } else if (normalizedYaw >= 247.5 && normalizedYaw < 292.5) {
            return "E";  // +X
        } else {
            return "SE";
        }
    }

    private String getDirectionalSign(float yaw, char axis) {
        float normalizedYaw = (yaw % 360 + 360) % 360;

        if (axis == 'X') {
            if (normalizedYaw >= 202.5 && normalizedYaw < 337.5) {
                return "+"; // east or positive x
            } else {
                return "-"; // west or negative x
            }
        } else if (axis == 'Z') {
            if (normalizedYaw >= 22.5 && normalizedYaw < 202.5) {
                return "-"; // north or negative z
            } else {
                return "+"; // south or positive z
            }
        }
        return "";
    }
}
