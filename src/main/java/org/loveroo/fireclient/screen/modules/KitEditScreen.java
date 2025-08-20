package org.loveroo.fireclient.screen.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.KitManager;
import org.loveroo.fireclient.mixin.modules.AddSlotAccessor;
import org.loveroo.fireclient.mixin.modules.GetSlotAccessor;
import org.loveroo.fireclient.screen.config.ModuleConfigScreen;

public class KitEditScreen extends KitViewScreen {

    private final Identifier trashIcon = Identifier.of(FireClient.MOD_ID, "textures/gui/kit/trash_item.png");

    private boolean edited = false;
    private boolean saved = false;
    private final Slot trashSlot;

    private final int trashSlotX = 95;
    private final int trashSlotY = 62;

    public KitEditScreen(PlayerEntity player, PlayerInventory inventory, String kitName, boolean fromCommand) {
        super(player, inventory, "Edit", kitName, fromCommand);

        trashSlot = new Slot(inventory, 255, trashSlotX, trashSlotY);

        var accessor = (AddSlotAccessor)getScreenHandler();
        accessor.addSlotAccessed(trashSlot);
    }

    @Override
    protected void init() {
        super.init();

        var saveButton = ButtonWidget.builder(Text.of("\uD83D\uDCBE Save"), this::saveButtonPressed)
                .dimensions(width/2 + 10, height/2 + 90,80, 20)
                .build();

        addDrawableChild(saveButton);

        var undoButton = ButtonWidget.builder(Text.of("↶ Undo"), this::undoButtonPressed)
                .dimensions(width/2 - 90, height/2 + 90,80, 20)
                .build();

        addDrawableChild(undoButton);
    }

    private void saveButtonPressed(ButtonWidget button) {
        var deleteStatus = KitManager.deleteKit(kitName);

        switch(deleteStatus) {
            case SUCCESS -> { }

            case FAILURE -> {
                RooHelper.sendNotification("Failed to recycle \"" + kitName + "\"", "The kit will not be modified");
                return;
            }
        }

        var createStatus = KitManager.createKit(kitName, KitManager.getInventoryAsString(getInventory()));

        switch(createStatus) {
            case SUCCESS -> { RooHelper.sendNotification("Successfully saved \"" + kitName + "\"", "The old kit was recycled"); }

            case INVALID_KIT -> { RooHelper.sendNotification("Failed to save \"" + kitName + "\"", "Invalid editor inventory"); }
            case ALREADY_EXISTS -> { RooHelper.sendNotification("Failed to save \"" + kitName + "\"", "Kit name already exists"); }
            case WRITE_FAIL -> { RooHelper.sendNotification("Failed to save \"" + kitName + "\"", "Failed to save kit"); }
        }

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

//    @Override
//    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
//        if(actionType == SlotActionType.THROW && slot != null && slot.hasStack()) {
//            client.getNetworkHandler().sendPacket(new CreativeInventoryActionC2SPacket(-1, slot.getStack()));
//        }
//
//        super.onMouseClick(slot, slotId, button, actionType);
//    }
//
//    @Override
//    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
//        return super.isClickOutsideBounds();
//    }


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

        context.drawTexture(RenderLayer::getGuiTextured, trashIcon, x + trashSlotX - 1, y + trashSlotY - 1, 0, 0, 18, 18, 18, 18, 0xFFFFFFFF);
    }

    @Override
    protected void onClose() {
        if(edited && !saved) {
            RooHelper.sendNotification("Successfully reverted \"" + kitName + "\"", "No changes were made");
        }
    }
}
