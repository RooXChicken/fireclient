package org.loveroo.fireclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
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
import org.loveroo.fireclient.screen.widgets.ToggleButtonBuilder;

import java.util.ArrayList;
import java.util.List;

public class ArmorDisplayModule extends ModuleBase {

    private static final Color color = Color.fromRGB(0xAAF089);

    private Identifier cooldownTexture = Identifier.of(FireClient.MOD_ID, "textures/armor_display/cooldown.png");

    @JsonOption(name = "locked")
    private boolean locked = true;

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

        if(locked) {
            getData().setPosX((int)(client.getWindow().getScaledWidth()/2.0 - (10 * getData().getScale())));
            getData().setPosY((int)(client.getWindow().getScaledHeight() - 50.0 - (22 * getData().getScale())));
        }
    }

    @Override
    public void draw(DrawContext context, RenderTickCounter ticks) {
        if(!canDraw()) {
            return;
        }

        transform(context.getMatrices());

        var client = MinecraftClient.getInstance();
        var text = client.textRenderer;

        var items = new ArrayList<ItemStack>();
        items.add(client.player.getEquippedStack(EquipmentSlot.HEAD));
        items.add(client.player.getEquippedStack(EquipmentSlot.CHEST));
        items.add(client.player.getEquippedStack(EquipmentSlot.LEGS));
        items.add(client.player.getEquippedStack(EquipmentSlot.FEET));

        var offset = 10;
        for(var i = 0; i < 4; i++) {
            var item = items.get(i);

            if(item != ItemStack.EMPTY) {
                var progress = client.player.getItemCooldownManager().getCooldownProgress(item, ticks.getTickProgress(true));
                var cooldown = (int)Math.ceil(progress * 10);

                // TODO: method is named fill :3
                context.drawTexture(RenderPipelines.GUI_TEXTURED, cooldownTexture, 0, offset*i + 2 - cooldown + 9, 0, 0, 20, cooldown, 20, cooldown);
                context.drawCenteredTextWithShadow(text, (item.getMaxDamage() - item.getDamage()) + "", 10, offset*i + 2, getColor(item));
            }
        }

        endTransform(context.getMatrices());
    }

    private int getColor(ItemStack item) {
        var percentage = (item.getMaxDamage() - item.getDamage() + 0.0) / item.getMaxDamage();

        var color = 0xFF2BEE00;

        if(item.getDamage() == 0) {
            color = 0xFF099A00;
        }
        if(percentage <= 0.7) {
            color = 0xFFFF8022;
        }
        if(percentage <= 0.5) {
            color = 0xFFFFFC36;
        }
        if(percentage <= 0.2) {
            color = 0xFFD50000;
        }
        if(percentage <= 0.1) {
            color = 0xFF970000;
        }
        if(percentage <= 0.05) {
            color = flashColor;
        }

        return color;
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
        widgets.add(getToggleVisibleButton(base.width/2 - 60, base.height/2 - 20));

        widgets.add(new ToggleButtonBuilder(Text.translatable("fireclient.module.armor_display.lock_button.name"))
            .getValue(() -> { return locked; })
            .setValue((value) -> { locked = value; })
            .position(base.width/2 - 60, base.height/2 + 10)
            .tooltip(Tooltip.of(Text.translatable("fireclient.module.armor_display.lock_button.tooltip")))
            .onChange(() -> {
                if(locked) {
                    getData().setScale(2.0/3.0);
                }
            })
            .build());

        return widgets;
    }
}
