package org.loveroo.fireclient.data;

public enum FireClientOption {
    FIX_TRIDENT_RIPTIDE("Fix Riptide Shields", 0, FireClientOptionType.TOGGLE),
    BRANDING("Branding", 1, FireClientOptionType.TOGGLE);
//    FORCE_CENTER_CURSOR("Force Cursor Centering", 0, FireClientOptionType.TOGGLE);

    private final String name;
//    private final String description;
    private final int defaultValue;

    private final FireClientOptionType type;

    FireClientOption(String name, int defaultValue, FireClientOptionType type) {
        this.name = name;
        this.defaultValue = defaultValue;

        this.type = type;
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    public String getName() {
        return name;
    }

    public FireClientOptionType getType() {
        return type;
    }
}
