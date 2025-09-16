package org.loveroo.fireclient.mixin.modules.bigitems;

import org.loveroo.fireclient.modules.BigItemsModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

@Mixin(ItemRenderer.class)
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

