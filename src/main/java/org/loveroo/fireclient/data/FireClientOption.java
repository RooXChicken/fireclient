package org.loveroo.fireclient.data;

public enum FireClientOption {
    FIX_TRIDENT_RIPTIDE("Fix Riptide Shields", "Fixes shields obstructing the view when riptiding with a trident", 1, FireClientOptionType.TOGGLE),
    BRANDING("Branding", "Optional branding message that shows in menus", 1, FireClientOptionType.TOGGLE),
    PREVENT_UNTOGGLE_STICKY("Untoggling Keys Fix", "Prevents untoggling sticky keys (Sprint & Sneak) when respawning", 1, FireClientOptionType.TOGGLE),
    HAZELI_MODE("Hazeli Mode", "...", 0, FireClientOptionType.TOGGLE),
    DONT_RESET_DEATH("Death Buttons Mode", "Toggles whether the Death Screen buttons get reset when the window size changes (true: dont reset)", 1, FireClientOptionType.TOGGLE),
    EXTINGUISH_FIX("Fix Extinguish Sound", "Toggles whether to make the fire extinguish sound not spammable", 1, FireClientOptionType.TOGGLE);
//    FORCE_CENTER_CURSOR("Force Cursor Centering", 0, FireClientOptionType.TOGGLE);

    private final String name;
    private final String description;
    private final int defaultValue;

    private final FireClientOptionType type;

    FireClientOption(String name, String description, int defaultValue, FireClientOptionType type) {
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }
}
