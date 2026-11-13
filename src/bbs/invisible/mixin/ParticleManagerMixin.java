/*
 * BBS_Invisible - Copyright (C) 2026 遇见
 * Licensed under the GNU Lesser General Public License v3.0 or later.
 * See the LICENSE file in the project root.
 */
package bbs.invisible.mixin;

import bbs.invisible.render.CloudFade;
import bbs.invisible.render.Transparency;
import net.minecraft.class_702;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 粒子渲染：去除程度 >= 1 时跳过；0~1 之间时在整个粒子渲染前后拷贝 / 混合画面实现渐隐。
 */
@Mixin(value = class_702.class, priority = 2000)
public class ParticleManagerMixin
{
    private static final String OWNER = "particles";

    @Inject(method = "method_3049", at = @At("HEAD"), cancellable = true, require = 0)
    private void bbsfs$hideParticles(CallbackInfo ci)
    {
        float amount = Transparency.particlesAmount();

        if (amount >= 1F)
        {
            ci.cancel();
        }
        else if (amount > 0F)
        {
            CloudFade.beginFor(OWNER, amount);
        }
    }

    @Inject(method = "method_3049", at = @At("RETURN"), require = 0)
    private void bbsfs$fadeParticles(CallbackInfo ci)
    {
        CloudFade.endFor(OWNER);
    }
}
