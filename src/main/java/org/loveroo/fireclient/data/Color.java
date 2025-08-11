package org.loveroo.fireclient.data;

public record Color(int r, int g, int b, int a) {

    public static Color fromARGB(int argb) {
        var a = (byte)argb >> 24;
        var r = (byte)argb >> 16;
        var g = (byte)argb >> 8;
        var b = (byte)argb;

        return new Color(r, g, b, a);
    }

    public static Color fromRGBA(int argb) {
        var r = (byte)argb >> 24;
        var g = (byte)argb >> 16;
        var b = (byte)argb >> 8;
        var a = (byte)argb;

        return new Color(r, g, b, a);
    }

    public static Color fromRGB(int argb) {
        var r = (byte)argb >> 16;
        var g = (byte)argb >> 8;
        var b = (byte)argb;
        var a = 255;

        return new Color(r, g, b, a);
    }

    public Color blend(Color other, double amount) {
        var r = blendPoints(this.r, other.r, amount);
        var g = blendPoints(this.g, other.g, amount);
        var b = blendPoints(this.b, other.b, amount);
        var a = blendPoints(this.a, other.a, amount);

        return new Color(r, g, b, a);
    }

    private int blendPoints(int p1, int p2, double amount) {
        return (int)((p2-p1) * amount + p1);
    }

    public int toInt() {
        return a << 24 | r << 16 | g << 8 | b;
    }
}
