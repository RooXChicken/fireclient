package org.loveroo.fireclient.mixin;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Screen.class)
public interface AddWidgetAccessor {

    @Invoker("addDrawableChild")
    public <T extends Element & Drawable & Selectable> T addDrawableChildInvoker(T drawableElement);

    @Invoker("addSelectableChild")
    public <T extends Element & Selectable> T addSelectableChildInvoker(T child);
}
