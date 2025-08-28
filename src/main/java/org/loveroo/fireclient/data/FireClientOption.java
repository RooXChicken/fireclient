package org.loveroo.fireclient.data;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.loveroo.fireclient.client.FireClientside;

public enum FireClientOption {
    FIX_TRIDENT_RIPTIDE(new ToggleOptionData(1)),
    BRANDING(new ToggleOptionData(1)),
    PREVENT_UNTOGGLE_STICKY(new ToggleOptionData(1)),
    HAZELI_MODE(new ToggleOptionData(0)),
    DONT_RESET_DEATH(new ToggleOptionData(1)),
    EXTINGUISH_FIX(new ToggleOptionData(1)),
    SHOW_HIDDEN_MODULES(new ToggleOptionData(1)),
    SHOW_TUTORIAL_TEXT(new ToggleOptionData(1)),
    PREVENT_HIDING_ENTRIES(new ToggleOptionData(0)),
    SHOW_PING_NUMBER(new ToggleOptionData(0)),
    BLAZE_POWDER_FILL(new ToggleOptionData(0)),
    SHOW_MODULES_DEBUG(new ToggleOptionData(1)),
    CACHE_UUID(new ToggleOptionData(1)),
    CAP_PARTICLE_COUNT(new ToggleOptionData(1)),
    
    CHAT_HISTORY(new SliderOptionData(1, 100, 2000) {

        @Override
        public Text updateMessage(FireClientOption option, int amount) {
            MutableText amountText;

            if(amount >= ((FireClientOption.SliderOptionData)option.getData()).getMaxValue()) {
                amountText = Text.translatable("fireclient.settings.chat_history.unlimited");
            }
            else {
                amountText = Text.literal(String.valueOf(amount));
            }

            return Text.translatable("fireclient.settings.chat_history.name", amountText);
        }
    }),

    CLEARVIEW(new ToggleOptionData(0)),
    NO_RELOAD_OVERLAY(new ToggleOptionData(0)),
    PREVENT_PACK_CLEAR(new ToggleOptionData(1));
//    DISABLE_GRADIENT(0, FireClientOptionType.TOGGLE);

    private final Text name;
    private final Text description;

    private final OptionData data;

    FireClientOption(OptionData data) {
        this.name = Text.translatable("fireclient.settings." + name().toLowerCase() + ".name");
        this.description = Text.translatable("fireclient.settings." + name().toLowerCase() + ".description");

        this.data = data;
    }

    public OptionData getData() {
        return data;
    }

    public int getDefaultValue() {
        return data.getDefaultValue();
    }

    public Text getName() {
        return name;
    }

    public Text getDescription() {
        return description;
    }

    public OptionType getType() {
        return data.getType();
    }

    public int getValue() {
        return FireClientside.getSetting(this);
    }

    public void setValue(int value) {
        FireClientside.setSetting(this, value);
    }

    public enum OptionType {
        TOGGLE,
        SLIDER,
    }

    public static abstract class OptionData {

        protected final OptionType type;
        protected final int defaultValue;

        OptionData(OptionType type, int defaultValue) {
            this.type = type;
            this.defaultValue = defaultValue;
        }

        public OptionType getType() {
            return type;
        }

        public int getDefaultValue() {
            return defaultValue;
        }
    }

    public static class ToggleOptionData extends OptionData {

        public ToggleOptionData(int defaultValue) {
            super(OptionType.TOGGLE, defaultValue);
        }
    }

    public static class SliderOptionData extends OptionData {

        private final int minValue;
        private final int maxValue;

        public SliderOptionData(int minValue, int defaultValue, int maxValue) {
            super(OptionType.SLIDER, defaultValue);

            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public Text updateMessage(FireClientOption option, int amount) {
            return Text.translatable("fireclient.settings." + option.name().toLowerCase() + ".name", amount);
        }

        public int getMinValue() {
            return minValue;
        }

        public int getMaxValue() {
            return maxValue;
        }
    }
}
