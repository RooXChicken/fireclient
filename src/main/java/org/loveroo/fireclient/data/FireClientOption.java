package org.loveroo.fireclient.data;

import net.minecraft.text.Text;

public enum FireClientOption {
    FIX_TRIDENT_RIPTIDE(1, FireClientOptionType.TOGGLE),
    BRANDING(1, FireClientOptionType.TOGGLE),
    PREVENT_UNTOGGLE_STICKY(1, FireClientOptionType.TOGGLE),
    HAZELI_MODE(0, FireClientOptionType.TOGGLE),
    DONT_RESET_DEATH(1, FireClientOptionType.TOGGLE),
    EXTINGUISH_FIX(1, FireClientOptionType.TOGGLE),
    SHOW_HIDDEN_MODULES(1, FireClientOptionType.TOGGLE),
    SHOW_TUTORIAL_TEXT(1, FireClientOptionType.TOGGLE),
    PREVENT_HIDING_ENTRIES(0, FireClientOptionType.TOGGLE),
    SHOW_PING_NUMBER(0, FireClientOptionType.TOGGLE),
    BLAZE_POWDER_FILL(0, FireClientOptionType.TOGGLE),
    SHOW_MODULES_DEBUG(1, FireClientOptionType.TOGGLE);

    private final Text name;
    private final Text description;
    private final int defaultValue;

    private final FireClientOptionType type;

    FireClientOption(int defaultValue, FireClientOptionType type) {
        this.name = Text.translatable("fireclient.settings." + name().toLowerCase() + ".name");
        this.description = Text.translatable("fireclient.settings." + name().toLowerCase() + ".description");
        this.defaultValue = defaultValue;

        this.type = type;
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    public Text getName() {
        return name;
    }

    public Text getDescription() {
        return description;
    }

    public FireClientOptionType getType() {
        return type;
    }

    public enum FireClientOptionType {
        TOGGLE
    }
}
