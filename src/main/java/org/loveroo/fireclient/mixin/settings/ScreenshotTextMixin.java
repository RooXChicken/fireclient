package org.loveroo.fireclient.mixin.settings;

import java.io.File;

import org.loveroo.fireclient.data.Color;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.Screenshot;

@Mixin(Screenshot.class)
public abstract class ScreenshotTextMixin {

    @Shadow
    private static File getFile(File picDir) {
        return picDir;
    }

    @Unique
    private static final Color color1 = Color.fromRGB(0xBFFFB0);

    @Unique
    private static final Color color2 = Color.fromRGB(0x82D96C);

//    @ModifyVariable(method = "saveScreenshot(Ljava/io/File;Ljava/lang/String;Lnet/minecraft/client/gl/Framebuffer;ILjava/util/function/Consumer;)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
//    private static Consumer<Text> modifyMessage(Consumer<Text> original, @Local(ordinal = 0, argsOnly = true) File file) {
//        if(FireClientside.getSetting(FireClientOption.EXTRA_SCREENSHOT_OPTIONS) == 0) {
//            return original;
//        }
//
//        return (message) -> {
//            var screenshotFile = getScreenshotFilename(file);
//            var copyText = RooHelper.gradientText(Text.translatable("fireclient.settings.extra_screenshot_options.copy.message").getString(), color1, color2);
//
//            // TODO: completely borked
//            var clipboard = "ERROR";
//            try {
//                clipboard = Files.readString(Paths.get(screenshotFile.toURI()));
//            }
//            catch(Exception e) {}
//
//            var click = new ClickEvent.CopyToClipboard(clipboard);
//            var hover = new HoverEvent.ShowText(Text.translatable("fireclient.settings.extra_screenshot_options.copy.hover", screenshotFile.getName()));
//
//            var copyClickable = copyText.setStyle(copyText.getStyle().withClickEvent(click).withHoverEvent(hover));
//
//            var newMessage = message.copy().append(" ").append(copyClickable);
//            original.accept(newMessage);
//        };
//    }
}
