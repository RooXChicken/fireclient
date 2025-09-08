package org.loveroo.fireclient.mixin.modules.nametag;

import java.util.UUID;

import org.loveroo.fireclient.modules.NametagModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.render.item.ItemRenderState;

@Mixin(ItemRenderState.class)
public abstract class StoreUUIDMixin implements NametagModule.UUIDStorage {

    @Unique
    private UUID uuid = UUID.randomUUID();

    @Override
    public UUID fireclient$getUUID() {
        return uuid;
    }

    @Override
    public void fireclient$setUUID(UUID uuid) {
        this.uuid = uuid;
    }
}

