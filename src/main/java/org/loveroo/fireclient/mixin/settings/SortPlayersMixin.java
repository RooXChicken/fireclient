package org.loveroo.fireclient.mixin.settings;

import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.text.OrderedText;
import org.loveroo.fireclient.FireClient;
import org.loveroo.fireclient.RooHelper;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.loveroo.fireclient.settings.PlayerSortPriority;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(ChatInputSuggestor.class)
public class SortPlayersMixin {

    @Inject(method = "sortSuggestions", at = @At("RETURN"), cancellable = true)
    private void sortPlayers(Suggestions old, CallbackInfoReturnable<List<Suggestion>> info) {
        if(FireClientside.getSetting(FireClientOption.PRIORITIZE_PLAYERS) == 0) {
            return;
        }

        var players = RooHelper.getNetworkHandler().getPlayerList().stream()
                .map((entry) -> entry.getProfile().getName().toLowerCase())
                .collect(Collectors.toUnmodifiableSet());

        var sorted = new ArrayList<>(info.getReturnValue()).stream()
                .sorted((suggestion1, suggestion2) -> {
                    var text1 = suggestion1.getText();
                    var text2 = suggestion2.getText();

                    var text1Lower = text1.toLowerCase();
                    var text2Lower = text2.toLowerCase();

                    var isPlayer1 = players.contains(text1Lower);
                    var isPlayer2 = players.contains(text2Lower);

                    if(!isPlayer1 || !isPlayer2) {
                        return text1.compareTo(text2);
                    }

                    var usages1 = PlayerSortPriority.getUsages(text1Lower);
                    var usages2 = PlayerSortPriority.getUsages(text2Lower);

                    return (Integer.compare(usages2, usages1));
                })
                .toList();

        info.setReturnValue(sorted);
    }
}
