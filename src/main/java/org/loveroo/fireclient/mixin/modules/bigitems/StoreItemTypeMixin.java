package org.loveroo.fireclient.mixin.modules.bigitems;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.loveroo.fireclient.modules.BigItemsModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemStackRenderState.class)
public abstract class StoreItemTypeMixin implements BigItemsModule.ItemTypeStorage {

    @Unique
    private Item item = Items.AIR;

    @Override
    public Item fireclient$getItem() {
        return item;
    }

    @Override
    public void fireclient$setItem(Item type) {
        item = type;
    }
}

