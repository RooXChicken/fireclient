package org.loveroo.fireclient.data;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Text;

public class ModuleData {
    private final String id;

    private final MutableText emoji;
    private final MutableText shownName;
    private final MutableText description;

    private double posX = 0;
    private double posY = 0;

    private double defaultPosX = 0;
    private double defaultPosY = 0;

    private double scale = 1;

    private double snapX = 5.0;
    private double snapY = 8.0;
    private double snapScale = 0.25;

    private double width = 0;
    private double height = 0;

    private boolean skip = false;
    private boolean visible = false;
    private boolean enabled = false;
    private boolean guiElement = true;

    public ModuleData(String id, String emoji, Color color) {
        this.id = id;
        this.emoji = Text.literal(emoji + " ").withColor(color.toInt());

        this.shownName = this.emoji.copy().append(Text.translatable("fireclient.module." + id + ".name").withColor(0xFFFFFFFF));
        this.description = Text.translatable("fireclient.module." + id + ".description");
    }

    public MutableText getShownName() {
        return shownName;
    }

    public MutableText getEmoji() {
        return emoji;
    }

    public MutableText getTooltip(boolean showTransformation) {
        var transform = new StringBuilder();

        if(showTransformation) {
            transform.append("\nX: ");
            transform.append(getPosX());

            transform.append(" Y: ");
            transform.append(getPosY());

            transform.append("\nScale: ");
            transform.append(String.format("%.2f", getScale()));
        }

        return shownName.copy().append(transform.toString());
    }

    public int getPosX() {
        var client = MinecraftClient.getInstance();
        var width = client.getWindow().getScaledWidth();

        return (int)Math.round(posX * width);
    }

    public void setPosX(int x) {
        var client = MinecraftClient.getInstance();
        var width = client.getWindow().getScaledWidth();

        setPosX(x, width);
    }

    public void setPosX(int x, int screenWidth) {
        posX = ((double)x / screenWidth);
    }

    public int getPosY() {
        var client = MinecraftClient.getInstance();
        var height = client.getWindow().getScaledHeight();

        return (int)Math.round(posY * height);
    }

    public void setPosY(int y) {
        var client = MinecraftClient.getInstance();
        var height = client.getWindow().getScaledHeight();

        setPosY(y, height);
    }

    public void setPosY(int y, int screenHeight) {
        posY = ((double)y / screenHeight);
    }

    public double getRawPosX() {
        return posX;
    }

    public double getRawPosY() {
        return posY;
    }

    public void setRawPosX(double posX) {
        this.posX = posX;
    }

    public void setRawPosY(double posY) {
        this.posY = posY;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getId() {
        return id;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isGuiElement() {
        return guiElement;
    }

    public void setGuiElement(boolean guiElement) {
        this.guiElement = guiElement;
    }

    public Text getDescription() {
        return description;
    }

    public double getSnapX() {
        return snapX;
    }

    public void setSnapX(double snapX) {
        this.snapX = snapX;
    }

    public double getSnapY() {
        return snapY;
    }

    public void setSnapY(double snapY) {
        this.snapY = snapY;
    }

    public double getSnapScale() {
        return snapScale;
    }

    public void setSnapScale(double snapScale) {
        this.snapScale = snapScale;
    }

    public double getDefaultPosX() {
        return defaultPosX;
    }

    public void setDefaultPosX(double defaultPosX, int screenWidth) {
        this.defaultPosX = defaultPosX/screenWidth;
    }

    public double getDefaultPosY() {
        return defaultPosY;
    }

    public void setDefaultPosY(double defaultPosY, int screenHeight) {
        this.defaultPosY = defaultPosY/screenHeight;
    }
}