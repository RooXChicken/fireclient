package org.loveroo.fireclient.data;

public class Color {
    public int r = 255;
    public int g = 255;
    public int b = 255;
    public int a = 255;

    public Color() { }

    public Color(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(int argb) {
        this.a = argb >> 24;
        this.r = argb >> 16;
        this.g = argb >> 8;
        this.b = argb;
    }

    public Color blend(Color other, double amount) {
        var newColor = new Color();

        newColor.r = blendPoints(this.r, other.r, amount);
        newColor.g = blendPoints(this.g, other.g, amount);
        newColor.b = blendPoints(this.b, other.b, amount);
        newColor.a = blendPoints(this.a, other.a, amount);

        return newColor;
    }

    private int blendPoints(int p1, int p2, double amount) {
        return (int)((p2-p1) * amount + p1);
    }

    public int toInt() {
        return a << 24 | r << 16 | g << 8 | b;
    }
}
