/*
 * BBS_Invisible - Copyright (C) 2026 遇见
 * Licensed under the GNU Lesser General Public License v3.0 or later.
 * See the LICENSE file in the project root.
 */
package bbs.invisible.mixin;

import bbs.invisible.render.CloudFade;
import bbs.invisible.render.Transparency;
import mchorse.bbs_mod.client.BBSRendering;
import net.minecraft.class_1297;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_898;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 实体渲染：去除程度 >= 1 时跳过；在 0~1 之间时，画这个实体前后拷贝 / 混合画面实现渐隐。
 */
@Mixin(value = class_898.class, priority = 2000)
public class EntityRenderDispatcherMixin
{
    private static final String OWNER = "entity";

    @Inject(method = "method_3954", at = @At("HEAD"), cancellable = true, require = 0)
    private void bbsfs$hideEntity(class_1297 entity, double x, double y, double z, float yaw, float tickDelta, class_4587 matrices, class_4597 vertexConsumers, int light, CallbackInfo ci)
    {
        float amount = Transparency.entityAmount(entity);

        if (amount >= 1F || (amount > 0F && !Transparency.canFlush() && amount >= 0.5F))
        {
            ci.cancel();

            return;
        }

        if (amount > 0F && Transparency.canFlush() && !BBSRendering.isIrisShadowPass())
        {
            /* 先把之前攒着的顶点画掉，这样快照里不含这个实体 */
            if (Transparency.flush(vertexConsumers))
            {
                CloudFade.beginFor(OWNER, amount);
            }
        }
    }

    @Inject(method = "method_3954", at = @At("RETURN"), require = 0)
    private void bbsfs$fadeEntity(class_1297 entity, double x, double y, double z, float yaw, float tickDelta, class_4587 matrices, class_4597 vertexConsumers, int light, CallbackInfo ci)
    {
        if (CloudFade.holds(OWNER))
        {
            Transparency.flush(vertexConsumers);
            CloudFade.endFor(OWNER);
        }
    }
}
