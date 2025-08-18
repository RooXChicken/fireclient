package org.loveroo.fireclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.data.Color;
import org.loveroo.fireclient.data.ModuleData;

import java.util.ArrayList;
import java.util.List;

public class LocalDifficultyFinderModule extends ModuleBase {

    private final Color color1 = new Color(230, 137, 25, 255);
    private final Color color2 = new Color(240, 204, 79, 255);

//    private float approximation = 0;
    private int fireTicks = 0;
    private int maxFire = 0;

    private Difficulty worldDifficulty = Difficulty.NORMAL;
    private long timeOfDay = 0;
    private float moonSize = 0;

    private float ldApprox = 0;
    private int serverLdFromFire = 0;
//    private float ldDiff = 0;

    public LocalDifficultyFinderModule() {
        super(new ModuleData("local_difficulty_finder", "\uD83D\uDCA5 Local Difficulty", "A local difficulty calculator based on Zombie Fire Ticks"));
        getData().setShownName(generateDisplayName(0xFFDEA1));

        getData().setHeight(16);
        getData().setWidth(60);

        getData().setPosX(4, 640);
        getData().setPosY(42, 360);
    }

    @Override
    public void update(MinecraftClient client) {
        if(client.player == null) {
            return;
        }

        if(client.player.isOnFire()) {
            if(fireTicks == 0) {
                worldDifficulty = client.player.clientWorld.getDifficulty();
                timeOfDay = client.player.clientWorld.getTimeOfDay();
                moonSize = client.player.clientWorld.getMoonSize();
            }

            fireTicks++;
        }
        else {
            if(fireTicks != 0) {
                maxFire = fireTicks;

                serverLdFromFire = (int)Math.ceil(maxFire / 40f);

                var approx = 0.75f;
                var timeOffset = MathHelper.clamp(((float)timeOfDay + -72000.0F) / 1440000.0F, 0.0f, 1.0f) * 0.25f;

                approx += timeOffset;

                var moonOffset = MathHelper.clamp(moonSize * 0.25f, 0.0f, timeOffset);
                if(worldDifficulty == Difficulty.EASY) {
                    moonOffset *= 0.5f;
                }

                ldApprox = (approx + moonOffset) * worldDifficulty.getId();
//
//                ldDiff = serverLdFromFire - (int)ldApprox;
//                var newDiff = (ldDiff / worldDifficulty.getId());
//
//                var inhabitedMoonOffset = timeDiff - newDiff;
//
//                if(worldDifficulty == Difficulty.EASY) {
//                    inhabitedMoonOffset *= 2;
//                    moonOffset *= 2.0f;
//                }
//
//                var inhabitedApprox = inhabitedMoonOffset - moonOffset;
//                if(worldDifficulty != Difficulty.HARD) {
//                    inhabitedApprox /= 0.75f;
//                }
//
//                approximation = inhabitedApprox * 3600000f;
            }

            fireTicks = 0;
        }
    }

    @Override
    public List<ClickableWidget> getConfigScreen(Screen base) {
        var widgets = new ArrayList<ClickableWidget>();

        widgets.add(getToggleVisibleButton(base.width/2 - 60, base.height/2 - 10));

        return widgets;
    }

    @Override
    public void draw(DrawContext context, RenderTickCounter ticks) {
        if(!canDraw()) {
            return;
        }

        transform(context.getMatrices());

        var client = MinecraftClient.getInstance();
        var text = client.textRenderer;

        var fireMsg = ((fireTicks != 0) ? fireTicks : maxFire) + " ticks";
        var approxMsg = "LD: " + ldApprox + " SD: " + serverLdFromFire;

        var fireText = RooHelper.gradientText(fireMsg, color1, color2);
        var approxText = RooHelper.gradientText(approxMsg, color1, color2);

        getData().setWidth(text.getWidth(approxText));

        context.drawText(text, fireText, 0, 0, 0xFFFFFFFF, true);
        context.drawText(text, approxText, 0, 10, 0xFFFFFFFF, true);

        endTransform(context.getMatrices());
    }
}
