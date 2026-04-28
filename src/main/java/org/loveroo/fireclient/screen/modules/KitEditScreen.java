package org.loveroo.fireclient.screen.modules;

import net.minecraft.world.inventory.ContainerInput;
import org.jspecify.annotations.NonNull;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.KitManager;
import org.loveroo.fireclient.mixin.modules.kit.AddSlotAccessor;
import org.loveroo.fireclient.mixin.modules.kit.GetSlotAccessor;
import org.loveroo.fireclient.screen.config.ModuleConfigScreen;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class KitEditScreen extends KitViewScreen {

    private final Identifier trashIcon = Identifier.fromNamespaceAndPath(FireClient.MOD_ID, "textures/gui/kit/trash_item.png");

    private boolean edited = false;
    private boolean saved = false;
    private final Slot trashSlot;

    private final int trashSlotX = 95;
    private final int trashSlotY = 62;

    public KitEditScreen(Player player, Inventory inventory, String kitName, boolean fromCommand) {
        super(player, inventory, Component.translatable("fireclient.screen.edit_kit.title", kitName), kitName, fromCommand);

        trashSlot = new Slot(inventory, 255, trashSlotX, trashSlotY);

        var accessor = (AddSlotAccessor) getMenu();
        accessor.addSlotAccessed(trashSlot);
    }

    @Override
    protected void init() {
        super.init();

        var saveButton = Button.builder(Component.translatable("fireclient.screen.edit_kit.save.name"), this::saveButtonPressed)
            .tooltip(Tooltip.create(Component.translatable("fireclient.screen.edit_kit.save.tooltip", kitName)))
            .bounds(width/2 + 10, height/2 + 90,80, 20)
            .build();

        addRenderableWidget(saveButton);

        var undoButton = Button.builder(Component.translatable("fireclient.screen.edit_kit.undo.name"), this::undoButtonPressed)
            .tooltip(Tooltip.create(Component.translatable("fireclient.screen.edit_kit.undo.tooltip", kitName)))
            .bounds(width/2 - 90, height/2 + 90,80, 20)
            .build();

        addRenderableWidget(undoButton);
    }

    private void saveButtonPressed(Button button) {
        var deleteStatus = KitManager.deleteKit(kitName);

        if(!handleDeleteStatus(deleteStatus)) {
            return;
        }

        var createStatus = KitManager.createKit(kitName, KitManager.getInventoryAsString(getInventory()));
        handleCreateStatus(createStatus);

        saved = true;

        if(isFromCommand()) {
            onClose();
            return;
        }

        var kit = FireClientside.getModule("kit");
        if(kit == null) {
            return;
        }

        Minecraft.getInstance().setScreen(new ModuleConfigScreen(kit));
    }

    private boolean handleDeleteStatus(KitManager.KitManageStatus status) {
        MutableComponent title = null;
        MutableComponent contents = null;

        switch(status) {
            case SUCCESS -> { }

            case FAILURE -> {
                title = Component.translatable("fireclient.module.kit.recycle.failure.title", kitName);
                contents = Component.translatable("fireclient.module.kit.recycle.failure.contents");
            }
        }

        if(title == null) {
            return true;
        }

        if(isFromCommand()) {
            if(minecraft == null || minecraft.player == null) {
                return false;
            }

            minecraft.player.sendSystemMessage(title.append(" ").append(contents));
        }
        else {
            RooHelper.sendNotification(title, contents);
        }

        return false;
    }

    private boolean handleCreateStatus(KitManager.KitCreateStatus status) {
        MutableComponent title = null;
        MutableComponent contents = null;

        switch(status) {
            case SUCCESS -> {
                    title = Component.translatable("fireclient.module.kit.edit.success.name", kitName);
                    contents = Component.translatable("fireclient.module.kit.edit.success.contents");
            }

            case INVALID_KIT -> {
                    title = Component.translatable("fireclient.module.kit.edit.failure.name", kitName);
                    contents = Component.translatable("fireclient.module.kit.edit.invalid_editor_inventory");
            }

            case ALREADY_EXISTS -> {
                    title = Component.translatable("fireclient.module.kit.edit.failure.name", kitName);
                    contents = Component.translatable("fireclient.module.kit.generic.already_exists.contents");
            }

            case WRITE_FAIL -> {
                    title = Component.translatable("fireclient.module.kit.edit.failure.name", kitName);
                    contents = Component.translatable("fireclient.module.kit.generic.write_failure.contents");
            }
        }

        if(title == null) {
            return true;
        }

        if(isFromCommand()) {
            if(minecraft == null || minecraft.player == null) {
                return false;
            }

            minecraft.player.sendSystemMessage(title.append(" ").append(contents));
        }
        else {
            RooHelper.sendNotification(title, contents);
        }

        return false;
    }

    private void undoButtonPressed(Button button) {
        if(!isFromCommand()) {
            exitToKit();
        }
        else {
            onClose();
        }
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent click, boolean doubled) {
        if(!edited) {
            var slot = ((GetSlotAccessor)this).getSlotAtAccessed(click.x(), click.y());
            if(slot != null) {
                edited = true;
            }
        }

        if(preventCrafting(click.x(), click.y())) {
            return true;
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) {
        if(preventCrafting(click.x(), click.y())) {
            return true;
        }

        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
        if(preventCrafting(click.x(), click.y())) {
            return true;
        }

        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if(!edited && (input.key() != GLFW.GLFW_KEY_ESCAPE && !minecraft.options.keyInventory.matches(input))) {
            edited = true;
        }

        return super.keyPressed(input);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        if(trashSlot.hasItem() && !trashSlot.getItem().isEmpty()) {
            trashSlot.setByPlayer(ItemStack.EMPTY);
        }

        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    @Override
    protected boolean checkHotbarKeyPressed(@NonNull KeyEvent input) {
        if(!menu.getCarried().isEmpty() || hoveredSlot == null) {
            return false;
        }

        if(minecraft.options.keySwapOffhand.matches(input)) {
            slotClicked(hoveredSlot, hoveredSlot.index, 0, ContainerInput.PICKUP);

            var offhandSlot = getMenu().slots.get(InventoryMenu.SHIELD_SLOT);
            slotClicked(offhandSlot, offhandSlot.index, 0, ContainerInput.PICKUP);
            slotClicked(hoveredSlot, hoveredSlot.index, 0, ContainerInput.PICKUP);

            return true;
        }

        for(int i = 0; i < 9; i++) {
            if(minecraft.options.keyHotbarSlots[i].matches(input)) {
                slotClicked(hoveredSlot, hoveredSlot.index, 0, ContainerInput.PICKUP);

                var hotbarSlot = getMenu().slots.get(InventoryMenu.USE_ROW_SLOT_START + i);
                slotClicked(hotbarSlot, hotbarSlot.index, 0, ContainerInput.PICKUP);
                slotClicked(hoveredSlot, hoveredSlot.index, 0, ContainerInput.PICKUP);

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

        return (slot.container instanceof CraftingContainer);
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractBackground(graphics, mouseX, mouseY, delta);

        graphics.blit(RenderPipelines.GUI_TEXTURED, trashIcon, leftPos + trashSlotX - 1, topPos + trashSlotY - 1, 0, 0, 18, 18, 18, 18, 0xFFFFFFFF);
    }

    @Override
    public void onClose() {
        if(edited && !saved) {
            var title = Component.translatable("fireclient.module.kit.edit.revert.name", kitName);
            var contents = Component.translatable("fireclient.module.kit.edit.revert.contents");

            if(isFromCommand()) {
                if(minecraft == null || minecraft.player == null) {
                    return;
                }

                minecraft.player.sendSystemMessage(title.append(" ").append(contents));
            }
            else {
                RooHelper.sendNotification(title, contents);
            }
        }
    }
}
