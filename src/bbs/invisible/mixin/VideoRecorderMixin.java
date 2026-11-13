/*
 * BBS_Invisible - Copyright (C) 2026 遇见
 * Licensed under the GNU Lesser General Public License v3.0 or later.
 * See the LICENSE file in the project root.
 */
package bbs.invisible.mixin;

import bbs.invisible.render.Transparency;
import java.io.File;
import mchorse.bbs_mod.BBSSettings;
import mchorse.bbs_mod.settings.values.core.ValueString;
import mchorse.bbs_mod.utils.VideoRecorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * BBS 的 VideoRecorder 用 GL_BGR（3 字节/像素）读帧再喂给 ffmpeg，没有 alpha。
 * 透明模式下改成 GL_BGRA（4 字节/像素）并换成带透明通道的 ffmpeg 参数。
 */
@Mixin(value = VideoRecorder.class, remap = false)
public class VideoRecorderMixin
{
    @Inject(method = "startRecording", at = @At("HEAD"), remap = false)
    private void bbsfs$latch(String movieName, File audioFile, int textureId, int width, int height, CallbackInfo ci)
    {
        if (!((VideoRecorder) (Object) this).isRecording())
        {
            Transparency.latch();
        }
    }

    /* int size = width * height * 3  ->  * 4 */
    @ModifyConstant(method = "startRecording", constant = @Constant(intValue = 3), remap = false)
    private int bbsfs$bytesPerPixel(int original)
    {
        return Transparency.isLatchedAlpha() ? 4 : original;
    }

    /* GL_BGR (32992) -> GL_BGRA (32993) */
    @ModifyConstant(method = {"recordFramePBO", "recordFrameDirect"}, constant = @Constant(intValue = 32992), remap = false)
    private int bbsfs$pixelFormat(int original)
    {
        return Transparency.isLatchedAlpha() ? 32993 : original;
    }

    /* 视频设置里的 ffmpeg 参数（带音频 / 不带音频两处读取） */
    @Redirect(method = "startRecording", at = @At(value = "INVOKE", target = "Lmchorse/bbs_mod/settings/values/core/ValueString;get()Ljava/lang/Object;"), remap = false)
    private Object bbsfs$arguments(ValueString value)
    {
        if (Transparency.isLatchedAlpha())
        {
            return Transparency.encoderArguments(value == BBSSettings.videoArgumentsAudio);
        }

        return value.get();
    }
}
