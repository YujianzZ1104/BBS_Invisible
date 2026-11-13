/*
 * BBS_Invisible - Copyright (C) 2026 遇见
 * Licensed under the GNU Lesser General Public License v3.0 or later.
 * See the LICENSE file in the project root.
 */
package bbs.invisible.mixin;

import bbs.invisible.render.CloudFade;
import bbs.invisible.render.Transparency;
import mchorse.bbs_mod.blocks.entities.ModelBlockEntity;
import mchorse.bbs_mod.client.renderer.ModelBlockEntityRenderer;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * BBS 模型方块是立即绘制的，所以可以在每个模型方块前后拷贝 / 混合画面实现渐隐。
 */
@Mixin(value = ModelBlockEntityRenderer.class, remap = false)
public class ModelBlockEntityRendererMixin
{
    private static final String OWNER = "model_block";
    private static final String TARGET = "render(Lmchorse/bbs_mod/blocks/entities/ModelBlockEntity;FLnet/minecraft/class_4587;Lnet/minecraft/class_4597;II)V";

    @Inject(method = TARGET, at = @At("HEAD"), remap = false, require = 0)
    private void bbsfs$fadeBefore(ModelBlockEntity entity, float tickDelta, class_4587 matrices, class_4597 vertexConsumers, int light, int overlay, CallbackInfo ci)
    {
        float amount = Transparency.modelBlocksAmount();

        if (amount > 0F && amount < 1F)
        {
            CloudFade.beginFor(OWNER, amount);
        }
    }

    @Inject(method = TARGET, at = @At("RETURN"), remap = false, require = 0)
    private void bbsfs$fadeAfter(ModelBlockEntity entity, float tickDelta, class_4587 matrices, class_4597 vertexConsumers, int light, int overlay, CallbackInfo ci)
    {
        CloudFade.endFor(OWNER);
    }
}
