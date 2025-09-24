package org.loveroo.fireclient.screen.widgets;

import java.util.UUID;
import java.util.function.Consumer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PlayerHeadWidget extends ClickableWidget {

    private final String playerName;
    private Identifier texture = Identifier.ofVanilla("textures/entity/player/wide/steve.png");

    public PlayerHeadWidget(String name, UUID uuid, int x, int y) {
        super(x, y, 16, 16, Text.literal(name));
        this.playerName = name;

        var thread = new ProfileFetchThread(uuid, (newTexture) -> { texture = newTexture; });
        thread.start();
    }

    public PlayerHeadWidget(String name, Identifier texture, int x, int y) {
        super(x, y, 16, 16, Text.literal(name));
        this.playerName = name;

        this.texture = texture;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        PlayerSkinDrawer.draw(context, texture, getX(), getY(), 12, true, false, 0xFFFFFFFF);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, playerName);
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
            var client = MinecraftClient.getInstance();
            var session = client.getSessionService();
            var skinProvider = client.getSkinProvider();

            if(uuid == null) {
                return;
            }

            var profileResult = session.fetchProfile(uuid, false);
            if(profileResult == null || profileResult.profile() == null) {
                return;
            }

            var profile = profileResult.profile();

            skinProvider.fetchSkinTextures(profile).thenAccept((head) -> {
                if(!head.isPresent()) {
                    return;
                }

                afterFetch.accept(head.get().texture());
            });
        }
    }
}
