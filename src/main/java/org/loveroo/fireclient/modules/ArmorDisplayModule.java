package org.loveroo.fireclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.JsonOption;
import org.loveroo.fireclient.data.ModuleData;
import org.loveroo.fireclient.keybind.Keybind;
import org.loveroo.fireclient.screen.widgets.ToggleButtonWidget;

import java.util.ArrayList;
import java.util.List;

public class ArmorDisplayModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0xAAF089);

    @JsonOption(name = "locked")
    private boolean locked = true;

    @JsonOption(name = "mode")
    private DisplayMode mode = DisplayMode.TEXT;

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
                Text.translatable("fireclient.keybind.generic.toggle.name"),
                Text.translatable("fireclient.keybind.generic.toggle_visibility.description", getData().getShownName()),
                true, null,
                () -> getData().setVisible(!getData().isVisible()), null);

        FireClientside.getKeybindManager().registerKeybind(toggleBind);
    }

    @Override
    public void update(MinecraftClient client) {
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
        var client = MinecraftClient.getInstance();

        switch(mode) {
            case TEXT -> {
                getData().setWidth(20);
                getData().setHeight(38);
            }

            case BARS -> {
                getData().setWidth(14);
                getData().setHeight(11);
            }
        }
        
        if(locked) {
            switch(mode) {
                case TEXT -> {
                    getData().setPosX((int)(client.getWindow().getScaledWidth()/2.0 - (10 * getData().getScale())));
                    getData().setPosY((int)(client.getWindow().getScaledHeight() - 48.0 - (22 * getData().getScale())));
                    getData().setScale(2.0/3.0);
                }

                case BARS -> {
                    getData().setPosX((int)(client.getWindow().getScaledWidth()/2.0 - (10 * getData().getScale()) + 3));
                    getData().setPosY((int)(client.getWindow().getScaledHeight() - 26.0 - (22 * getData().getScale())));
                    getData().setScale(1.0);
                }
            }
        }
    }

    @Override
    public void draw(DrawContext context, RenderTickCounter ticks) {
        if(!canDraw()) {
            return;
        }

        transform(context.getMatrices());

        var client = MinecraftClient.getInstance();
        var items = new ArrayList<ItemStack>();

        items.add(client.player.getEquippedStack(EquipmentSlot.HEAD));
        items.add(client.player.getEquippedStack(EquipmentSlot.CHEST));
        items.add(client.player.getEquippedStack(EquipmentSlot.LEGS));
        items.add(client.player.getEquippedStack(EquipmentSlot.FEET));

        for(var i = 0; i < 4; i++) {
            var item = items.get(i);

            if(item != ItemStack.EMPTY && item.getMaxDamage() > 0) {
                var progress = client.player.getItemCooldownManager().getCooldownProgress(item, ticks.getTickDelta(true));
                var cooldown = (int)Math.ceil(progress * 10);

                switch(mode) {
                    case TEXT -> drawArmorText(context, item, i, cooldown);
                    case BARS -> drawArmorBars(context, item, i, cooldown);
                }
            }
        }

        endTransform(context.getMatrices());
    }

    private void drawArmorText(DrawContext context, ItemStack item, int index, int cooldown) {
        var client = MinecraftClient.getInstance();
        var text = client.textRenderer;

        var y = 10*index;
        if(cooldown > 0) {
            context.fill(0, y + 9 - cooldown, 20, y+9, 0x809F9F9F);
        }

        context.drawCenteredTextWithShadow(text, (item.getMaxDamage() - item.getDamage()) + "", 10, y, getColor(item));
    }

    private void drawArmorBars(DrawContext context, ItemStack item, int index, int cooldown) {
        var y = 3*index;
        context.fill(0, y+2, 14, y, 0xFF000000);
        
        if(cooldown > 0) {
            final double cooldownMult = (14.0/10.0);
            context.fill(0, y+2, (int)Math.ceil(cooldown*cooldownMult), y+1, 0xFF9F9F9F);
        }

        var ratio = ((item.getMaxDamage()-item.getDamage()) / (double)item.getMaxDamage());
        context.fill(0, y+1, (int)Math.ceil(ratio*14), y, getColor(item));
    }

    private int getColor(ItemStack item) {
        var ratio = ((item.getMaxDamage()-item.getDamage()) / (double)item.getMaxDamage());
        if(ratio == 1.0) {
            return 0xFF099A00;
        }
        else if(ratio < flashThreshold) {
            return flashColor;
        }

        return 0xFF000000 + item.getItemBarColor();
    }

    @Override
    public void handleTransformation(int mouseState, OldTransform old, int mouseX, int mouseY, int oldMouseX, int oldMouseY, boolean snap) {
        if(locked) {
            return;
        }

        super.handleTransformation(mouseState, old, mouseX, mouseY, oldMouseX, oldMouseY, snap);
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(FireClientside.getKeybindManager().getKeybind("toggle_armor_display").getRebindButton(5, base.height - 25, 120,20));
        widgets.add(getToggleVisibleButton(base.width/2 - 60, base.height/2 - 10));

        widgets.add(new ButtonWidget.Builder(Text.translatable("fireclient.module.armor_display.mode_toggle.name", mode.getName()), this::modeButtonPressed)
            .dimensions(base.width/2 - 60, base.height/2 + 50, 120, 20)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.armor_display.mode_toggle.tooltip")))
            .build());

        widgets.add(new ToggleButtonWidget.ToggleButtonBuilder(Text.translatable("fireclient.module.armor_display.lock_button.name"))
            .getValue(() -> { return locked; })
            .setValue((value) -> { locked = value; })
            .position(base.width/2 - 60, base.height/2 + 20)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.armor_display.lock_button.tooltip")))
            .onChange(() -> {
                if(locked) {
                    getData().setScale(2.0/3.0);
                }
            })
            .build());

        return widgets;
    }

    private void modeButtonPressed(ButtonWidget button) {
        mode = switch(mode) {
            case TEXT -> DisplayMode.BARS;
            case BARS -> DisplayMode.TEXT;
        };

        button.setMessage(Text.translatable("fireclient.module.armor_display.mode_toggle.name", mode.getName()));
        refreshPosition();
    }

    public enum DisplayMode {

        TEXT(Text.translatable("fireclient.module.armor_display.mode.text")),
        BARS(Text.translatable("fireclient.module.armor_display.mode.bars"));

        private final Text name;

        private DisplayMode(Text name) {
            this.name = name;
        }

        public Text getName() {
            return name;
        }
    } 
}
