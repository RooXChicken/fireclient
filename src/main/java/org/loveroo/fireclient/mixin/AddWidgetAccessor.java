package org.loveroo.fireclient.mixin;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Screen.class)
public interface AddWidgetAccessor {

    @Invoker("addRenderableWidget")
    public <T extends GuiEventListener & Renderable & NarratableEntry> T addDrawableChildInvoker(T drawableElement);

    @Invoker("addWidget")
    public <T extends GuiEventListener & NarratableEntry> T addSelectableChildInvoker(T child);
}
