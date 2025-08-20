package org.loveroo.fireclient.data;

public enum FireClientOption {
    FIX_TRIDENT_RIPTIDE("Fix Riptide Shields", "Fixes shields obstructing the view when riptiding with a trident", 1, FireClientOptionType.TOGGLE),
    BRANDING("Branding", "Optional branding message that shows in menus", 1, FireClientOptionType.TOGGLE),
    PREVENT_UNTOGGLE_STICKY("Fix Untoggling Keys", "Prevents untoggling sticky keys (Sprint & Sneak) when respawning", 1, FireClientOptionType.TOGGLE),
    HAZELI_MODE("Hazeli Mode", "...", 0, FireClientOptionType.TOGGLE),
    DONT_RESET_DEATH("Fix Death Buttons", "Fixes the Death Screen buttons being reset when the window size changes", 1, FireClientOptionType.TOGGLE),
    EXTINGUISH_FIX("Fix Extinguish Sound", "Fixes the fire extinguish sound being spammed", 1, FireClientOptionType.TOGGLE),
    SHOW_HIDDEN_MODULES("Show Hidden Modules", "Shows module outlines when they are hidden", 1, FireClientOptionType.TOGGLE),
    SHOW_TUTORIAL_TEXT("Show Tutorial Text", "Shows the transform tutorial in the Main Config screen", 1, FireClientOptionType.TOGGLE),
    PREVENT_HIDING_ENTRIES("Prevent Hiding Entries", "Prevents hiding the player list entries in the tab list", 0, FireClientOptionType.TOGGLE),
    SHOW_PING_NUMBER("Show Ping Number", "Shows a player's latency as a number", 0, FireClientOptionType.TOGGLE),
    BLAZE_POWDER_FILL("Blaze Powder Fill", "Prevents blaze powder from being autofilled into the fuel slot (makes it an ingredient instead)", 0, FireClientOptionType.TOGGLE);

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

    public enum FireClientOptionType {
        TOGGLE
    }
}
