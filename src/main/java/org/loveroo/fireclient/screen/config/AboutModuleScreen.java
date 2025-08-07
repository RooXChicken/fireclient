package org.loveroo.fireclient.screen.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.loveroo.fireclient.modules.ModuleBase;

public class AboutModuleScreen extends ConfigScreenBase {

    private ModuleBase module;

    public AboutModuleScreen(ModuleBase module) {
        super(Text.of("About " + module.getData().getName()));
        this.module = module;
    }

    @Override
    protected boolean escapePressed() {
        MinecraftClient.getInstance().setScreen(new ModuleConfigScreen(module));
        return true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        var text = MinecraftClient.getInstance().textRenderer;
        context.drawCenteredTextWithShadow(text, Text.literal(module.getData().getDescription()), width/2, -10, 0xFFFFFFFF);
    }
}
