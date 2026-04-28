package org.loveroo.fireclient.modules;

import java.util.ArrayList;
import java.util.List;

import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.JsonOption;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;
import org.loveroo.fireclient.screen.widgets.ToggleButtonWidget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.DeltaTracker;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

public class ArmorDisplayModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0xAAF089);

    @JsonOption(name = "locked")
    private boolean locked = true;

    @JsonOption(name = "mode")
    private DisplayMode mode = DisplayMode.TEXT;

    @JsonOption(name = "show_items")
    private boolean showItems = false;

    private final double flashThreshold = 2.0/14.0;
    private int ticks = 0;
    private int flashColor = 0xFFFF5656;

    public ArmorDisplayModule() {
        super(new ModuleData("armor_display", "\uD83D\uDEE1", color));

        getData().setWidth(20);
        getData().setHeight(40);
        getData().setScale(2.0/3.0);

        getData().setSnapScale(1.0/3.0);

        getData().setVisible(true);

        var toggleBind = new Keybind("toggle_armor_display",
                Component.translatable("fireclient.keybind.generic.toggle.name"),
                Component.translatable("fireclient.keybind.generic.toggle_visibility.description", getData().getShownName()),
                true, null,
                () -> getData().setVisible(!getData().isVisible()), null);

        FireClientside.getKeybindManager().registerKeybind(toggleBind);
    }

    @Override
    public void update(Minecraft client) {
        if(++ticks % 8 == 0) {
            ticks = 0;

            if(flashColor == 0xFFFFFFFF) {
                flashColor = 0xFFFF5656;
            }
            else {
                flashColor = 0xFFFFFFFF;
            }
        }

        refreshPosition();
    }

    private void refreshPosition() {
        var client = Minecraft.getInstance();

        switch(mode) {
            case TEXT -> {
                getData().setWidth((!showItems) ? 20 : 30);
                getData().setHeight(38);

                if(locked) {
                    getData().setPosX((int)(client.getWindow().getGuiScaledWidth()/2.0 - (10 * getData().getScale()) - ((!showItems) ? 0 : 3)));
                    getData().setPosY((int)(client.getWindow().getGuiScaledHeight() - 48.0 - (22 * getData().getScale())));
                    getData().setScale(2.0/3.0);
                }
            }

            case BARS -> {
                getData().setWidth(14);
                getData().setHeight(11);

                if(locked) {
                    getData().setPosX((int)(client.getWindow().getGuiScaledWidth()/2.0 - (10 * getData().getScale()) + 3));
                    getData().setPosY((int)(client.getWindow().getGuiScaledHeight() - 26.0 - (22 * getData().getScale())));
                    getData().setScale(1.0);
                }
            }
        }
    }

    @Override
    public void draw(GuiGraphicsExtractor graphics, DeltaTracker ticks) {
        if(!canDraw()) {
            return;
        }

        var client = Minecraft.getInstance();
        if(client.player == null) {
            return;
        }
        
        transform(graphics.pose());

        var items = new ArrayList<ItemStack>();

        items.add(client.player.getItemBySlot(EquipmentSlot.HEAD));
        items.add(client.player.getItemBySlot(EquipmentSlot.CHEST));
        items.add(client.player.getItemBySlot(EquipmentSlot.LEGS));
        items.add(client.player.getItemBySlot(EquipmentSlot.FEET));

        for(var i = 0; i < 4; i++) {
            var item = items.get(i);

            if(item != ItemStack.EMPTY && item.getMaxDamage() > 0) {
                var progress = client.player.getCooldowns().getCooldownPercent(item, ticks.getGameTimeDeltaPartialTick(true));
                var cooldown = (int)Math.ceil(progress * 10);

                switch(mode) {
                    case TEXT -> drawArmorText(graphics, item, i, cooldown);
                    case BARS -> drawArmorBars(graphics, item, i, cooldown);
                }
            }
        }

        endTransform(graphics.pose());
    }

    private void drawArmorText(GuiGraphicsExtractor context, ItemStack item, int index, int cooldown) {
        var client = Minecraft.getInstance();
        var text = client.font;

        var x = (showItems) ? 20 : 10;
        var y = 10*index;
        if(cooldown > 0) {
            context.fill(x-10, y + 9 - cooldown, x+10, y+9, 0x809F9F9F);
        }

        context.centeredText(text, String.valueOf(item.getMaxDamage() - item.getDamageValue()), x, y, getColor(item));

        if(showItems) {
            var matrix = context.pose();
            matrix.pushMatrix();

            matrix.scale(0.6f, 0.6f);
            context.item(client.player, item, 0, (int)(y*1.6f), index);

            matrix.popMatrix();
        }
    }

    private void drawArmorBars(GuiGraphicsExtractor context, ItemStack item, int index, int cooldown) {
        var y = 3*index;
        context.fill(0, y+2, 14, y, 0xFF000000);
        
        if(cooldown > 0) {
            final double cooldownMult = (14.0/10.0);
            context.fill(0, y+2, (int)Math.ceil(cooldown*cooldownMult), y+1, 0xFF9F9F9F);
        }

        var ratio = ((item.getMaxDamage()-item.getDamageValue()) / (double)item.getMaxDamage());
        context.fill(0, y+1, (int)Math.ceil(ratio*14), y, getColor(item));
    }

    private int getColor(ItemStack item) {
        var ratio = ((item.getMaxDamage()-item.getDamageValue()) / (double)item.getMaxDamage());
        if(ratio == 1.0) {
            return 0xFF099A00;
        }
        else if(ratio < flashThreshold) {
            return flashColor;
        }

        return 0xFF000000 + item.getBarColor();
    }

    @Override
    public void handleTransformation(int mouseState, OldTransform old, int mouseX, int mouseY, int oldMouseX, int oldMouseY, boolean snap) {
        if(locked) {
            return;
        }

        super.handleTransformation(mouseState, old, mouseX, mouseY, oldMouseX, oldMouseY, snap);
    }

    @Override
    public List<AbstractWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<AbstractWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("toggle_armor_display").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleVisibleButton(base.width/2 - 60, base.height/2 - 10));

        widgets.add(new Button.Builder(Component.translatable("fireclient.module.armor_display.mode_toggle.name", mode.getName()), this::modeButtonPressed)
            .bounds(base.width/2 - 60, base.height/2 + 50, 120, 20)
            .tooltip(Tooltip.create(Component.translatable("fireclient.module.armor_display.mode_toggle.tooltip")))
            .build());

        widgets.add(new ToggleButtonWidget.ToggleButtonBuilder(Component.translatable("fireclient.module.armor_display.lock_button.name"))
            .getValue(() -> { return locked; })
            .setValue((value) -> { locked = value; })
            .position(base.width/2 - 60, base.height/2 + 20)
            .tooltip(Tooltip.create(Component.translatable("fireclient.module.armor_display.lock_button.tooltip")))
            .onChange(() -> {
                if(locked) {
                    getData().setScale(2.0/3.0);
                }
            })
            .build());
        
        widgets.add(new ToggleButtonWidget.ToggleButtonBuilder(Component.translatable("fireclient.module.armor_display.show_items.name"))
            .getValue(() -> { return showItems; })
            .setValue((value) -> { showItems = value; })
            .position(base.width/2 - 60, base.height/2 + 80)
            .tooltip(Tooltip.create(Component.translatable("fireclient.module.armor_display.show_items.tooltip")))
            .build());

        return widgets;
    }

    private void modeButtonPressed(Button button) {
        mode = switch(mode) {
            case TEXT -> DisplayMode.BARS;
            case BARS -> DisplayMode.TEXT;
        };

        button.setMessage(Component.translatable("fireclient.module.armor_display.mode_toggle.name", mode.getName()));
        refreshPosition();
    }

    public enum DisplayMode {

        TEXT(Component.translatable("fireclient.module.armor_display.mode.text")),
        BARS(Component.translatable("fireclient.module.armor_display.mode.bars"));

        private final Component name;

        private DisplayMode(Component name) {
            this.name = name;
        }

        public Component getName() {
            return name;
        }
    } 
}
