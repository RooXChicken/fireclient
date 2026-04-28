package org.loveroo.fireclient.modules;

import java.util.ArrayList;
import java.util.List;

import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.JsonOption;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;
import org.loveroo.fireclient.mixin.modules.scrollclick.BoundKeyAccessor;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;

public class ScrollClickModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0xC9B5B5);

    private int leftClicks = 0;
    private int rightClicks = 0;

    private boolean disableWithPerspective = false;

    private boolean activeSinceJoined = false;
    private boolean usedSinceJoined = false;

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

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            activeSinceJoined = false;
            usedSinceJoined = false;
        });

        var toggleBind = new Keybind("toggle_scroll_click",
            Component.translatable("fireclient.keybind.generic.toggle.name"),
            Component.translatable("fireclient.keybind.generic.toggle.description", getData().getShownName()),
            true, null,
            () -> getData().setEnabled(!getData().isEnabled()), null);

        FireClientside.getKeybindManager().registerKeybind(toggleBind);
    }

    @Override
    public void update(Minecraft client) {
        if(!getData().isEnabled() || client.player == null || client.screen != null) {
            rightClicks = 0;
            leftClicks = 0;

            return;
        }

        if(!activeSinceJoined) {
            activeSinceJoined = true;
            FireClient.LOGGER.info("ScrollClick is enabled!");
        }

        if(rightClicks > 0) {
            detectUsage();
            rightClicks = Math.min(3, rightClicks - 1);

            var keyAccessor = (BoundKeyAccessor)client.options.keyUse;
            KeyMapping.click(keyAccessor.getKey());
        }

        if(leftClicks > 0) {
            detectUsage();
            leftClicks = Math.min(3, leftClicks - 1);

            var keyAccessor = (BoundKeyAccessor)client.options.keyAttack;
            KeyMapping.click(keyAccessor.getKey());
        }
    }

    private void detectUsage() {
        if(!usedSinceJoined) {
            usedSinceJoined = true;
            FireClient.LOGGER.info("ScrollClick was used!");
        }
    }

    @Override
    public List<AbstractWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<AbstractWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("toggle_scroll_click").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleEnableButton(base.width/2 - 60, base.height/2 - 10));

        widgets.add(Button.builder(getScrollModeText(), this::scrollModeChanged)
                .bounds(base.width/2 - 130, base.height/2 + 20, 120, 20)
                .tooltip(Tooltip.create(Component.translatable("fireclient.module.scroll_click.scroll_type.tooltip")))
                .build());

        widgets.add(Button.builder(getClickTypeText(), this::clickTypeChanged)
                .bounds(base.width/2 + 10, base.height/2 + 20, 120, 20)
                .tooltip(Tooltip.create(Component.translatable("fireclient.module.scroll_click.click_type.tooltip")))
                .build());

        return widgets;
    }

    private void scrollModeChanged(Button button) {
        mode = (mode == ScrollMode.SINGLE) ? ScrollMode.DUAL : ScrollMode.SINGLE;
        reloadScreen();
    }

    private Component getScrollModeText() {
        return Component.translatable("fireclient.module.scroll_click.click_type.name", mode.getName());
    }

    private void clickTypeChanged(Button button) {
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

    private Component getClickTypeText() {
        switch(mode) {
            case SINGLE -> { return singleClickType.getName(); }
            case DUAL -> { return Component.translatable("fireclient.module.scroll_click.dual_click_type.direction", dualClickType.getName()); }
        }

        return Component.nullToEmpty("");
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

        public Component getName() {
            return Component.translatable("fireclient.module.scroll_click.click_type." + name.toLowerCase());
        }
    }

    public enum SingleClickType {
        USE("Use"),
        ATTACK("Attack");

        private final String name;

        SingleClickType(String name) {
            this.name = name;
        }

        public Component getName() {
            return Component.translatable("fireclient.module.scroll_click.single_click_type." + name.toLowerCase());
        }
    }

    public boolean isDisableWithPerspective() {
        return disableWithPerspective;
    }
}
