/*
 * BBS_Invisible - Copyright (C) 2026 遇见
 * Licensed under the GNU Lesser General Public License v3.0 or later.
 * See the LICENSE file in the project root.
 */
package bbs.invisible.mixin;

import bbs.invisible.render.AlphaFix;
import bbs.invisible.render.CloudFade;
import bbs.invisible.render.TerrainFade;
import bbs.invisible.render.Transparency;
import mchorse.bbs_mod.client.BBSRendering;
import net.minecraft.class_1921;
import net.minecraft.class_4587;
import net.minecraft.class_761;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 地形渐隐的「画完之后」部分。priority 比 BBS 低，这样本次混合先于 BBS 在同一层结尾画模型，
 * 模型不会跟着地形一起变淡。
 */
@Mixin(value = class_761.class, priority = 500)
public class WorldRendererFadeMixin
{
    @Inject(method = "method_3251(Lnet/minecraft/class_1921;Lnet/minecraft/class_4587;DDDLorg/joml/Matrix4f;)V", at = @At("TAIL"), require = 0)
    private void bbsfs$fadeTerrain(class_1921 layer, class_4587 matrices, double x, double y, double z, Matrix4f projectionMatrix, CallbackInfo ci)
    {
        float terrain = Transparency.terrainAmount();

        if (terrain <= 0F || terrain >= 1F || BBSRendering.isIrisShadowPass() || !TerrainFade.isFadeLayer(layer))
        {
            return;
        }

        if (TerrainFade.isOpaqueLayer(layer))
        {
            AlphaFix.run();
        }

        CloudFade.end(terrain);
    }
}
