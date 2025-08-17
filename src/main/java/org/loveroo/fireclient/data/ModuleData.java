package org.loveroo.fireclient.data;

import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;

public class ModuleData {
    private final String id;

    private final String name;
    private final String description;
    private MutableText shownName;

    private double posX = 0;
    private double posY = 0;

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

    public ModuleData(String id, String name, String description) {
        this.id = id;

        this.name = name;
        this.description = description;
        shownName = MutableText.of(new PlainTextContent.Literal(getName()));
    }

    public MutableText getShownName() {
        return shownName;
    }

    public MutableText getTooltip(boolean showTransformation) {
        var transform = new StringBuilder();

        if(showTransformation) {
            transform.append("\nX: ");
            transform.append((int) getPosX());

            transform.append(" Y: ");
            transform.append((int) getPosY());

            transform.append("\nScale: ");
            transform.append(String.format("%.2f", getScale()));
        }

        return shownName.copy().append(transform.toString());
    }

    public void setShownName(MutableText shownName) {
        this.shownName = shownName;
    }

    public String getName() {
        return name;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
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

    public String getDescription() {
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
}