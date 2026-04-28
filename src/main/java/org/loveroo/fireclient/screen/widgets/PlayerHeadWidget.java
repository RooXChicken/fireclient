package org.loveroo.fireclient.screen.widgets;

import java.util.UUID;
import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class PlayerHeadWidget extends AbstractWidget {

    private final String playerName;
    private Identifier texture = Identifier.withDefaultNamespace("textures/entity/player/wide/steve.png");

    public PlayerHeadWidget(String name, UUID uuid, int x, int y) {
        super(x, y, 16, 16, Component.literal(name));
        this.playerName = name;

        var thread = new ProfileFetchThread(uuid, (newTexture) -> { texture = newTexture; });
        thread.start();
    }

    public PlayerHeadWidget(String name, Identifier texture, int x, int y) {
        super(x, y, 16, 16, Component.literal(name));
        this.playerName = name;

        this.texture = texture;
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        PlayerFaceExtractor.extractRenderState(graphics, texture, getX(), getY(), 12, true, false, 0xFFFFFFFF);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
        builder.add(NarratedElementType.TITLE, playerName);
    }

    static class ProfileFetchThread extends Thread {

        private final UUID uuid;
        private final Consumer<Identifier> afterFetch;

        public ProfileFetchThread(UUID uuid, Consumer<Identifier> afterFetch) {
            this.uuid = uuid;
            this.afterFetch = afterFetch;
        }

        @Override
        public void run() {
            var client = Minecraft.getInstance();
            var session = client.services();
            var skinProvider = client.getSkinManager();

            if(uuid == null) {
                return;
            }

            var profile = session.profileResolver().fetchById(uuid).orElse(null);
            if(profile == null) {
                return;
            }

            skinProvider.get(profile).thenAccept((head) -> {
                if(!head.isPresent()) {
                    return;
                }

                afterFetch.accept(head.get().body().texturePath());
            });
        }
    }
}
