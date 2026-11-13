/*
 * BBS_Invisible - Copyright (C) 2026 遇见
 * Licensed under the GNU Lesser General Public License v3.0 or later.
 * See the LICENSE file in the project root.
 */
package bbs.invisible.mixin;

import bbs.invisible.render.Transparency;
import mchorse.bbs_mod.client.BBSRendering;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * BBS 每帧把世界画面 blit 进导出贴图，再由它截图 / 录像。
 * 在 blit 之前把这张贴图换成带 alpha 的 RGBA（或换回 RGB）。
 */
@Mixin(value = BBSRendering.class, remap = false)
public class BBSRenderingMixin
{
    @Inject(method = "onRenderBeforeScreen", at = @At("HEAD"), remap = false)
    private static void bbsfs$syncExportTexture(CallbackInfo ci)
    {
        Transparency.syncExportTexture();
    }
}
