package org.loveroo.fireclient.screen.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.loveroo.fireclient.modules.ModuleBase;

import java.util.ArrayList;

public class FireClientSettingsScreen extends ConfigScreenBase {

    private ArrayList<ButtonWidget> settingsButtons;
    private ButtonWidget backButton;

    public FireClientSettingsScreen() {
        super(Text.of("FireClient Options"));
    }

    @Override
    public void init() {
        settingsButtons = new ArrayList<>();

        for(var i = 0; i < FireClientOption.values().length; i++) {
            var option = FireClientOption.values()[i];

            var x = ((i % 3) - 1) * 145;
            var y = (i / 3) * 30;

            settingsButtons.add(ButtonWidget.builder(getOptionLabel(option), (button) -> handleSettings(button, option))
                    .dimensions(width/2 - 65 + x, height/2 - 20 + y, 130, 20)
                    .tooltip(Tooltip.of(Text.of(option.getDescription())))
                    .build());

            addSelectableChild(settingsButtons.get(i));
        }

        backButton = ButtonWidget.builder(Text.of("Back"), this::backButtonPressed)
                .dimensions(width/2 - 60, height/2 + - 20 +  (FireClientside.getModules().size() / 3 + 1) * 30, 120, 20)
                .build();

        addSelectableChild(backButton);
    }

    private void backButtonPressed(ButtonWidget button) {
        MinecraftClient.getInstance().setScreen(new MainConfigScreen());
    }

    private void handleSettings(ButtonWidget button, FireClientOption option) {
        switch(option.getType()) {
            case TOGGLE -> {
                var value = (FireClientside.getSetting(option) == 0) ? 1 : 0;
                FireClientside.setSetting(option, value);

                button.setMessage(getOptionLabel(option));
            }
        }
    }

    private Text getOptionLabel(FireClientOption option) {
        switch(option.getType()) {
            case TOGGLE -> {
                var value = FireClientside.getSetting(option);
                return Text.of((value == 1 ? "✔ " : "❌ ") + (option.getName()));
            }
        }

        return Text.of(option.getName());
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

        context.drawCenteredTextWithShadow(text, "Settings", width/2, height/2 - 35, 0xFFFFFFFF);

        for(var button : settingsButtons) {
            button.render(context, mouseX, mouseY, delta);
        }

        backButton.render(context, mouseX, mouseY, delta);
    }
}
