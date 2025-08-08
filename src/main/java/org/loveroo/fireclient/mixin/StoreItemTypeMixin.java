package org.loveroo.fireclient.mixin;

import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.loveroo.fireclient.data.ItemTypeStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemRenderState.class)
public abstract class StoreItemTypeMixin implements ItemTypeStorage {

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

