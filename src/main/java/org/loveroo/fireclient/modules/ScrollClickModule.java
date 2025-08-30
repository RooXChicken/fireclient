package org.loveroo.fireclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.JsonOption;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;
import org.loveroo.fireclient.mixin.modules.scrollclick.BoundKeyAccessor;

import java.util.ArrayList;
import java.util.List;

public class ScrollClickModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0xC9B5B5);

    private int leftClicks = 0;
    private int rightClicks = 0;

    private boolean disableWithPerspective = false;

    @JsonOption(name = "scroll_mode")
    private ScrollMode mode = ScrollMode.SINGLE;

    @JsonOption(name = "single_click_type")
    private SingleClickType singleClickType = SingleClickType.USE;

    @JsonOption(name = "dual_click_type")
    private SingleClickType dualClickType = SingleClickType.USE;

    public ScrollClickModule() {
        super(new ModuleData("scroll_click", "\uD83D\uDDB1", color));

        getData().setGuiElement(false);
        getData().setEnabled(false);

        var toggleBind = new Keybind("toggle_scroll_click",
                Text.translatable("fireclient.keybind.generic.toggle.name"),
                Text.translatable("fireclient.keybind.generic.toggle.description", getData().getShownName()),
                true, null,
                () -> getData().setEnabled(!getData().isEnabled()), null);

        FireClientside.getKeybindManager().registerKeybind(toggleBind);
    }

    @Override
    public void update(MinecraftClient client) {
        if(!getData().isEnabled() || client.player == null || client.currentScreen != null) {
            rightClicks = 0;
            leftClicks = 0;

            return;
        }

        if(rightClicks > 0) {
            rightClicks = Math.min(3, rightClicks - 1);

            var keyAccessor = (BoundKeyAccessor)client.options.useKey;
            KeyBinding.onKeyPressed(keyAccessor.getBoundKey());
        }

        if(leftClicks > 0) {
            leftClicks = Math.min(3, leftClicks - 1);

            var keyAccessor = (BoundKeyAccessor)client.options.attackKey;
            KeyBinding.onKeyPressed(keyAccessor.getBoundKey());
        }
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("toggle_scroll_click").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 - 10));

        widgets.add(ButtonWidget.builder(getScrollModeText(), this::scrollModeChanged)
                .dimensions(base.width/2 - 130, base.height/2 + 20, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.scroll_click.scroll_type.tooltip")))
                .build());

        widgets.add(ButtonWidget.builder(getClickTypeText(), this::clickTypeChanged)
                .dimensions(base.width/2 + 10, base.height/2 + 20, 120, 20)
                .tooltip(Tooltip.of(Text.translatable("fireclient.module.scroll_click.click_type.tooltip")))
                .build());

        return widgets;
    }

    private void scrollModeChanged(ButtonWidget button) {
        mode = (mode == ScrollMode.SINGLE) ? ScrollMode.DUAL : ScrollMode.SINGLE;
        reloadScreen();
    }

    private Text getScrollModeText() {
        return Text.translatable("fireclient.module.scroll_click.click_type.name", mode.getName());
    }

    private void clickTypeChanged(ButtonWidget button) {
        switch(mode) {
            case SINGLE -> {
                singleClickType = (singleClickType == SingleClickType.USE) ? SingleClickType.ATTACK : SingleClickType.USE;
            }

            case DUAL -> {
                dualClickType = (dualClickType == SingleClickType.USE) ? SingleClickType.ATTACK : SingleClickType.USE;
            }
        }

        button.setMessage(getClickTypeText());
    }

    private Text getClickTypeText() {
        switch(mode) {
            case SINGLE -> { return singleClickType.getName(); }
            case DUAL -> { return Text.translatable("fireclient.module.scroll_click.dual_click_type.direction", dualClickType.getName()); }
        }

        return Text.of("");
    }

    public void incrementClicks(double direction) {
        switch(mode) {
            case SINGLE -> incrementSingle();
            case DUAL -> incrementDouble(direction);
        }
    }

    private void incrementSingle() {
        switch(singleClickType) {
            case USE -> rightClicks++;
            case ATTACK -> leftClicks++;
        }
    }

    private void incrementDouble(double direction) {
        if(direction > 0) {
            switch(dualClickType) {
                case USE -> rightClicks++;
                case ATTACK -> leftClicks++;
            }
        }
        else {
            switch(dualClickType) {
                case USE -> leftClicks++;
                case ATTACK -> rightClicks++;
            }
        }
    }

    public enum ScrollMode {
        SINGLE("Single"),
        DUAL("Dual");

        private final String name;

        ScrollMode(String name) {
            this.name = name;
        }

        public Text getName() {
            return Text.translatable("fireclient.module.scroll_click.click_type." + name.toLowerCase());
        }
    }

    public enum SingleClickType {
        USE("Use"),
        ATTACK("Attack");

        private final String name;

        SingleClickType(String name) {
            this.name = name;
        }

        public Text getName() {
            return Text.translatable("fireclient.module.scroll_click.single_click_type." + name.toLowerCase());
        }
    }

    public boolean isDisableWithPerspective() {
        return disableWithPerspective;
    }
}
