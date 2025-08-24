package org.loveroo.fireclient.mixin.settings;

import net.minecraft.client.gui.hud.ChatHud;
import org.loveroo.fireclient.client.FireClientside;
import org.loveroo.fireclient.data.FireClientOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ChatHud.class)
public class ChatLengthMixin {

    @ModifyConstant(method = "addToMessageHistory", constant = @Constant(intValue = 100))
    private int modify1(int original) {
        return getLength();
    }

    @ModifyConstant(method = "addVisibleMessage", constant = @Constant(intValue = 100))
    private int modify2(int original) {
        return getLength();
    }

    @ModifyConstant(method = "addMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V", constant = @Constant(intValue = 100))
    private int modify3(int original) {
        return getLength();
    }

    @Unique
    private int getLength() {
        var length = FireClientside.getSetting(FireClientOption.CHAT_HISTORY);

        if((length >= ((FireClientOption.SliderOptionData)FireClientOption.CHAT_HISTORY.getData()).getMaxValue())) {
            return Integer.MAX_VALUE;
        }
        else {
            return length;
        }
    }
}
