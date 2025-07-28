package org.loveroo.fireclient.screen.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.modules.ModuleBase;

import java.util.ArrayList;

public class ModuleSelectScreen extends ConfigScreenBase {

    private ArrayList<ButtonWidget> moduleButtons;
    private ButtonWidget backButton;

    public ModuleSelectScreen() {
        super(Text.of("FireClient Module Options"));
    }

    @Override
    public void init() {
        moduleButtons = new ArrayList<>();

        var skips = 0;
        for(var i = 0; i < FireClientside.getModules().size(); i++) {
            var module = FireClientside.getModules().get(i);

            if(module.getData().isSkip()) {
                skips++;
                continue;
            }

            var index = i - skips;

            var x = ((index % 3) - 1) * 110;
            var y = (index / 3) * 30;

            moduleButtons.add(ButtonWidget.builder(Text.of(module.getData().getName()), module::moduleConfigPressed)
                    .dimensions(width/2 - 50 + x, height/2 - 20 + y, 100, 20)
                    .build());

            addSelectableChild(moduleButtons.get(index));
        }

        backButton = ButtonWidget.builder(Text.of("Back"), this::backButtonPressed)
                .dimensions(width/2 - 60, height/2 + - 20 +  (FireClientside.getModules().size() / 3 + 1) * 30, 120, 20)
                .build();

        addSelectableChild(backButton);
    }

    private void backButtonPressed(ButtonWidget button) {
        MinecraftClient.getInstance().setScreen(new MainConfigScreen());
    }

    @Override
    protected boolean escapePressed() {
        MinecraftClient.getInstance().setScreen(new MainConfigScreen());
        return true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        var text = MinecraftClient.getInstance().textRenderer;

        context.drawCenteredTextWithShadow(text, "Modules Config", width/2, height/2 - 35, 0xFFFFFFFF);

        for(var button : moduleButtons) {
            button.render(context, mouseX, mouseY, delta);
        }

        backButton.render(context, mouseX, mouseY, delta);
    }
}
