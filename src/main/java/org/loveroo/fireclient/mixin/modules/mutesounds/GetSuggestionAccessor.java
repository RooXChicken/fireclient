package org.loveroo.fireclient.mixin.modules.mutesounds;

import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EditBox.class)
public interface GetSuggestionAccessor {

    @Accessor("suggestion")
    public String getSuggestion();
}
