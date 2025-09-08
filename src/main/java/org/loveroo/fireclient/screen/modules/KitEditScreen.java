package org.loveroo.fireclient.screen.modules;

import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.KitManager;
import org.loveroo.fireclient.mixin.modules.kit.AddSlotAccessor;
import org.loveroo.fireclient.mixin.modules.kit.GetSlotAccessor;
import org.loveroo.fireclient.screen.config.ModuleConfigScreen;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class KitEditScreen extends KitViewScreen {

    private final Identifier trashIcon = Identifier.of(FireClient.MOD_ID, "textures/gui/kit/trash_item.png");

    private boolean edited = false;
    private boolean saved = false;
    private final Slot trashSlot;

    private final int trashSlotX = 95;
    private final int trashSlotY = 62;

    public KitEditScreen(PlayerEntity player, PlayerInventory inventory, String kitName, boolean fromCommand) {
        super(player, inventory, Text.translatable("fireclient.screen.edit_kit.title", kitName), kitName, fromCommand);

        trashSlot = new Slot(inventory, 255, trashSlotX, trashSlotY);

        var accessor = (AddSlotAccessor)getScreenHandler();
        accessor.addSlotAccessed(trashSlot);
    }

    @Override
    protected void init() {
        super.init();

        var saveButton = ButtonWidget.builder(Text.translatable("fireclient.screen.edit_kit.save.name"), this::saveButtonPressed)
            .tooltip(Tooltip.of(Text.translatable("fireclient.screen.edit_kit.save.tooltip", kitName)))
            .dimensions(width/2 + 10, height/2 + 90,80, 20)
            .build();

        addDrawableChild(saveButton);

        var undoButton = ButtonWidget.builder(Text.translatable("fireclient.screen.edit_kit.undo.name"), this::undoButtonPressed)
            .tooltip(Tooltip.of(Text.translatable("fireclient.screen.edit_kit.undo.tooltip", kitName)))
            .dimensions(width/2 - 90, height/2 + 90,80, 20)
            .build();

        addDrawableChild(undoButton);
    }

    private void saveButtonPressed(ButtonWidget button) {
        var deleteStatus = KitManager.deleteKit(kitName);

        if(!handleDeleteStatus(deleteStatus)) {
            return;
        }

        var createStatus = KitManager.createKit(kitName, KitManager.getInventoryAsString(getInventory()));
        handleCreateStatus(createStatus);

        saved = true;

        if(isFromCommand()) {
            close();
            return;
        }

        var kit = FireClientside.getModule("kit");
        if(kit == null) {
            return;
        }

        MinecraftClient.getInstance().setScreen(new ModuleConfigScreen(kit));
    }

    private boolean handleDeleteStatus(KitManager.KitManageStatus status) {
        MutableText title = null;
        MutableText contents = null;

        switch(status) {
            case SUCCESS -> { }

            case FAILURE -> {
                title = Text.translatable("fireclient.module.kit.recycle.failure.title", kitName);
                contents = Text.translatable("fireclient.module.kit.recycle.failure.contents");
            }
        }

        if(title == null) {
            return true;
        }

        if(isFromCommand()) {
            if(client == null || client.player == null) {
                return false;
            }

            client.player.sendMessage(title.append(" ").append(contents), false);
        }
        else {
            RooHelper.sendNotification(title, contents);
        }

        return false;
    }

    private boolean handleCreateStatus(KitManager.KitCreateStatus status) {
        MutableText title = null;
        MutableText contents = null;

        switch(status) {
            case SUCCESS -> {
                    title = Text.translatable("fireclient.module.kit.edit.success.name", kitName);
                    contents = Text.translatable("fireclient.module.kit.edit.success.contents");
            }

            case INVALID_KIT -> {
                    title = Text.translatable("fireclient.module.kit.edit.failure.name", kitName);
                    contents = Text.translatable("fireclient.module.kit.edit.invalid_editor_inventory");
            }

            case ALREADY_EXISTS -> {
                    title = Text.translatable("fireclient.module.kit.edit.failure.name", kitName);
                    contents = Text.translatable("fireclient.module.kit.generic.already_exists.contents");
            }

            case WRITE_FAIL -> {
                    title = Text.translatable("fireclient.module.kit.edit.failure.name", kitName);
                    contents = Text.translatable("fireclient.module.kit.generic.write_failure.contents");
            }
        }

        if(title == null) {
            return true;
        }

        if(isFromCommand()) {
            if(client == null || client.player == null) {
                return false;
            }

            client.player.sendMessage(title.append(" ").append(contents), false);
        }
        else {
            RooHelper.sendNotification(title, contents);
        }

        return false;
    }

    private void undoButtonPressed(ButtonWidget button) {
        if(!isFromCommand()) {
            exitToKit();
        }
        else {
            close();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(!edited) {
            var slot = ((GetSlotAccessor)this).getSlotAtAccessed(mouseX, mouseY);
            if(slot != null) {
                edited = true;
            }
        }

        if(preventCrafting(mouseX, mouseY)) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(preventCrafting(mouseX, mouseY)) {
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(preventCrafting(mouseX, mouseY)) {
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(!edited && (keyCode != GLFW.GLFW_KEY_ESCAPE && !client.options.inventoryKey.matchesKey(keyCode, scanCode))) {
            edited = true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if(trashSlot.hasStack() && !trashSlot.getStack().isEmpty()) {
            trashSlot.setStack(ItemStack.EMPTY);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected boolean handleHotbarKeyPressed(int keyCode, int scanCode) {
        if(!handler.getCursorStack().isEmpty() || focusedSlot == null) {
            return false;
        }

        if(client.options.swapHandsKey.matchesKey(keyCode, scanCode)) {
            onMouseClick(focusedSlot, focusedSlot.id, 0, SlotActionType.PICKUP);

            var offhandSlot = getScreenHandler().slots.get(PlayerScreenHandler.OFFHAND_ID);
            onMouseClick(offhandSlot, offhandSlot.id, 0, SlotActionType.PICKUP);
            onMouseClick(focusedSlot, focusedSlot.id, 0, SlotActionType.PICKUP);

            return true;
        }

        for(int i = 0; i < 9; i++) {
            if(client.options.hotbarKeys[i].matchesKey(keyCode, scanCode)) {
                onMouseClick(focusedSlot, focusedSlot.id, 0, SlotActionType.PICKUP);

                var hotbarSlot = getScreenHandler().slots.get(PlayerScreenHandler.HOTBAR_START + i);
                onMouseClick(hotbarSlot, hotbarSlot.id, 0, SlotActionType.PICKUP);
                onMouseClick(focusedSlot, focusedSlot.id, 0, SlotActionType.PICKUP);

                return true;
            }
        }

        return false;
    }

    private boolean preventCrafting(double mouseX, double mouseY) {
        var slot = ((GetSlotAccessor)this).getSlotAtAccessed(mouseX, mouseY);
        if(slot == null) {
            return false;
        }

        return (slot.inventory instanceof RecipeInputInventory);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);

        context.drawTexture(RenderPipelines.GUI_TEXTURED, trashIcon, x + trashSlotX - 1, y + trashSlotY - 1, 0, 0, 18, 18, 18, 18, 0xFFFFFFFF);
    }

    @Override
    protected void onClose() {
        if(edited && !saved) {
            var title = Text.translatable("fireclient.module.kit.edit.revert.name", kitName);
            var contents = Text.translatable("fireclient.module.kit.edit.revert.contents");

            if(isFromCommand()) {
                if(client == null || client.player == null) {
                    return;
                }

                client.player.sendMessage(title.append(" ").append(contents), false);
            }
            else {
                RooHelper.sendNotification(title, contents);
            }
        }
    }
}
