package org.loveroo.fireclient.data;

/**
 * Used for all color related operations.
 * Preferred over passing hex to Minecraft because colors have additional functions (gradients being the main example)
 * @param r Red component
 * @param g Green component
 * @param b Blue component
 * @param a Alpha component
 */

public record Color(int r, int g, int b, int a) {

    // used to mask bytes in hex
    private static final int COLOR_MASK = 0xff;

    /**
     * Creates a Color object from an <b>ARGB</b> hex code<br>
     * EX: <code>#FFFFFFFF</code>
     * @param argb Hex color code
     * @return A Color object created from the ARGB hex
     */
    public static Color fromARGB(int argb) {
        var a = (argb >> 24 & COLOR_MASK);
        var r = (argb >> 16 & COLOR_MASK);
        var g = (argb >> 8 & COLOR_MASK);
        var b = (argb & COLOR_MASK);

        return new Color(r, g, b, a);
    }

    /**
     * Creates a Color object from an <b>RGBA</b> hex code<br>
     * EX: <code>#FFFFFFFF</code>
     * @param rgba Hex color code
     * @return A Color object created from the RGBA hex
     */
    public static Color fromRGBA(int rgba) {
        var a = (rgba & COLOR_MASK);
        var r = (rgba >> 24 & COLOR_MASK);
        var g = (rgba >> 16 & COLOR_MASK);
        var b = (rgba >> 8 & COLOR_MASK);

        return new Color(r, g, b, a);
    }

    /**
     * Creates a Color object from an <b>RGB</b> hex code<br>
     * Alpha is defaulted to 255<br>
     * EX: <code>#FFFFFF</code>
     * @param rgb Hex color code
     * @return A Color object created from the RGB hex
     */
    public static Color fromRGB(int rgb) {
        var a = (255);
        var r = (rgb >> 16 & COLOR_MASK);
        var g = (rgb >> 8 & COLOR_MASK);
        var b = (rgb & COLOR_MASK);

        return new Color(r, g, b, a);
    }

    /**
     * Blends between two colors based on a value from <b>0.0</b> to <b>1.0</b>
     * @param other The color to blend with
     * @param amount A value from <b>0.0</b> to <b>1.0</b> with <b>0.0</b> being fully <b>this</b>, and <b>1.0</b> being fully <b>other</b>
     * @return A new Color that is blended
     */
    public Color blend(Color other, double amount) {
        var a = blendPoints(this.a, other.a, amount);
        var r = blendPoints(this.r, other.r, amount);
        var g = blendPoints(this.g, other.g, amount);
        var b = blendPoints(this.b, other.b, amount);

        return new Color(r, g, b, a);
    }

    private int blendPoints(int p1, int p2, double amount) {
        return (int)((p2-p1) * Math.clamp(amount, 0.0, 1.0) + p1);
    }

    /**
     * Converts this Color to an ARGB integer
     * @return The integer color
     */
    public int toInt() {
        return a << 24 | r << 16 | g << 8 | b;
    }

    /**
     * Converts this Color into an <b>ARGB</b> hex string
     * @return The Color hex string
     */
    public String toARGBHex() {
        var rText = getColorHexComponent(r);
        var gText = getColorHexComponent(g);
        var bText = getColorHexComponent(b);
        var aText = getColorHexComponent(a);

        return (aText + rText + gText + bText).toUpperCase();
    }

    /**
     * Converts this Color into an <b>RGBA</b> hex string
     * @return The Color hex string
     */
    public String toRGBAHex() {
        var rText = getColorHexComponent(r);
        var gText = getColorHexComponent(g);
        var bText = getColorHexComponent(b);
        var aText = getColorHexComponent(a);

        return (rText + gText + bText + aText).toUpperCase();
    }

    /**
     * Returns a new copy of this Color
     * @return The copied Color
     */
    public Color clone() {
        return new Color(r, g, b, a);
    }

    /**
     * Converts this Color into an <b>RGB</b> hex string
     * @return The Color hex string
     */
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
