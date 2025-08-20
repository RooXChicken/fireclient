package org.loveroo.fireclient.data;

public record Color(int r, int g, int b, int a) {

    private static final int COLOR_MASK = 0xff;

    public static Color fromARGB(int argb) {
        var a = (argb >> 24 & COLOR_MASK);
        var r = (argb >> 16 & COLOR_MASK);
        var g = (argb >> 8 & COLOR_MASK);
        var b = (argb & COLOR_MASK);

        return new Color(r, g, b, a);
    }

    public static Color fromRGBA(int argb) {
        var a = (argb & COLOR_MASK);
        var r = (argb >> 24 & COLOR_MASK);
        var g = (argb >> 16 & COLOR_MASK);
        var b = (argb >> 8 & COLOR_MASK);

        return new Color(r, g, b, a);
    }

    public static Color fromRGB(int argb) {
        var a = (255);
        var r = (argb >> 16 & COLOR_MASK);
        var g = (argb >> 8 & COLOR_MASK);
        var b = (argb & COLOR_MASK);

        return new Color(r, g, b, a);
    }

    public Color blend(Color other, double amount) {
        var a = blendPoints(this.a, other.a, amount);
        var r = blendPoints(this.r, other.r, amount);
        var g = blendPoints(this.g, other.g, amount);
        var b = blendPoints(this.b, other.b, amount);

        return new Color(r, g, b, a);
    }

    private int blendPoints(int p1, int p2, double amount) {
        return (int)((p2-p1) * amount + p1);
    }

    public int toInt() {
        return a << 24 | r << 16 | g << 8 | b;
    }

    public String toARGBHex() {
        var rText = getColorHexComponent(r);
        var gText = getColorHexComponent(g);
        var bText = getColorHexComponent(b);
        var aText = getColorHexComponent(a);

        return (aText + rText + gText + bText).toUpperCase();
    }

    public String toRGBAHex() {
        var rText = getColorHexComponent(r);
        var gText = getColorHexComponent(g);
        var bText = getColorHexComponent(b);
        var aText = getColorHexComponent(a);

        return (rText + gText + bText + aText).toUpperCase();
    }

    public String toRGBHex() {
        var rText = getColorHexComponent(r);
        var gText = getColorHexComponent(g);
        var bText = getColorHexComponent(b);

        return (rText + gText + bText).toUpperCase();
    }

    private String getColorHexComponent(int color) {
        var text = Integer.toHexString(color);
        return "0".repeat(2 - text.length()) + text;
    }
}
